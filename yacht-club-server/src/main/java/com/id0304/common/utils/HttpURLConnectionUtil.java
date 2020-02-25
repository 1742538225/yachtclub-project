package com.id0304.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;

/**
 * @Author WuZhengHua
 * @Description TODO HttpURLConnectionUtil发送请求工具类
 * @Date 2019/9/29 10:06
 */
@Slf4j
public class HttpURLConnectionUtil {

    public static String httpURLConnection(String str){
        try {
            log.info("#####开始读取url:{}的资源",str);
            URL url = new URL(str.replace("blob:",""));
            //得到connection对象。
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("POST");
            //连接
            connection.connect();
            //得到响应码
            int responseCode = connection.getResponseCode();
            //存放获取到的结果
            StringBuilder returnStr = new StringBuilder();
            if(responseCode == HttpURLConnection.HTTP_OK){
                //得到响应流
                InputStream inputStream = connection.getInputStream();
                //获取响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    returnStr.append(line);
                }
                reader.close();
                //断开连接
                connection.disconnect();
                log.info("#####完成读取url:{}的资源",str);
            }
            return returnStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####读取url:{}的资源出现错误",str);
            return null;
        }
    }

}
