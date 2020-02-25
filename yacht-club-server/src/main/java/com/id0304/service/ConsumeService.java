package com.id0304.service;

import com.id0304.entity.Consume;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 消费信息服务层接口
 * @Date 23:03 2019/9/5
 **/
public interface ConsumeService {

    String addConsumeToken(Consume consume, HttpServletRequest request, HttpServletResponse response);

    Consume getConsumeToken(String token);

    void updateConsume(Consume consume);

    String getMemberConsumes(HttpServletRequest request);

    String ifTimeCanSelect(String useTime, String yachtId);

    Consume getConsumeMsgByOrderId(String orderId);

    String getConsumes(String condition,String state,String productType,String minUseTime,String maxUseTime,String minCreated,String maxCreated,String  page, String counts);

    String deleteNullConsumes(String[] batchDelList);

    String deleteVoidOrders();

    String setVoidOrders();
}
