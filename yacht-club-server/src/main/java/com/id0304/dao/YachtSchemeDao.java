package com.id0304.dao;

import com.id0304.common.mybatis.BaseDao;
import com.id0304.entity.YachtScheme;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface YachtSchemeDao extends BaseDao{
    @Select("select id,yachtid,time,price from yacht_scheme where yachtid = #{id}")
    List<YachtScheme> getSchemeByYachtId(@Param("id") Long id);

    @Select("select id,yachtid,time,price from yacht_scheme where id = #{schemeId}")
    YachtScheme getSchemeById(@Param("schemeId") Long schemeId);

    void addYachtSchemeList(List<YachtScheme> yachtSchemeList);

    @Delete("delete from yacht_scheme where yachtid = #{yachtId}")
    void deleteYachtSchemeByYachtId(@Param("yachtId") Long yachtId);
}
