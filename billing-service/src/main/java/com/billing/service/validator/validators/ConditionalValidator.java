package com.billing.service.validator.validators;

import com.billing.service.validator.Conditional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;

import static org.springframework.util.ObjectUtils.isEmpty;

@Log4j2
public class ConditionalValidator implements ConstraintValidator<Conditional, Object> {

    private String selected;
    private String[] required;
    private String message;
    private String[] values;

    @Override
    public void initialize(Conditional requiredIfChecked) {
        selected = requiredIfChecked.selected();
        required = requiredIfChecked.required();
        message = requiredIfChecked.message();
        values = requiredIfChecked.values();
    }

    @Override
    public boolean isValid(Object objectToValidate, ConstraintValidatorContext context) {
        log.debug("Checking if condition is valid");
        if (objectToValidate == null) {
            return true; // Don't fail on null object
        }
        Boolean valid = true;
        BeanWrapper beanWrapper = new BeanWrapperImpl(objectToValidate);
        Object actualValue = null;
        try {
            actualValue = beanWrapper.getPropertyValue(selected);
        } catch (Exception e) {
            log.warn("Could not get property '{}' from object: {}", selected, e.getMessage());
            return true; // Don't fail if property is missing
        }
        if (Arrays.asList(values).contains(actualValue)) {
            log.debug("Condition is valid");
            for (String propName : required) {
                beanWrapper = new BeanWrapperImpl(objectToValidate);
                Object requiredValue = null;
                try {
                    requiredValue = beanWrapper.getPropertyValue(propName);
                } catch (Exception e) {
                    log.warn("Could not get required property '{}' from object: {}", propName, e.getMessage());
                    requiredValue = null;
                }
                valid = requiredValue != null && !isEmpty(requiredValue);
                System.out.println("value: " + "" + requiredValue);
                if (!valid) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message).addPropertyNode(propName).addConstraintViolation();
                }
            }
        }
        return valid;
    }
}