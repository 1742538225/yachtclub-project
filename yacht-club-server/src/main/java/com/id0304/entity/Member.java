package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author WuZhengHua
 * @Description TODO 会员主体类
 * @Date 2019/9/5 22:25
 */
@Getter
@Setter
public class Member extends BaseEntity{
    private String name;
    private Long tel;   //手机号
    private String password;    //密码
    private Integer sex;    //0男 1女
    private Integer age;
    private Integer level;  //0会员   1管理员

}
