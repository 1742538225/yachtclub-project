package com.id0304.pay.adapter;

import com.id0304.entity.Consume;
import com.id0304.entity.PaymentType;

public interface PayAdapter {
    String pay(Consume consume, PaymentType paymentType);
}
