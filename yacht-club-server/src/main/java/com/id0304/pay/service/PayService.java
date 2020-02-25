package com.id0304.pay.service;


import com.id0304.entity.Consume;

/**
 * @Author WuZhengHua
 * @Description TODO 支付服务接口
 * @Date 2019/9/15 9:47
 */
public interface PayService {

    String pay(Consume consume);
}
