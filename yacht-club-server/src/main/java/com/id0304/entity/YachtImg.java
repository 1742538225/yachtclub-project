package com.id0304.entity;

import com.id0304.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇图片路径实体类
 * @Date 2019/9/11 16:07
 */
@Getter
@Setter
@ToString
public class YachtImg extends BaseEntity {
    private Long yachtId;
    private String img;

    private Yacht yacht;
}
