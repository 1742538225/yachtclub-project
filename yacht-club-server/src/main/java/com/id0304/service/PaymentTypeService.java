package com.id0304.service;

import com.id0304.entity.PaymentType;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface PaymentTypeService {

    PaymentType getPaymentType(@RequestParam("id") Long id);
}
