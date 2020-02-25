package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇主体类
 * @Date 2019/9/5 22:25
 */
@Getter
@Setter
@ToString
public class Yacht extends BaseEntity {
    private String name;
    private String color;
    private BigDecimal length;  //游艇长度,单位:米
    private Integer members;        //游艇限载人数
    private String address;     //游艇地址
    private Long maintenance;   //游艇维护周期,单位:分钟
    private Integer state;  //是否可供用户选择 0不可选,1可租赁,2可购买
    private Long price;     //游艇出售价格,单位:分

    private List<YachtImg> yachtImgList;
    private List<YachtScheme> yachtSchemeList;
}
