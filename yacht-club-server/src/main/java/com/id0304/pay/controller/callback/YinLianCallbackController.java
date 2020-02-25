package com.id0304.pay.controller.callback;

import com.id0304.pay.service.CallbackService;
import com.id0304.pay.unionpay.acp.sdk.AcpService;
import com.id0304.pay.unionpay.acp.sdk.LogUtil;
import com.id0304.pay.unionpay.acp.sdk.SDKConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequestMapping("/pay/callback")
@Controller
public class YinLianCallbackController {
    @Autowired
    private CallbackService callbackService;

    @Value("${payFinishBackUrl}")
    private String payFinishBackUrl;    //支付成功同步回调地址,要跳转的页面

    private String toPage(String type){
        if(type.equals("PAY_FAIL")){
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"refresh\" content=\"0.1;url=" + payFinishBackUrl + "\">\n" +
                    "\n" +
                    "    <title>Aweather-支付失败</title>\n" +
                    "    <link rel=\"icon\" href=\"/img/favicon.ico\" type=\"image/x-icon\"/>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div style=\"text-align:center;\">\n" +
                    "    <h1 style=\"color: red\">充值失败!遇到未知错误,请勿刷新页面...</h1>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }else{
            return "<!DOCTYPE html\">\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"refresh\" content=\"0.1;url=" + payFinishBackUrl + "\">\n" +
                    "    <!--http://localhost:8080/#/user-->\n" +
                    "    <title>Aweather-支付成功</title>\n" +
                    "    <link rel=\"icon\" href=\"/img/favicon.ico\" type=\"image/x-icon\"/>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div style=\"text-align:center;\">\n" +
                    "    <h1 style=\"color: green\">充值成功!正在跳转,请勿刷新页面...</h1>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

    @PostMapping("/syn.action")
    @ResponseBody
    public String syn(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Map<String, String> reqParam = callbackService.syn(req);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        String encoding = req.getParameter(SDKConstants.param_encoding);
        if (!AcpService.validate(reqParam, encoding)) {
            LogUtil.writeLog("验证签名结果[失败].");
            //验签失败，需解决验签问题
            return toPage("PAY_FAIL");
        }
//        req.setAttribute("txnAmt", Double.parseDouble(reqParam.get("txnAmt")) / 100);
//        req.setAttribute("orderId", Long.parseLong(reqParam.get("orderId")));
//        req.setAttribute("payFinishUrl", "http://localhost:8080/#/user");
        LogUtil.writeLog("验证签名结果[成功].");
        return toPage("PAY_SUCCESS");
    }

    @PostMapping("/asyn.action")
    public String asyn(HttpServletRequest req) {
        return callbackService.asyn(req);
    }
}
