package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @Author WuZhengHua
 * @Description TODO 消费实体类
 * @Date 2019/9/5 22:26
 */
@Setter
@Getter
@ToString
public class Consume extends BaseEntity{
    private Long memberId;
    private Long tel;   //订单联系电话
    private String name;    //订单联系人
    private Long yachtId;  //关联游艇
    private Long schemeId;  //包艇套餐  包含游艇id和包艇时长和包艇价格
    private Timestamp useTime; //使用时间
    private Long typeId;    //支付方式
    private String orderId;     //订单号
    private String platformorderId;     //支付平台返回的订单号
    private Integer state;  //订单状态,未支付0,支付1,过期无效2
    private String payMessage;  //支付报文,用于对账
    private Long price; //消费金额,单位:分

    private Yacht yacht;
    private YachtScheme yachtScheme;
}
