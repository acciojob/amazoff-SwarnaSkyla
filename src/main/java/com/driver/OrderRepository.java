package com.driver;


import io.swagger.models.auth.In;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String,Order>  ordermap;
   private HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
   private HashMap<String,List<String>> orderDeliveryPartnerHashMap;

   private Set<String> odernotassign;


    public OrderRepository() {
        this.ordermap=new HashMap<>();
        this.deliveryPartnerHashMap=new HashMap<>();
        this.orderDeliveryPartnerHashMap=new HashMap<>();
        this.odernotassign=new HashSet<>();
    }

    public void addOrder(Order order){
        ordermap.put(order.getId(),order);
        odernotassign.add(order.getId());
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
          deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders()+1);
          if(orderDeliveryPartnerHashMap.containsKey(partnerId)) {
              List<String> orders = orderDeliveryPartnerHashMap.get(partnerId);
              orders.add(orderId);
              odernotassign.remove(orderId);
              return;
          }
          orderDeliveryPartnerHashMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
          odernotassign.remove(orderId);

//        if(ordermap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId)){
//            HashSet<String> currorder=new HashSet<String>();
//            if(partnerOrdermap.containsKey(partnerId)){
//                currorder=partnerOrdermap.get(partnerId);
//            }
//            currorder.add(orderId);
//            partnerOrdermap.put(partnerId,currorder);
//
//            DeliveryPartner p=deliveryPartnerHashMap.get(partnerId);
//            p.setNumberOfOrders(currorder.size());
//            orderDeliveryPartnerHashMap.put(orderId,partnerId);
        }


    public Order getOrderById(String orderId){

        return ordermap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){

        return deliveryPartnerHashMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        return orderDeliveryPartnerHashMap.get(partnerId).size();
//        Integer count=0;
//        if(deliveryPartnerHashMap.containsKey(partnerId)){
//            count=deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
//        }
//        return count;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
    List<String> orders=new ArrayList<>();
    List<String> id=orderDeliveryPartnerHashMap.get(partnerId);
    for(String o:id) {
        orders.add(ordermap.get(o).getId());
    }
    return orders;
    }
    public List<String> getAllOrders(){

//        return new ArrayList<>(ordermap.keySet());
        Collection<Order> v=ordermap.values();
        List<String> olist=new ArrayList<>();
        for(Order o:v) {
            olist.add(o.getId());
        }
        return olist;
    }


    public Integer getCountOfUnassignedOrders(){

    return odernotassign.size();
//        Integer count=0;
//        List<String> order=new ArrayList<>(ordermap.keySet());
//        for(String id:order){
//            if(!orderDeliveryPartnerHashMap.containsKey(id)){
//                count+=1;
//            }
//        }
//        return count;
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId) {
    int numerictime=Integer.parseInt(time.substring(0,2))-60+Integer.parseInt(time.substring(3,5));

       int c=0;
       for (String id:orderDeliveryPartnerHashMap.get(partnerId)){
             if(ordermap.get(id).getDeliveryTime()>numerictime) {
                 c++;
             }
        }
       return c;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time = 0;

        if(orderDeliveryPartnerHashMap.containsKey(partnerId)){
//            HashSet<String> orders =orderDeliveryPartnerHashMap.get(partnerId);
            for(String order: orderDeliveryPartnerHashMap.get(partnerId)){
                if(ordermap.get(order).getDeliveryTime()>time){
//                    Order currOrder = ordermap.get(order);
                    time = ordermap.get(order).getDeliveryTime();
                }
            }
        }

        Integer hour = time/60;
        Integer minutes = time%60;

        String hourInString = Integer.toString(hour);
        String minInString = Integer.toString(minutes);
        if(hourInString.length() == 1){
            hourInString = "0" + hourInString;
        }
        if(minInString.length() == 1){
            minInString = "0" + minInString;
        }

        return  hourInString + ":" + minInString;
    }
    public void deletePartnerById(String partnerId){

       if(!orderDeliveryPartnerHashMap.isEmpty()) {
           odernotassign.addAll(orderDeliveryPartnerHashMap.get(partnerId));
       }
       orderDeliveryPartnerHashMap.remove(partnerId);
       deliveryPartnerHashMap.remove(partnerId);
    }
    public void deleteOrderById(String orderId) {
       ordermap.remove(orderId);
       if(odernotassign.contains(orderId)){
           odernotassign.remove(orderId);}
       else {
           for (List<String> listofid:orderDeliveryPartnerHashMap.values()) {
               listofid.remove(orderId);
           }
       }
    }
}

