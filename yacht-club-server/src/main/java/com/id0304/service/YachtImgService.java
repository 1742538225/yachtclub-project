package com.id0304.service;

import org.springframework.web.multipart.MultipartFile; /**
 * @Author WuZhengHua
 * @Description TODO 游艇图片服务层接口
 * @Date 13:20 2019/9/29
 **/
public interface YachtImgService {
    String addYachtImg(MultipartFile[] files, Long yachtId);

    Integer deleteImgFileByYachtId(Long yachtId);

    String addHomeImg(MultipartFile[] files);
}
