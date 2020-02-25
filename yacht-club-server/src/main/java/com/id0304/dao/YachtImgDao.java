package com.id0304.dao;

import com.id0304.common.mybatis.BaseDao;
import com.id0304.entity.YachtImg;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇图片dao
 * @Date 13:18 2019/9/29
 **/
@Mapper
@Component
public interface YachtImgDao extends BaseDao{
    void addYachtImgList(List<YachtImg> yachtImgList);

    @Delete("delete from yacht_img where yachtid = #{yachtId}")
    void deleteYachtImgByYachtId(@Param("yachtId") Long yachtId);
}
