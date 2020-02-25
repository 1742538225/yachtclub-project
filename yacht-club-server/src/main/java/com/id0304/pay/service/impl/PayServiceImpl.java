package com.id0304.pay.service.impl;

import com.id0304.entity.Consume;
import com.id0304.entity.PaymentType;
import com.id0304.pay.adapter.PayAdapter;
import com.id0304.pay.service.PayService;
import com.id0304.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 支付服务接口实现类
 * @Date 2019/9/15 9:47
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private PaymentTypeService paymentTypeService;

    @Autowired
    private YinLianPayServiceImpl yinLianPayService;

    @Override
    public String pay(Consume consume) {
        //判断支付类型
        Long typeId = consume.getTypeId();
        PaymentType paymentType= paymentTypeService.getPaymentType(typeId);
        if(paymentType == null){
            //没有找到该接口类型
            return null;
        }
        String typeName = paymentType.getTypename();
        PayAdapter payAdapter = null;
        switch (typeName){
            case "yinlianPay":
                payAdapter = yinLianPayService;
                break;
            default:
                break;
        }
        return payAdapter.pay(consume,paymentType);
    }
}
