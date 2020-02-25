package com.id0304.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @Author WuZhengHua
 * @Description TODO 公共实体类
 * @Date 2019/9/5 22:34
 */
@Getter
@Setter
@ToString
public class BaseEntity {
    private Long id;
    private Timestamp created;
    private Timestamp updated;
}
