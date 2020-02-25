package com.id0304.dao;

import com.id0304.common.mybatis.BaseDao;
import com.id0304.entity.Yacht;
import com.id0304.entity.YachtScheme;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇信息dao层
 * @Date 23:07 2019/9/5
 **/
@Mapper
@Component
public interface YachtDao extends BaseDao {
    List<Yacht> getYachtsAndMainPicByState(@Param("state") Integer state);

    Yacht getYachtAndImgById(Long id);

    @Insert("insert into yacht(id,name,color,length,members,address,maintenance,state,price,created,updated) value(null,#{name},#{color},#{length},#{members},#{address},#{maintenance},#{state},#{price},#{created},#{updated})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addYacht(Yacht yacht);

    @Update("update yacht set name = #{name},color = #{color},length = #{length},members = #{members},address = #{address},maintenance = #{maintenance},state = #{state},price = #{price},updated = #{updated} where id = #{id}")
    void updateYacht(Yacht yacht);

    /**
     * @Author WuZhengHua
     * @Description TODO 分页条件查询游艇信息
     * @Date 13:38 2019/10/3
     **/
    List<List<?>> getYachtsByConAndPage(@Param("condition") String condition, @Param("yacht") Yacht yacht, @Param("minLength") String minLength, @Param("maxLength") String maxLength, @Param("minMembers") String minMembers, @Param("maxMembers") String maxMembers);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇 id 删除游艇信息-游艇图片信息-游艇套餐信息-包含该游艇的无效订单信息
     * @Date 16:11 2019/10/3
     **/
    void deleteYachtMessage(Long yachtId);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇id设置游艇状态
     * @Date 13:10 2019/10/6
     **/
    @Update("update yacht set state = #{state} where id = #{yachtId}")
    void updateStateByYachtId(@Param("yachtId") Long yachtId, @Param("state") Integer state);
}
