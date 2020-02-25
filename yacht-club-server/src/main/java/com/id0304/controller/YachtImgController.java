package com.id0304.controller;

import com.id0304.common.controller.BaseController;
import com.id0304.service.YachtImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇图片控制层
 * @Date 2019/9/29 13:19
 */
@RestController
@RequestMapping("/api/yachtImg")
public class YachtImgController extends BaseController{
    @Autowired
    private YachtImgService yachtImgService;

    /**
     * @Author WuZhengHua
     * @Description TODO 保存上传的游艇图片
     * @Date 21:27 2019/9/29
     **/
    @PostMapping("/admin/addYachtImg.action")
    public Map<String,Object> addYachtImg(@RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam("yachtId") Long yachtId) {
        if(files!=null&&files.length>=2&&yachtId!=null){
            String data = yachtImgService.addYachtImg(files,yachtId);
            return setSuccess(data);
        }else{
            return setParamError();
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 首页展览图上传
     * @Date 17:02 2019/10/15
     **/
    @PostMapping("/admin/addHomeImg.action")
    public Map<String,Object> addHomeImg(@RequestParam(value = "files", required = false) MultipartFile[] files){
        if(files!=null){
            String data = yachtImgService.addHomeImg(files);
            return setSuccess(data);
        }else{
            return setParamError();
        }
    }
}
