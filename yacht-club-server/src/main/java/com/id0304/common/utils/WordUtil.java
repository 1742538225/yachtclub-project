package com.id0304.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.id0304.entity.Consume;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

/**
 * @Author WuZhengHua
 * @Description TODO word文档生成工具类
 * @Date 20:37 2019/9/20
 **/
@Component
public class WordUtil {
    private Configuration configuration = null;

    @Value("${memberOrdersAddress}")
    private String memberOrders;

    @Value("${orderFtlAddress}")
    private String orderFtl;

    public WordUtil() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 将文本信息转为word输出
     * @Date 20:53 2019/9/20
     **/
    private File createDoc(Consume consume) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        getData(dataMap, consume); //创建数据
//        configuration.setClassForTemplateLoading(this.getClass(), ""); //模板文件所在路径
        configuration.setDirectoryForTemplateLoading(new File(orderFtl));
//        configuration.setServletContextForTemplateLoading(getServletContext(),"/templates");
        Template t = null;

        String fileName = memberOrders+"订单编号" + consume.getOrderId() + ".doc";
        File file = new File(fileName);
        try {
            t = configuration.getTemplate("memberOrder.ftl"); //获取模板文件
            t.setEncoding("utf-8");

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
            t.process(dataMap, out);  //将填充数据填入模板文件并输出到目标文件
            out.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 导出word 并提供下载
     * @Date 20:53 2019/9/20
     **/
    public void download(HttpServletResponse response, Consume consume) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File file = createDoc(consume);
            String fileName = "订单编号" + consume.getOrderId() + ".doc";

            response.setContentType("application/msword;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=\""
                    + new String(fileName.getBytes(), "iso-8859-1") + "\"");

            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());

            byte[] buff = new byte[10240];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bis.close();
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 获取数据
     * @Date 20:53 2019/9/20
     **/
    private void getData(Map<String, Object> dataMap, Consume consume) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataMap.put("orderId", consume.getOrderId());
        dataMap.put("memberId", consume.getMemberId());
        dataMap.put("tel", consume.getTel().toString());
        dataMap.put("memberName", consume.getName());
        dataMap.put("yachtId", consume.getYacht().getId());
        dataMap.put("yachtName", consume.getYacht().getName());
        dataMap.put("color", consume.getYacht().getColor());
        dataMap.put("length", consume.getYacht().getLength());
        dataMap.put("members", consume.getYacht().getMembers());
        dataMap.put("address", consume.getYacht().getAddress());
        String schemeTime;
        if (consume.getYachtScheme() == null) {
            schemeTime = "无";
        } else {
            schemeTime = consume.getYachtScheme().getTime() + "小时包艇";
        }
        dataMap.put("schemeTime", schemeTime);
        String useTime;
        if (consume.getUseTime() != null) {
            useTime = consume.getUseTime().toString() + "";
        } else {
            useTime = "无";
        }
        dataMap.put("useTime", useTime);
        String type = "";
        if (consume.getYacht().getState().equals(1)) {
            type = "租赁";
        } else if (consume.getYacht().getState().equals(2)) {
            type = "购买";
        }
        dataMap.put("type", type);
        String state = "";
        if (consume.getState().equals(0)) {
            state = "未支付";
        } else if (consume.getState().equals(1)) {
            state = "已支付";
        } else {
            state = "订单过期";
        }
        dataMap.put("state", state);
        dataMap.put("price", consume.getPrice() / 100);
        dataMap.put("consumeCreated", consume.getCreated().toString());
        dataMap.put("created", DateUtil.getTimestamp().toString());
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 获得图片的base64编码
     * @Date 20:53 2019/9/20
     **/
    private String getBase64(String imgUrl) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        URL url = null;
        InputStream in = null;
        HttpURLConnection httpUrl = null;
        byte[] by = new byte[1024];

        try {
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            in = httpUrl.getInputStream();
            int len = -1;
            while ((len = in.read(by)) != -1) {
                data.write(by, 0, len);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }

}