package com.useditemmarket.vo;

import com.useditemmarket.model.OrderStatus;
import com.useditemmarket.po.PurchaseRecord;
import com.useditemmarket.po.SaleRecord;
import com.useditemmarket.po.TradeRecord;

public class OrderVo {
    private String pid;
    private String buyerUid;
    private String sellerUid;
    private String date;
    private String time;
    private String gid;
    private String goodsName;
    private String category;
    private Double price;
    private Double quantity;
    private OrderStatus status;
    private String deliveryMode;
    private String pickupLocation;
    private String appointmentTime;
    private String remark;
    private String addressSnapshot;

    public static OrderVo fromPurchase(PurchaseRecord record, String sellerUid, OrderStatus status) {
        OrderVo vo = new OrderVo();
        vo.pid = record.getPID();
        vo.sellerUid = sellerUid;
        vo.date = record.getDate();
        vo.time = record.getTime();
        vo.gid = record.getGID();
        vo.goodsName = record.getGname();
        vo.category = record.getGkind();
        vo.price = record.getGprice();
        vo.quantity = record.getGnumber();
        vo.status = record.getStatus() == null ? status : OrderStatus.fromValue(record.getStatus());
        vo.deliveryMode = record.getDeliveryMode();
        vo.pickupLocation = record.getPickupLocation();
        vo.appointmentTime = record.getAppointmentTime();
        vo.remark = record.getRemark();
        vo.addressSnapshot = record.getAddressSnapshot();
        return vo;
    }

    public static OrderVo fromSale(SaleRecord record, OrderStatus status) {
        OrderVo vo = new OrderVo();
        vo.pid = record.getPID();
        vo.buyerUid = record.getBuyerID();
        vo.date = record.getDate();
        vo.time = record.getTime();
        vo.gid = record.getGID();
        vo.goodsName = record.getGname();
        vo.category = record.getGkind();
        vo.price = record.getGprice();
        vo.quantity = record.getGnumber();
        vo.status = record.getStatus() == null ? status : OrderStatus.fromValue(record.getStatus());
        vo.deliveryMode = record.getDeliveryMode();
        vo.pickupLocation = record.getPickupLocation();
        vo.appointmentTime = record.getAppointmentTime();
        vo.remark = record.getRemark();
        vo.addressSnapshot = record.getAddressSnapshot();
        return vo;
    }

    public static OrderVo fromTrade(TradeRecord record) {
        OrderVo vo = new OrderVo();
        vo.pid = record.getPID();
        vo.buyerUid = record.getBuyerID();
        vo.sellerUid = record.getSellerID();
        vo.date = record.getDate();
        vo.time = record.getTime();
        vo.gid = record.getGID();
        vo.goodsName = record.getGname();
        vo.category = record.getGkind();
        vo.price = record.getGprice();
        vo.quantity = record.getGnumber();
        vo.status = OrderStatus.fromValue(record.getStatus());
        vo.deliveryMode = record.getDeliveryMode();
        vo.pickupLocation = record.getPickupLocation();
        vo.appointmentTime = record.getAppointmentTime();
        vo.remark = record.getRemark();
        vo.addressSnapshot = record.getAddressSnapshot();
        return vo;
    }

    public String getPid() {
        return pid;
    }

    public String getBuyerUid() {
        return buyerUid;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getGid() {
        return gid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getRemark() {
        return remark;
    }

    public String getAddressSnapshot() {
        return addressSnapshot;
    }
}
