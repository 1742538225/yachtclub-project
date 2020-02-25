package com.id0304.service.impl;

import com.id0304.dao.PaymentTypeDao;
import com.id0304.entity.PaymentType;
import com.id0304.service.PaymentTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Slf4j
public class PaymentTypeServiceImpl implements PaymentTypeService {
    @Autowired
    private PaymentTypeDao paymentTypeDao;

    @Override
    public PaymentType getPaymentType(@RequestParam("id") Long id) {
        PaymentType paymentType = paymentTypeDao.getPaymentType(id);
        if(paymentType == null){
            log.error("#####未查找到支付类型");
            return null;
        }
        return paymentType;
    }
}
