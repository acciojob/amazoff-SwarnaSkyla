package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id=id;
        Integer hr=Integer.valueOf(deliveryTime.substring(0,2));
        Integer min=Integer.valueOf(deliveryTime.substring(3));
        this.deliveryTime=hr*60+min;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
