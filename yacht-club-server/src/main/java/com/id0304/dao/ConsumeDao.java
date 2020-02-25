package com.id0304.dao;

import com.id0304.common.mybatis.BaseDao;
import com.id0304.entity.Consume;
import com.id0304.entity.Yacht;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Mapper
@Component
public interface ConsumeDao extends BaseDao {
    Consume getConsumeById(@Param("id") Long id);

    @Insert("insert into consume (id,memberid,tel,name,yachtid,schemeid,usetime,typeid,orderid,platformorderid,price,state,created,updated) value(null,#{memberId},#{tel},#{name},#{yachtId},#{schemeId},#{useTime},#{typeId},#{orderId},#{platformorderId},#{price},#{state},#{created},#{updated})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveConsume(Consume consume);

    Consume getConsumeByOrderId(@Param("orderId") String orderId);

    @Update("update consume set state = #{state},payMessage=#{payMessage},platformorderId=#{platformorderId},updated=#{updated} where orderId=#{orderId}")
    void updateConsume(Consume consume);

    List<Consume> getMemberConsumesByMemberId(Long memberId);

    /**
     * @Author WuZhengHua
     * @Description TODO useTime为用户想选择的租艇时间点,yachtId为用户想租用的游艇id,返回一个订单list集合(useTime和maintenance),条件useTime前后两个月之间的时间段
     * @Date 11:23 2019/9/17
     **/
    List<Consume> getUseTimeListByTimeAndYachtId(@Param("useTime") String useTime, @Param("yachtId") String yachtId);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据会员id修改所有未支付订单的状态
     * @Date 14:43 2019/9/17
     **/
    @Update("update consume set state = #{state} where memberid = #{memberId} and state = 0")
    Integer updateStatePayByMemberId(@Param("memberId") Long memberId,@Param("state") Integer state);

    Consume getConsumeMsgByOrderId(@Param("orderId") String orderId);

    /**
     * @Author WuZhengHua
     * @Description TODO 难点--返回总页数和订单list集合(条件+分页)
     * @Date 21:43 2019/9/23
     **/
    List<List<?>> getConsumesByConAndPage(@Param("condition") String condition, @Param("consume") Consume consume,@Param("minUseTime") String minUseTime,@Param("maxUseTime") String maxUseTime,@Param("minCreated") String minCreated,@Param("maxCreated") String maxCreated, @Param("yacht") Yacht yacht, @Param("index") Integer index, @Param("counts") Integer counts);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据订单id的数组批量删除无效订单
     * @Date 18:03 2019/9/26
     **/
    Integer deleteNullConsumesByIdList(Long[] batchDelList);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇id获取订单信息
     * @Date 16:06 2019/10/3
     **/
    @Select("select id from consume where yachtid = #{yachtId} and state != 2")
    List<Consume> getConsumeIdByYachtId(@Param("yachtId") String yachtId);

    /**
     * @Author WuZhengHua
     * @Description TODO 删除所有无效订单
     * @Date 13:47 2019/10/4
     **/
    @Delete("delete from consume where state = 2")
    void deleteVoidOrders();

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id查询所有该用户的有效订单
     * @Date 19:01 2019/10/4
     **/
    @Select("select  id,memberid,tel,name,schemeid,usetime,typeid,orderid,platformorderid,price,state from consume where state !=2 and memberid = #{memberId}")
    List<Consume> getUseFulConsumeByMemberId(Long memberId);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id删除无效的订单
     * @Date 19:05 2019/10/4
     **/
    @Delete("delete from consume where memberid = #{memberId} and state = 2")
    void deleteNullConsumeByMemberId(Long memberId);

    /**
     * @Author WuZhengHua
     * @Description TODO 设置所有未支付且下单时间超过5分钟的订单为无效订单
     * @Date 12:29 2019/10/6
     **/
    @Update("update consume set state = 2 where state = 0 and DATE_ADD(created,INTERVAL 5 MINUTE) < NOW()")
    void setVoidOrders();
}
