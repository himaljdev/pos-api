/**
 * User: Himal_J
 * Date: 2/3/2025
 * Time: 10:26 AM
 * <p>
 */

package com.billing.service.util;

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
    public static final String LATEST_BILLING_RETRIEVE_SUCCESS = "val.billing.retrieve.success";
    public static final String TODAY_SALES_FILTER_LIST_SUCCESS = "val.today.sales.filter.list.success";
    public static final String BILLING_NOT_FOUND = "val.billing.not.found";
    public static final String BILLING_ITEM_RETRIEVE_SUCCESS = "val.billing.item.retrieve.success";
    public static final String CHECKOUT_TOKEN_GENERATE_SUCCESS = "val.checkout.token.generate.success";
    public static final String CHECKOUT_TOKEN_NOT_FOUND = "val.checkout.token.not.found";
    public static final String DUPLICATE_BILLING_FOUND = "val.duplicate.billing.found";

    /* JWT/Auth */
    public static final String JWT_INVALID_TOKEN = "val.jwt.invalid.token";
    public static final String JWT_VALIDATION_FAILED = "val.jwt.validation.failed";
    public static final String JWT_USERNAME_MISMATCH = "val.jwt.username.mismatch";
    public static final String JWT_TOKEN_MISMATCH = "val.jwt.token.mismatch";
    public static final String JWT_INTERNAL_ERROR = "val.jwt.internal.error";

    /* Cash In/Out */
    public static final String CASH_IN_OUT_ADDED_SUCCESSFULLY = "val.cash.in.out.added.success";
    public static final String CASH_IN_OUT_RETRIEVE_SUCCESSFULLY = "val.cash.in.out.retrieve.success";

    /*Customer*/
    public static final String CUSTOMER_REFERENCE_DATE_SUCCESS = "val.customer.reference.date.success";
}
