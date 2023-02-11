package com.driver;


import io.swagger.models.auth.In;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    private HashMap<String,Order>  ordermap;
   private HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
   private HashMap<String,String> orderDeliveryPartnerHashMap;

   private HashMap<String, HashSet<String>> partnerOrdermap;


    public OrderRepository() {
        this.ordermap=new HashMap<>();
        this.deliveryPartnerHashMap=new HashMap<>();
        this.orderDeliveryPartnerHashMap=new HashMap<>();
        this.partnerOrdermap=new HashMap<>();
    }

    public void addOrder(Order order){
        ordermap.put(order.getId(),order);
//        String id=order.getId();
//        int deliveryTime=order.getDeliveryTime();
//
//        if(!ordermap.containsKey(id)){
//            ordermap.put(id,new Order(id,String.valueOf(deliveryTime)));
//        }
//        return "New Order Added Successfully";
    }

    public void addPartner(String partnerId){


        DeliveryPartner deliveryPartner=new DeliveryPartner(partnerId);
        deliveryPartnerHashMap.put(partnerId,deliveryPartner);

        //return "New delivery partner Added successfully ";
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        if(ordermap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId)){
            HashSet<String> currorder=new HashSet<String>();
            if(partnerOrdermap.containsKey(partnerId)){
                currorder=partnerOrdermap.get(partnerId);
            }
            currorder.add(orderId);
            partnerOrdermap.put(partnerId,currorder);

            DeliveryPartner p=deliveryPartnerHashMap.get(partnerId);
            p.setNumberOfOrders(currorder.size());
            orderDeliveryPartnerHashMap.put(orderId,partnerId);
        }
    }

    public Order getOrderById(String orderId){

        return ordermap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){

        return deliveryPartnerHashMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count=0;
        if(deliveryPartnerHashMap.containsKey(partnerId)){
            count=deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
        }
        return count;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        HashSet<String> orders=new HashSet<>();
        if(partnerOrdermap.containsKey(partnerId)){
            orders=partnerOrdermap.get(partnerId);
        }
        return new ArrayList<>(orders);

    }
    public List<String> getAllOrders(){

        return new ArrayList<>(ordermap.keySet());
    }


    public Integer getCountOfUnassignedOrders(){

        Integer count=0;
        List<String> order=new ArrayList<>(ordermap.keySet());
        for(String id:order){
            if(!orderDeliveryPartnerHashMap.containsKey(id)){
                count+=1;
            }
        }
        return count;
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer hr=Integer.valueOf(time.substring(0,2));
        Integer min=Integer.valueOf(time.substring(3));
        Integer t=hr*60+min;

        Integer c=0;
        if(partnerOrdermap.containsKey(partnerId)){
            HashSet<String> orders=partnerOrdermap.get(partnerId);
            for(String o:orders){
                if(ordermap.containsKey(o)){
                    Order current=ordermap.get(o);
                    if(t<current.getDeliveryTime()){
                        c+=1;
                    }
                }
            }
        }
        return c;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time = 0;

        if(partnerOrdermap.containsKey(partnerId)){
            HashSet<String> orders =partnerOrdermap.get(partnerId);
            for(String order: orders){
                if(ordermap.containsKey(order)){
                    Order currOrder = ordermap.get(order);
                    time = Math.max(time, currOrder.getDeliveryTime());
                }
            }
        }

        Integer hour = time/60;
        Integer minutes = time%60;

        String hourInString = String.valueOf(hour);
        String minInString = String.valueOf(minutes);
        if(hourInString.length() == 1){
            hourInString = "0" + hourInString;
        }
        if(minInString.length() == 1){
            minInString = "0" + minInString;
        }

        return  hourInString + ":" + minInString;
    }
    public void deletePartnerById(String partnerId){

        HashSet<String> orders = new HashSet<>();
        if(partnerOrdermap.containsKey(partnerId)){
            orders = partnerOrdermap.get(partnerId);
            for(String order: orders){
                if(orderDeliveryPartnerHashMap.containsKey(order)){

                    orderDeliveryPartnerHashMap.remove(order);
                }
            }
            partnerOrdermap.remove(partnerId);
        }

        if(deliveryPartnerHashMap.containsKey(partnerId)){
            deliveryPartnerHashMap.remove(partnerId);
        }
    }
    public void deleteOrderById(String orderId){

        if(orderDeliveryPartnerHashMap.containsKey(orderId)){
            String partnerId = orderDeliveryPartnerHashMap.get(orderId);
            HashSet<String> orders = partnerOrdermap.get(partnerId);
            orders.remove(orderId);
            partnerOrdermap.put(partnerId, orders);

            //change order count of partner
            DeliveryPartner partner = deliveryPartnerHashMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
        }

        if(ordermap.containsKey(orderId)){
            ordermap.remove(orderId);
        }
    }
}

