package com.id0304.service;

import com.id0304.entity.Yacht;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇信息服务层接口
 * @Date 23:03 2019/9/5
 **/
public interface YachtService {
    String getYachts(String condition, String minLength, String maxLength, String minMembers, String maxMembers, String state, String maintenance, String page, String counts);

    String getYachtsAndMainPicByState(Integer state);

    String getYachtMessage(Long id);

    String addYacht(Yacht yacht);

    String updateYacht(Yacht yacht);

    String deleteYachtById(String yachtId);
}
