package com.id0304.dao;

import com.id0304.common.mybatis.BaseDao;
import com.id0304.entity.Member;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MemberDao extends BaseDao {
    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id查询用户信息
     * @Date 23:01 2019/9/5
     **/
    @Select("select id,tel,password,name,sex,age,level from member where id = #{id} and level != 2")
    Member getMemberById(@Param("id") Long id);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id删除用户信息
     * @Date 23:57 2019/9/5
     **/
    @Delete("delete from member where id = #{id} and level !=2")
    void deleteMemberById(@Param("id") Long id);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户手机号码查询用户
     * @Date 15:00 2019/9/12
     **/
    @Select("select id,name,tel,password,sex,age,level from member where tel = #{tel}")
    Member getMemberByTel(@Param("tel") Long tel);

    /**
     * @Author WuZhengHua
     * @Description TODO 修改会员基本信息,不包括手机和密码
     * @Date 16:01 2019/9/21
     **/
    @Update("update member set name = #{name},sex = #{sex},age = #{age} where id = #{id}")
    void updateMemberMsg(Member member);

    /**
     * @Author WuZhengHua
     * @Description TODO 分页条件查询用户信息
     * @Date 15:33 2019/10/4
     **/
    List<List<?>> getMembersByConAndPage(@Param("condition") String condition, @Param("minAge") String minAge, @Param("maxAge") String maxAge, @Param("member") Member member, @Param("index") Integer index, @Param("counts") Integer counts);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据管理员账号查询管理员信息
     * @Date 11:38 2019/10/6
     **/
    @Select("select id,name,tel,sex,age,level from member where tel = #{account} and (level = 1 or level = 2)")
    Member getAdminByAccount(@Param("account") Long account);

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id和新密码修改密码
     * @Date 17:52 2019/10/6
     **/
    @Update("update member set password = #{newPassword} where id = #{id}")
    void updatePwdById(@Param("id") Long id, @Param("newPassword") String newPassword);
}
