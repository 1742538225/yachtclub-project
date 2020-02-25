package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author WuZhengHua
 * @Description TODO 包艇套餐实体类
 * @Date 2019/9/14 19:51
 */
@Setter
@Getter
@ToString
public class YachtScheme extends BaseEntity {
    private Long yachtId;
    private Integer time;   //套餐时间,单位是小时,即一次包艇多长时间
    private Long price; //套餐价格,可以根据租艇商的优惠方式定价

    private Yacht yacht;
}
