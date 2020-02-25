package com.id0304.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.id0304.common.contants.CookieConstants;
import com.id0304.common.contants.RedisContants;
import com.id0304.common.contants.TimeConstants;
import com.id0304.common.utils.BaseRedisService;
import com.id0304.common.utils.CookieUtil;
import com.id0304.common.utils.DateUtil;
import com.id0304.common.utils.TokenUtil;
import com.id0304.dao.ConsumeDao;
import com.id0304.dao.MemberDao;
import com.id0304.entity.Consume;
import com.id0304.entity.Member;
import com.id0304.entity.Yacht;
import com.id0304.entity.YachtScheme;
import com.id0304.service.ConsumeService;
import com.id0304.service.YachtSchemeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 消费服务接口实现类
 * @Date 2019/9/5 22:50
 */
@Service
@Slf4j
public class ConsumeServiceImpl implements ConsumeService {

    @Autowired
    private ConsumeDao consumeDao;

    @Autowired
    private BaseRedisService baseRedisService;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private YachtSchemeService yachtSchemeService;


    /**
     * @Author WuZhengHua
     * @Description TODO 生成token和订单服务
     * @Date 12:03 2019/9/15
     **/
    @Override
    public String addConsumeToken(Consume consume, HttpServletRequest request, HttpServletResponse response) {
        try {
            Timestamp timestamp = consume.getUseTime();
            if (timestamp != null) {
                Date date = DateUtil.TimestampToDate(timestamp);
                String useTime = DateUtil.toStr(date);
                YachtScheme yachtScheme = yachtSchemeService.getSchemeById(consume.getSchemeId());
                String yachtId = yachtScheme.getYachtId() + "";
                if (ifTimeCanSelect(useTime, yachtId) != null) {
                    log.error("#####该时间点无法预订");
                    return "该时间点无法预订";
                }
            }
            String userToken = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_USERTOKEN);
            if (userToken == null) {
                log.error("#####登录信息过期");
                return "登录信息过期";
            }
            String memberTel = baseRedisService.get(userToken);
            if (memberTel == null) {
                log.error("#####登录信息过期");
                return "登录信息过期";
            }
            Member member = memberDao.getMemberByTel(Long.parseLong(memberTel));

            consumeDao.updateStatePayByMemberId(member.getId(), 2);

            consume.setMemberId(member.getId());
            consume.setTypeId(37L);
            consume.setOrderId(DateUtil.getTimeId() + member.getId());
            consume.setState(0);
            consume.setCreated(DateUtil.getTimestamp());
            consume.setUpdated(DateUtil.getTimestamp());
            consumeDao.saveConsume(consume);
            Long id = consume.getId();
            if (id == null) {
                log.error("#####系统错误,生成支付token失败");
                return "系统错误";
            }
            //生成对应token
            String payToken = TokenUtil.getPayToken();
            //存放再redis中 key-value：token-id
            baseRedisService.setString(payToken, id + "", RedisContants.REDIS_TIMEOUT_USER_10M);
            //cookie中存入paytoken
            CookieUtil.addCookie(response, CookieConstants.COOKIE_NAME_PAYTOKEN, payToken, CookieConstants.COOKIE_TIMEOUT_10M);
            log.info("#####生成支付token:{} 与订单成功", payToken);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####系统错误");
            return "系统错误";
        }
    }

    @Override
    public Consume getConsumeToken(String token) {
        //判断token是否为空
        if (StringUtils.isEmpty(token)) {
            log.error("#####获取订单信息时token不能为空");
            return null;
        }
        //使用token去redis去查找对应的支付id
        String id = baseRedisService.get(token);
        if (StringUtils.isEmpty(id)) {
            log.error("#####该支付信息已过期");
            return null;
        }
        //使用支付id查找数据库
        Long newId = Long.parseLong(id);
        return consumeDao.getConsumeById(newId);
    }

    @Override
    public void updateConsume(Consume consume) {
        consume.setUpdated(DateUtil.getTimestamp());
        consumeDao.updateConsume(consume);
    }

    @Override
    public String getMemberConsumes(HttpServletRequest request) {
        try {
            String userToken = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_USERTOKEN);
            if (userToken == null) {
                log.error("#####获取指定用户订单信息失败,无法找到token");
                return "登录过期";
            }
            String tel = baseRedisService.get(userToken);
            if (tel == null) {
                log.error("#####获取指定用户订单信息失败,无法查找到用户tel");
                return "登录过期";
            }
            Member member = memberDao.getMemberByTel(Long.parseLong(tel));
            String payToken = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_PAYTOKEN);
            if (payToken == null) {
                log.error("#####设置过期订单");
                consumeDao.updateStatePayByMemberId(member.getId(), 2);
            }
            List<Consume> consumeList = consumeDao.getMemberConsumesByMemberId(member.getId());
            log.info("#####查询到用户id为{}的所有订单", member.getId());
            return JSON.toJSONString(consumeList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("#####获取指定用户订单信息失败,服务器异常");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 业务难点######根据使用时间和游艇id确定该时间点是否能够供用户选择
     * @Date 11:20 2019/9/17
     **/
    @Override
    public String ifTimeCanSelect(String useTime, String yachtId) {
        try {
            Date date = DateUtil.parseDate(useTime);
            Timestamp nowStamp = DateUtil.getTimestamp();
            Date nowDate = DateUtil.TimestampToDate(nowStamp);
            if (date.getTime() <= DateUtil.addDate(nowDate, TimeConstants.SUBSCRIBE_BEFORE_DAY).getTime()) {
                log.error("#####该日期无效,请提前24小时预订");
                return "该日期无效,请提前24小时预订";
            }
            List<Consume> consumeList = consumeDao.getUseTimeListByTimeAndYachtId(useTime, yachtId);
            if (consumeList.size() <= 0) {
                log.info("#####用户所选日期和套餐可选");
                return null;
            }
            Long maintenance = consumeList.get(0).getYacht().getMaintenance();      //维护周期,单位:分钟
            Timestamp[] timestamps = new Timestamp[consumeList.size()];
            for (int i = 0; i < consumeList.size(); ++i) {
                timestamps[i] = consumeList.get(i).getUseTime();
                System.out.println(timestamps[i]);
            }
            Long dateAdd = date.getTime() + 1000 * 60 * maintenance;     //用户选择的时间加maintenance分钟
            Long dateSub = date.getTime() - 1000 * 60 * maintenance;     //用户选择的时间减maintenance分钟
            Boolean flag = true;    //默认可选
            for (Timestamp stamp : timestamps) {
                Long ifDate = DateUtil.TimestampToDate(stamp).getTime();
                if (ifDate > dateSub && ifDate < dateAdd) {         //用户选择的时间前后maintenance分钟若有订单,则返回false
                    flag = false;
                }
            }
            if (flag) {
                log.info("#####用户所选日期和套餐可选");
                return null;
            } else {
                log.error("#####用户所选日期和套餐不可选,已有预订订单");
                return "该日期和套餐不可选,已被预约";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据订单id查询所有订单关联信息
     * @Date 10:41 2019/9/21
     **/
    @Override
    public Consume getConsumeMsgByOrderId(String orderId) {
        try {
            log.info("#####根据订单id={}查询所有订单关联信息", orderId);
            return consumeDao.getConsumeMsgByOrderId(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误,根据订单id={}查询所有订单关联信息", orderId);
            return null;
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 管理员-根据条件页数返回订单列表
     * @Date 21:27 2019/9/23
     **/
    @Override
    public String getConsumes(String condition, String state, String productType, String minUseTime, String maxUseTime, String minCreated, String maxCreated, String page, String counts) {
        Consume consume = new Consume();
        Yacht yacht = new Yacht();
        if (!state.equals("")) {
            consume.setState(Integer.parseInt(state));
        }
        if (!productType.equals("")) {
            yacht.setState(Integer.parseInt(productType));
        }
        if (minUseTime.equals("")) {
            minUseTime = null;
        }
        if (maxUseTime.equals("")) {
            maxUseTime = null;
        }
        if (minCreated.equals("")) {
            minCreated = null;
        }
        if (maxCreated.equals("")) {
            maxCreated = null;
        }
        if (!condition.equals("")) {
            condition = "%" + condition + "%";
        } else {
            condition = null;
        }
        Integer index = (Integer.parseInt(page) - 1) * Integer.parseInt(counts);    //分页查询第一条记录的索引

        List<List<?>> resultList = consumeDao.getConsumesByConAndPage(condition, consume,minUseTime,maxUseTime,minCreated,maxCreated, yacht, index, Integer.parseInt(counts));
        List<Consume> consumeList = (List<Consume>) resultList.get(0);      //结果集
        Long listCounts = (Long) resultList.get(1).get(0);   //分页查询数据总数
        String[] arr = new String[]{JSON.toJSONString(consumeList), listCounts + ""};
        return JSON.toJSONString(arr);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 服务层--删除选定的无效订单
     * @Date 17:58 2019/9/26
     **/
    @Override
    public String deleteNullConsumes(String[] batchDelList) {
        try {
            if(batchDelList.length<=0){
                return "请选择要删除的订单";
            }
            Long[] idList = (Long[]) ConvertUtils.convert(batchDelList, Long.class);
            consumeDao.deleteNullConsumesByIdList(idList);
            log.info("#####批量删除指定id的无效订单完成");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####批量删除指定id的无效订单,服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 删除所有无效订单
     * @Date 13:46 2019/10/4
     **/
    @Override
    public String deleteVoidOrders() {
        try {
            consumeDao.deleteVoidOrders();
            log.info("#####删除所有无效订单成功");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####删除所有无效订单失败,服务器异常");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 设置所有过期未支付订单为无效订单
     * @Date 12:18 2019/10/6
     **/
    @Override
    public String setVoidOrders() {
        try {
            consumeDao.setVoidOrders();
            log.info("#####设置所有过期未支付订单为无效订单成功");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####设置所有过期未支付订单为无效订单失败,服务器异常");
            return "服务器错误";
        }
    }
}
