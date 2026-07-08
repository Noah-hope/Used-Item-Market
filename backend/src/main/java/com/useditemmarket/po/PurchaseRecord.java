package com.useditemmarket.po;

public class PurchaseRecord {

    private String PID;

    private String Date;

    private String Time;

    private String GID;

    private String Gname;

    private String Gkind;

    private Double Gprice;

    private Double Gnumber;

    private String Status;

    private String DeliveryMode;

    private String PickupLocation;

    private String AppointmentTime;

    private String Remark;

    private String AddressSnapshot;

    public PurchaseRecord() {
    }

    public PurchaseRecord(String pid, String date, String time, MarketGoods goods, Double number) {
        PID=pid;
        Date = date;
        Time = time;
        GID=goods.getGID();
        Gname = goods.getName();
        Gkind = goods.getKind();
        Gprice = goods.getPrice();
        Gnumber = number;
    }

    public PurchaseRecord(String PID, String date, String time, String GID, String gname, String gkind, Double gprice, Double gnumber)
    {
        this.PID = PID;
        Date = date;
        Time = time;
        this.GID = GID;
        Gname = gname;
        Gkind = gkind;
        Gprice = gprice;
        Gnumber = gnumber;
    }

    public String getPID()
    {
        return PID;
    }

    public void setPID(String PID)
    {
        this.PID = PID;
    }
    public String getDate()
    {
        return Date;
    }

    public void setDate(String date)
    {
        Date = date;
    }

    public String getTime()
    {
        return Time;
    }

    public void setTime(String time)
    {
        Time = time;
    }

    public String getGID()
    {
        return GID;
    }

    public void setGID(String GID)
    {
        this.GID = GID;
    }

    public String getGname()
    {
        return Gname;
    }

    public void setGname(String gname)
    {
        Gname = gname;
    }

    public String getGkind()
    {
        return Gkind;
    }

    public void setGkind(String gkind)
    {
        Gkind = gkind;
    }

    public Double getGprice()
    {
        return Gprice;
    }

    public void setGprice(Double gprice)
    {
        Gprice = gprice;
    }

    public Double getGnumber()
    {
        return Gnumber;
    }

    public void setGnumber(Double gnumber)
    {
        Gnumber = gnumber;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDeliveryMode() {
        return DeliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        DeliveryMode = deliveryMode;
    }

    public String getPickupLocation() {
        return PickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        PickupLocation = pickupLocation;
    }

    public String getAppointmentTime() {
        return AppointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        AppointmentTime = appointmentTime;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getAddressSnapshot() {
        return AddressSnapshot;
    }

    public void setAddressSnapshot(String addressSnapshot) {
        AddressSnapshot = addressSnapshot;
    }

    @Override
    public String toString()
    {
        return "PurchaseRecord{" +
                "PID='" + PID + '\'' +
                ", Date='" + Date + '\'' +
                ", Time='" + Time + '\'' +
                ", GID='" + GID + '\'' +
                ", Gname='" + Gname + '\'' +
                ", Gkind='" + Gkind + '\'' +
                ", Gprice=" + Gprice +
                ", Gnumber=" + Gnumber +
                '}';
    }
}
