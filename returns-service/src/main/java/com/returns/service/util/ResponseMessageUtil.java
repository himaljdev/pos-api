/**
 * User: Himal_J
 * Date: 2/3/2025
 * Time: 10:26 AM
 * <p>
 */

package com.returns.service.util;

public class ResponseMessageUtil {

    /* Cashier */
    public static final String CASHIER_USER_NOT_FOUND = "val.cashier.user.not.found";

    /* Billing */
    public static final String STOCK_NOT_FOUND = "val.stock.not.found";
    public static final String INSUFFICIENT_STOCK = "val.insufficient.stock";
    public static final String ITEM_COST_INVALID = "val.item.cost.invalid";
    public static final String TOTAL_AMOUNT_INVALID = "val.total.amount.invalid";
    public static final String CASHIER_NOT_FOUND = "val.cashier.not.found";
    public static final String CUSTOMER_NOT_FOUND = "val.customer.not.found";
    public static final String BILLING_PROCESSED_SUCCESS = "val.billing.processed.success";
    public static final String BILLING_STOCK_FILTER_LIST_SUCCESS = "val.billing.stock.filter.list.success";
    public static final String BILLING_REFERENCE_DATE_SUCCESS = "val.billing.reference.date.success";

    /* JWT/Auth */
    public static final String JWT_INVALID_TOKEN = "val.jwt.invalid.token";
    public static final String JWT_VALIDATION_FAILED = "val.jwt.validation.failed";
    public static final String JWT_USERNAME_MISMATCH = "val.jwt.username.mismatch";
    public static final String JWT_TOKEN_MISMATCH = "val.jwt.token.mismatch";
    public static final String JWT_INTERNAL_ERROR = "val.jwt.internal.error";

    /* Cash In/Out */
    public static final String CASH_IN_OUT_ADDED_SUCCESSFULLY = "val.cash.in.out.added.success";
    public static final String CASH_IN_OUT_RETRIEVE_SUCCESSFULLY = "val.cash.in.out.retrieve.success";

    /*Customer return*/
    public static final String BILLING_FILTER_LIST_SUCCESS = "val.billing.filter.list.success";
    public static final String BILLING_NOT_FOUND = "val.billing.not.found";
    public static final String BILLING_DETAIL_RETRIEVED_SUCCESS = "val.billing.details.retrieved.success";
}
