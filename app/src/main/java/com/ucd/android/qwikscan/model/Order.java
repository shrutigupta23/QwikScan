package com.ucd.android.qwikscan.model;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements BaseColumns,Serializable {

    private ArrayList<Order> orderList;
    public static final String TABLE_NAME = "orders";


    public static final String ORDER_ID = "order_id";

    public static final String PRODUCT_NAME = "product_name";

    public static final String QUANTITY = "quantity";

    public static final String PRODUCT_AMOUNT = "product_amount";

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    private String productName;
    private String quantity;
    private String productAmount;

    @Override
    public String toString() {
     //  return ("Product Name "+this.getProductName()+" ProductAmount "+this.getProductAmount()+" Quantity "+this.getQuantity());
        return(this.getProductName()+"\t"+this.getProductAmount()+"\t"+this.getQuantity());
    }
}


