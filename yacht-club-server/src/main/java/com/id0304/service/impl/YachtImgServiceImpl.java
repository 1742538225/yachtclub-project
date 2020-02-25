package com.id0304.service.impl;

import com.id0304.common.utils.DateUtil;
import com.id0304.dao.YachtImgDao;
import com.id0304.entity.YachtImg;
import com.id0304.service.YachtImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇图片服务层接口实现类
 * @Date 2019/9/29 13:19
 */
@Service
@Slf4j
public class YachtImgServiceImpl implements YachtImgService {
    @Autowired
    private YachtImgDao yachtImgDao;

    @Value("${yachtImg.prefix}")
    private String yachtImgAddress;

    @Value("${yachtImg.suffix}")
    private String yachtImgType;

    @Value("${homeImg.prefix}")
    private String homeImgAddress;

    @Value("${homeImg.suffix}")
    private String homeImgType;

    /**
     * @Author WuZhengHua
     * @Description TODO 上传游艇图片服务层接口实现类 返回1:成功  2:参数错误  3:服务器异常
     * @Date 21:33 2019/9/29
     **/
    @Override
    public String addYachtImg(MultipartFile[] files, Long yachtId) {
        try {
            yachtImgDao.deleteYachtImgByYachtId(yachtId);
            File folder = new File(yachtImgAddress);
            File[] delFiles = folder.listFiles();
            if (delFiles != null && delFiles.length > 0) {
                for (File file : delFiles) {
                    if (file.getName().contains(yachtId + "_")) {      //删除文件名带有       游艇id_*.png      的文件
                        file.delete();
                    }
                }
            }
            log.info("#####成功删除游艇id为{}的游艇图片", yachtId);

            List<YachtImg> yachtImgList = new ArrayList<YachtImg>();
            int i = 1;
            for (MultipartFile file : files) {
                String imgName = yachtId + "_" + (i >= 10 ? i : "0" + i);
                String filePath = yachtImgAddress + imgName + yachtImgType;
                File desFile = new File(filePath);
                if (!desFile.getParentFile().exists()) {
                    desFile.mkdirs();
                }
                file.transferTo(desFile);

                YachtImg yachtImg = new YachtImg();
                yachtImg.setYachtId(yachtId);
                yachtImg.setImg(yachtImgAddress+imgName + yachtImgType);
                yachtImg.setCreated(DateUtil.getTimestamp());
                yachtImg.setUpdated(DateUtil.getTimestamp());
                yachtImgList.add(yachtImg);
                ++i;
            }
            yachtImgDao.addYachtImgList(yachtImgList);
            log.info("#####游艇图片添加成功");
            return null;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            log.error("#####服务器异常");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇id删除对应的存在磁盘里的展示图
     * @Date 16:16 2019/10/3
     **/
    @Override
    public Integer deleteImgFileByYachtId(Long yachtId) {
        try {
            File folder = new File(yachtImgAddress);
            File[] delFiles = folder.listFiles();
            if (delFiles != null && delFiles.length > 0) {
                for (File file : delFiles) {
                    if (file.getName().contains(yachtId + "_")) {      //删除文件名带有       游艇id_*.png      的文件
                        file.delete();
                    }
                }
            }
            log.info("#####成功删除游艇id为{}的游艇图片", yachtId);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器异常,删除游艇id为{}的游艇图片失败", yachtId);
            return 2;
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 修改主页展览图
     * @Date 17:29 2019/10/15
     **/
    @Override
    public String addHomeImg(MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                String filePath = homeImgAddress + file.getResource().getFilename() + homeImgType;
                File desFile = new File(filePath);
                desFile.mkdirs();
                file.transferTo(desFile);
            }
            log.info("#####成功修改首页图片");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("#####修改首页图片错误,服务器错误");
            return "服务器错误";
        }
    }
}
