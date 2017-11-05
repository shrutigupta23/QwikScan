package com.ucd.android.qwikscan.model;

import android.provider.BaseColumns;

import java.sql.Date;

public class TransactionEntry implements BaseColumns {


    public static final String TABLE_NAME = "transactions";

    public static final String DATE = "transaction_date";

    public static final String ORDER_ID = "order_id";

    public static final String TOTAL_AMOUNT = "total_amount";

    private String entryId;

    private String date;
    private String orderId;
    private String totalAmount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
