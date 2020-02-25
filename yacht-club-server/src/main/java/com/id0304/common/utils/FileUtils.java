package com.id0304.common.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/*
 * @author WuZhengHua
 * @describe 文件处理工具类
 * @date 2019/11/18
 */
@Slf4j
@SuppressWarnings("all")
public class FileUtils {
    static BASE64Encoder encoder = new BASE64Encoder();
    static BASE64Decoder decoder = new BASE64Decoder();

    /**
     * 更改文件名
     *
     * @param filePath    文件全路径
     * @param newFileName 文件新名称
     * @return 返回新命名文件的全路径
     */
    public static String renameFile(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName)) // 文件名不能为空
            return null;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return newFilePath;
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 删除文件夹或文件名  传入完整路径
     * @Date 15:41 2019/10/24
     **/
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("#####删除文件失败:" + filePath + "不存在！");
            return false;
        } else {
            file.delete();
            log.info("#####删除文件: {} 成功", filePath);
            return true;
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 文件下载
     * @Date 20:53 2019/9/20
     **/
    public static void download(HttpServletResponse response, File file, String contentType) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            String fileName = file.getName();

            response.setContentType(contentType);
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
     * 写入/覆盖 文件
     *
     * @param url     文件全路径
     * @param content 文件内容
     */
    public static void writeFile(String url, String content) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(url);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("#####文件写入时发生异常");
            }
        }
    }

    /**
     * 读取文件,输出为String格式
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String generateString(File file) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            String line = null;
            StringBuilder stringContent = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringContent.append(line).append("\r\n");
            }
            log.info("#####读出文件 {} 成功", file.getPath());
            return stringContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("#####读文件时出现错误");
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("#####读文件关闭流时出现错误");
            }
        }
    }

    /**
     * 获取文件的base64字符串
     *
     * @param imgPath 文件全路径
     * @param mime    文件扩展类型(如 image/png)
     * @return base64字符串
     */
    public static String getFileBase64(String imgPath, String mime) {
        InputStream in = null;
        byte[] data = null;
        String encode = null; // 返回Base64编码过的字节数组字符串
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            // 读取图片字节数组
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            encode = encoder.encode(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "data:" + mime + ";base64," + encode;
    }
}
