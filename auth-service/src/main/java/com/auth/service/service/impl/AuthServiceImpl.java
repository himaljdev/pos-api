package com.auth.service.service.impl;

import com.auth.service.dto.request.LoginRequestDTO;
import com.auth.service.dto.response.ApiResponse;
import com.auth.service.dto.response.CashierUserResponseDTO;
import com.auth.service.enums.Channel;
import com.auth.service.enums.Status;
import com.auth.service.model.CashInOut;
import com.auth.service.model.CashierUser;
import com.auth.service.model.Token;
import com.auth.service.repository.CashInOutRepository;
import com.auth.service.repository.CashierUserRepository;
import com.auth.service.repository.PasswordPolicyRepository;
import com.auth.service.repository.TokenRepository;
import com.auth.service.service.AuthService;
import com.auth.service.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final CashierUserRepository cashierUserRepository;
    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;
    private final PasswordPolicyRepository passwordPolicyRepository;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final CashInOutRepository cashInOutRepository;

    @Cacheable(value = "isOpening", key = "#cashier + ':' + #startdate + ':' + #endDate + ':' +#cashInOut")
    public CashInOut isOpening(String cashier, Date startDate, Date endDate, com.auth.service.enums.CashInOut cashInOut) {
        return cashInOutRepository.findTopByCashierUser_UsernameAndCreatedDateBetweenAndCashInOutOrderByCreatedDateDesc(cashier, startDate, endDate, cashInOut);
    }


    /**
     * Handles user login logic: validates credentials, checks status, password expiry, and attempts, generates JWT, and updates user state.
     * @param loginRequestDTO login request data
     * @param locale locale for messages
     * @return ResponseEntity with ApiResponse
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Object>> login(LoginRequestDTO loginRequestDTO, Locale locale) throws NoSuchAlgorithmException {
        try {
            log.info("Login request for username: {}", loginRequestDTO.getUsername());

            String username = loginRequestDTO.getUsername();
            String password = loginRequestDTO.getPassword();

            // Validate input
            if (username == null || password == null) {
                return unauthorizedResponse(ResponseMessageUtil.USERNAME_PASSWORD_INVALID, 1001, locale);
            }

            // Find user by username and status
            Optional<CashierUser> userOpt = cashierUserRepository.findByUsername(username);

            if (userOpt.isEmpty()) {
                log.info("User not found: {}", username);
                return unauthorizedResponse(ResponseMessageUtil.USERNAME_PASSWORD_INVALID, 1001, locale);
            }

            CashierUser cashierUser = userOpt.get();

            // Combine userKey and password for BCrypt check
            String encodedPassword = PasswordUtil.passwordEncoder(cashierUser.getUserKey(),password);

            log.info("Encrypted password: {}", encodedPassword);

            if (!encodedPassword.equals(cashierUser.getPassword())) {
                log.info("Invalid password for user: {}", username);
                updateWrongAttempt(cashierUser);
                return unauthorizedResponse(ResponseMessageUtil.USERNAME_PASSWORD_INVALID, 1001, locale);
            }

            int attemptExceedCount = getAttemptExceedCount();

            // Check if user is in reset or inactive state
            if (cashierUser.isReset() || cashierUser.getStatus() == Status.INACTIVE) {
                log.info("User in reset or inactive state: {}", username);
                return response(ResponseMessageUtil.STATUS_INACTIVE_OR_EXPECTED_RESET, 1002, locale);
            }

            // Check if password is expired
            if (cashierUser.getPasswordExpiredDate().before(DateTimeUtil.getCurrentDateTime())) {
                log.info("Password expired for user: {}", username);
                updateLoginStatus(cashierUser);
                return response(ResponseMessageUtil.PASSWORD_EXPIRED_AT_LOGIN_TIME, 1003, locale);
            }

            // Check if login attempts exceeded
            if (attemptExceedCount > 0 && cashierUser.getAttemptCount() > attemptExceedCount) {
                log.info("Login attempt exceeded for user: {} {} attempts)", username, cashierUser.getAttemptCount());
                updateLoginStatus(cashierUser);
                return response(ResponseMessageUtil.PASSWORD_ATTEMPT_EXCEED, 1004, locale);
            }

            // Generate JWT token
            String accessToken = jwtUtil.generateToken(username);
            log.info("JWT token generated for user {}", username);

            // Save token and update user login state
            Token token = saveNewToken(accessToken,loginRequestDTO);
            updateSuccessLoginBefore(cashierUser, loginRequestDTO, token);
            CashierUserResponseDTO responseDTO = modelMapper.map(cashierUser, CashierUserResponseDTO.class);
            responseDTO.setStatusDescription(Status.valueOf(responseDTO.getStatus()).getDescription());
            responseDTO.setAccessToken(accessToken);
            updateSuccessLoginAfter(cashierUser, loginRequestDTO, token);
            boolean opening = getOpening(cashierUser);
            responseDTO.setOpening(opening);
            return ResponseEntity.ok().body(responseUtil.success(responseDTO, messageSource.getMessage(ResponseMessageUtil.AUTHENTICATION_SUCCESS, null, locale)));

        } catch (Exception e) {
            log.error("Login error", e);
            throw e;
        }
    }

    private boolean getOpening(CashierUser cashierUser){
        try {
            log.info("Getting opening balance");
            Date startOfToday = DateTimeUtil.getStartOfToday();
            Date endOfToday = DateTimeUtil.getEndOfToday();

            CashInOut latestOp = isOpening(
                    cashierUser.getUsername(), startOfToday, endOfToday, com.auth.service.enums.CashInOut.OP);
            CashInOut latestCl = isOpening(
                    cashierUser.getUsername(), startOfToday, endOfToday, com.auth.service.enums.CashInOut.CL);

            boolean hasOpenedToday = latestOp != null;
            boolean hasClosedToday = latestCl != null;

            // Check timestamps if both OP and CL exist
            boolean needsOpening = false;
            if (hasOpenedToday && hasClosedToday) {
                Date latestOpTime = latestOp.getCreatedDate();
                Date latestClTime = latestCl.getCreatedDate();

                // Example: OP at 2025/07/27 10:00 AM, CL at 2025/07/27 10:30 AM
                // If CL is more recent than OP, cashier has closed after opening, so needs new opening
                needsOpening = latestClTime.after(latestOpTime);
                log.info("Latest OP time: {}, Latest CL time: {}, Needs opening: {}",
                        latestOpTime, latestClTime, needsOpening);
            } else if (!hasOpenedToday) {
                // If no OP today, cashier needs to open
                needsOpening = true;
                log.info("No opening transaction found today, needs opening: {}", needsOpening);
            } else if (hasOpenedToday && !hasClosedToday) {
                // If OP exists but no CL, cashier is still open, no need for new opening
                needsOpening = false;
                log.info("Opened today but not closed, needs opening: {}", needsOpening);
            }
            
            log.info("Opening balance check - Has opened today: {}, Has closed today: {}, Needs opening: {}", 
                    hasOpenedToday, hasClosedToday, needsOpening);
            
            return needsOpening;
        }catch (Exception e){
            log.error(e);
            throw e;
        }
    }

    /**
     * Returns an unauthorized response with a localized message.
     */
    private ResponseEntity<ApiResponse<Object>> unauthorizedResponse(String messageKey, int code, Locale locale) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(responseUtil.error(null, code, messageSource.getMessage(messageKey, null, locale)));
    }

    /**
     * Returns a response with a localized message.
     */
    private ResponseEntity<ApiResponse<Object>> response(String messageKey, int code, Locale locale) {
        return ResponseEntity.ok()
                .body(responseUtil.error(null, code, messageSource.getMessage(messageKey, null, locale)));
    }

    /**
     * Saves a new JWT token to the database.
     */
    @Transactional
    protected Token saveNewToken(String accessToken, LoginRequestDTO loginRequestDTO) {
        try {
            log.info("Processing success access token ");
            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setCreatedBy(loginRequestDTO.getUsername());
            token.setCreatedDate(DateTimeUtil.getCurrentDateTime());
            token.setLastModifiedBy(loginRequestDTO.getUsername());
            token.setLastModifiedDate(DateTimeUtil.getCurrentDateTime());
            log.info("After success saved token");
            return tokenRepository.saveAndFlush(token);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Updates user state before successful login.
     */
    @Transactional
    protected void updateSuccessLoginBefore(CashierUser cashierUser, LoginRequestDTO loginRequestDTO, Token token) {
        try {
            log.info("Processing success login request username {}", loginRequestDTO.getUsername());
            cashierUser.setStatus(Status.ACTIVE);
            cashierUser.setPasswordExpiredDate(DateTimeUtil.get30FutureDate());
            cashierUser.setToken(token);
            cashierUserRepository.saveAndFlush(cashierUser);
            log.info("After success login request update data username {}", loginRequestDTO.getUsername());
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Updates user state after successful login.
     */
    @Transactional
    protected void updateSuccessLoginAfter(CashierUser cashierUser, LoginRequestDTO loginRequestDTO, Token token) {
        try {
            log.info("Processing success login request username after {}", loginRequestDTO.getUsername());
            cashierUser.setLastLoggedChannel(Channel.valueOf(loginRequestDTO.getChannel()));
            cashierUser.setLastLoggedDate(DateTimeUtil.getCurrentDateTime());
            cashierUser.setAttemptCount(0);
            if (loginRequestDTO.getChannel().equals(Channel.MB.name())) {
                cashierUser.setMbLastLoggedDate(DateTimeUtil.getCurrentDateTime());
            } else {
                cashierUser.setOpLastLoggedDate(DateTimeUtil.getCurrentDateTime());
            }
            cashierUserRepository.saveAndFlush(cashierUser);
            log.info("After success login request update data username after {}", loginRequestDTO.getUsername());
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Updates user status to inactive and sets reset flag after failed login attempts or password expiry.
     */
    @Transactional
    protected void updateLoginStatus(CashierUser cashierUser) {
        try {
            log.info("Processing update password login request  username {} attempt {}  before",
                    cashierUser.getUsername(), cashierUser.getAttemptCount());
            cashierUser.setStatus(Status.INACTIVE);
            cashierUser.setReset(true);
            cashierUserRepository.saveAndFlush(cashierUser);
            log.info("After update password login request update data username before {}", cashierUser.getUsername());
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Increments the user's failed login attempt count.
     */
    @Transactional
    protected void updateWrongAttempt(CashierUser user) {
        try {
            log.info("Processing wrong login request  username {} attempt {} ",
                    user.getUsername(), user.getAttemptCount());
            user.setAttemptCount(user.getAttemptCount() + 1);
            cashierUserRepository.saveAndFlush(user);
            log.info("After wrong login request update data username {}", user.getUsername());
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Retrieves the maximum allowed login attempts from password policy.
     */
    private Integer getAttemptExceedCount() {
        try {
            log.info("Fetching password policy attempt count");
            return passwordPolicyRepository.findTopByOrderByIdAsc()
                    .orElseThrow(() -> new IllegalStateException("Password policy not found"))
                    .getAttemptExceedCount();
        } catch (Exception e) {
            log.error("Error fetching attempt exceed count", e);
            throw e;
        }
    }

}
