package com.yxy.service;

import com.github.pagehelper.PageInfo;
import com.yxy.model.request.CreateOrderReq;
import com.yxy.model.vo.OrderVO;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    //发货
    void deliver(String orderNo);

    void finish(String orderNo);
}
