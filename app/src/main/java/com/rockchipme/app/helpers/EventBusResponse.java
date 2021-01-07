package com.rockchipme.app.helpers;

/**
 * Created by Alisons on 4/6/2018.
 */

public class EventBusResponse {

    public final static int UPDATE_CART = 1;
    public final static int CANCEL_ORDER = 2;
    public final static int ORDER_UPDATE = 3;

    public int type;
    public String productId, grandTotal;
    public String orderId;
    public int quantity;
}
