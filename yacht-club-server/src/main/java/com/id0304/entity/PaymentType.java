package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentType extends BaseEntity {
    private String typename;
    private String fronturl;
    private String backurl;
    private String merchantid;
}
