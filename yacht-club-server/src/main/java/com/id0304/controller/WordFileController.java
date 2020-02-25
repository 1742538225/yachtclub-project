package com.id0304.controller;

import com.id0304.common.utils.WordUtil;
import com.id0304.entity.Consume;
import com.id0304.service.ConsumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author WuZhengHua
 * @Description TODO 导出word文档格式的订单控制层
 * @Date 2019/9/20 20:54
 */
@RestController
@Slf4j
@RequestMapping("/api/file")
public class WordFileController {
    @Autowired
    private ConsumeService consumeService;

    @Resource
    private WordUtil wordUtil;

    /**
     * @Author WuZhengHua
     * @Description TODO 下载导出word格式的订单
     * @Date 20:57 2019/9/20
     **/
    @GetMapping(value = "/downloadWord.action")
    public void downloadInfo(HttpServletResponse response, @RequestParam("orderId") String orderId) {
        //if (doSecurityIntercept(Const.RESOURCES_TYPE_BUTTON){
        try {
            Consume consume = consumeService.getConsumeMsgByOrderId(orderId);
            if (consume != null) {
                wordUtil.download(response, consume);
                log.info("#####下载word");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####导出下载word格式订单失败");
        }
        //}
    }
}
