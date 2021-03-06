package com.example.moocmiaosha.service.model;

import java.math.BigDecimal;

public class OrderModel {

    //交易订单号
    private String id;

    //购买的用户id
    private Integer userId;

    //若非空则是以秒杀方式下单
    private Integer promoId;

    //购买的商品id
    private Integer itemId;

    //购买商品的单价，若promoId非空，则表示秒杀商品价格
    private BigDecimal itemPrice;

    //购买数量
    private Integer amount;

    //购买金额，若promoId非空，则表示秒杀商品价格
    private BigDecimal orderPrice;

    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", promoId=" + promoId +
                ", itemId=" + itemId +
                ", itemPrice=" + itemPrice +
                ", amount=" + amount +
                ", orderPrice=" + orderPrice +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
