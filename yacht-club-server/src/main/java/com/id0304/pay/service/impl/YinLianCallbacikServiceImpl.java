package com.id0304.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.id0304.common.utils.DateUtil;
import com.id0304.dao.ConsumeDao;
import com.id0304.dao.MemberDao;
import com.id0304.dao.YachtDao;
import com.id0304.entity.Consume;
import com.id0304.pay.service.CallbackService;
import com.id0304.pay.unionpay.acp.sdk.AcpService;
import com.id0304.pay.unionpay.acp.sdk.LogUtil;
import com.id0304.pay.unionpay.acp.sdk.SDKConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class YinLianCallbacikServiceImpl implements CallbackService {
    @Autowired
    private ConsumeDao consumeDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private YachtDao yachtDao;

    @Override
    public Map<String, String> syn(HttpServletRequest req) {
        LogUtil.writeLog("YinLianCallbacikServiceImpl接收前台通知开始");
        String encoding = req.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(req);
        LogUtil.printRequestLog(reqParam);

        LogUtil.writeLog("YinLianCallbacikServiceImpl接收前台通知结束");
        //返回给银联服务器http 200  状态码
        return reqParam;
    }

    @Override
    public String asyn(HttpServletRequest req) {
        String encoding = req.getParameter(SDKConstants.param_encoding);
        Map<String, String> reqParam = getAllRequestParam(req);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(reqParam, encoding)) {
            LogUtil.writeLog("验证签名结果[失败].");
            //验签失败，需解决验签问题

        }
        LogUtil.writeLog("验证签名结果[成功].");
        //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态

        String orderId = reqParam.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
        Consume consume = consumeDao.getConsumeByOrderId(orderId);
        if (consume == null) {
            return "fail";
        }
        Integer state = consume.getState();
        if (state.equals("1")) {
            log.error("订单号:{},已经支付成功!无需再次操作..", orderId);
            return "ok";
        }

        //第三方支付订单号
        consume.setPlatformorderId(reqParam.get("queryId"));
        //支付报文
        consume.setPayMessage(new JSONObject().toJSONString(reqParam));
        //状态
        consume.setState(1);
        //修改时间
        consume.setUpdated(DateUtil.getTimestamp());
        consumeDao.updateConsume(consume);
        //若为购买订单,修改游艇状态为已售出
        if(consume.getYacht().getState().equals(2)){
            log.info("#####修改游艇出售状态为已售出");
            yachtDao.updateStateByYachtId(consume.getYachtId(),3);
        }
        String respCode = reqParam.get("respCode");
        //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。

        LogUtil.writeLog("BackRcvResponse接收后台通知结束");
        return "ok";
    }

    /**
     * 获取请求参数中所有的信息
     * 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
     * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    // System.out.println("======为空的字段名===="+en);
                    res.remove(en);
                }
            }
        }
        return res;
    }
}
