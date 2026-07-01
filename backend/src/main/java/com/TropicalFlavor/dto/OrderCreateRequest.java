package com.TropicalFlavor.dto;

public class OrderCreateRequest {
    private String gid;
    private Double quantity;
    private Boolean fromCart;
    private Long addressId;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getFromCart() {
        return fromCart;
    }

    public void setFromCart(Boolean fromCart) {
        this.fromCart = fromCart;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
