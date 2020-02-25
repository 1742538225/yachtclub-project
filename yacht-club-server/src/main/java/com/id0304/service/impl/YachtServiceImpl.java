package com.id0304.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.id0304.common.contants.FileContants;
import com.id0304.common.contants.RedisContants;
import com.id0304.common.contants.TableName;
import com.id0304.common.utils.BaseRedisService;
import com.id0304.common.utils.DateUtil;
import com.id0304.common.utils.FileUtils;
import com.id0304.common.utils.HttpURLConnectionUtil;
import com.id0304.dao.ConsumeDao;
import com.id0304.dao.YachtDao;
import com.id0304.dao.YachtSchemeDao;
import com.id0304.entity.Consume;
import com.id0304.entity.Yacht;
import com.id0304.entity.YachtImg;
import com.id0304.entity.YachtScheme;
import com.id0304.service.YachtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇服务接口实现类
 * @Date 2019/9/5 22:51
 */
@Service
@Slf4j
public class YachtServiceImpl implements YachtService {
    @Autowired
    private YachtDao yachtDao;

    @Autowired
    private YachtSchemeDao yachtSchemeDao;

    @Autowired
    private ConsumeDao consumeDao;

    @Autowired
    private YachtImgServiceImpl yachtImgService;

    @Autowired
    private BaseRedisService baseRedisService;

    @Override
    public String getYachts(String condition, String minLength, String maxLength, String minMembers, String maxMembers, String state, String maintenance, String page, String counts) {
        Yacht yacht = new Yacht();
        if (!state.equals("")) {
            yacht.setState(Integer.parseInt(state));
        }
        if (!maintenance.equals("") && maintenance.matches("[0-9]+")) {       //维护时间必为纯数字
            yacht.setMaintenance(Long.parseLong(maintenance));
        }
        if (!condition.equals("")) {
            condition = "%" + condition + "%";
        } else {
            condition = null;
        }
        if (minLength.equals("")) {
            minLength = null;
        }
        if (maxLength.equals("")) {
            maxLength = null;
        }
        if (minMembers.equals("")) {
            minMembers = null;
        }
        if (maxMembers.equals("")) {
            maxMembers = null;
        }
        Integer index = (Integer.parseInt(page) - 1) * Integer.parseInt(counts);    //分页查询第一条记录的索引

        List<List<?>> resultList = yachtDao.getYachtsByConAndPage(condition, yacht, minLength, maxLength, minMembers, maxMembers);
        List<Yacht> yachtList = (List<Yacht>) resultList.get(0);      //结果集
        yachtList = yachtList.subList(index,index+Integer.parseInt(counts)<yachtList.size()?index+Integer.parseInt(counts):yachtList.size());

        for(Yacht yachtByList:yachtList){
            String img = yachtByList.getYachtImgList().get(0).getImg();
            String fileBase64 = FileUtils.getFileBase64(img,FileContants.IMAGE_JPG);
            yachtByList.getYachtImgList().get(0).setImg(fileBase64);
        }

        Long listCounts = (Long) resultList.get(1).get(0);   //分页查询数据总数

        String[] arr = new String[]{JSON.toJSONString(yachtList), listCounts + ""};
        return JSON.toJSONString(arr);
    }

    @Override
    public String getYachtsAndMainPicByState(Integer state) {
        try {
            String yachtListJson = null;
            if(state.equals(1)) {//租赁
                yachtListJson = baseRedisService.get(RedisContants.REDIS_KEY_YACHTS_HIRE);
            }else if(state.equals(2)){//销售
                yachtListJson = baseRedisService.get(RedisContants.REDIS_KEY_YACHTS_SALE);
            }
            if(yachtListJson == null){
                List<Yacht> yachtList = yachtDao.getYachtsAndMainPicByState(state);
                for(Yacht yacht:yachtList){
                    String img = yacht.getYachtImgList().get(0).getImg();
                    String fileBase64 = FileUtils.getFileBase64(img, FileContants.IMAGE_JPG);
                    yacht.getYachtImgList().get(0).setImg(fileBase64);
                }
                yachtListJson = JSON.toJSONString(yachtList);
                if(state.equals(1)) {//租赁
                    baseRedisService.setString(RedisContants.REDIS_KEY_YACHTS_HIRE,yachtListJson,RedisContants.REDIS_TIMEOUT_YACHT_30DAY);
                }else if(state.equals(2)){//销售
                    baseRedisService.setString(RedisContants.REDIS_KEY_YACHTS_SALE,yachtListJson,RedisContants.REDIS_TIMEOUT_YACHT_30DAY);
                }
                log.info("#####从数据库查询并向redis存入游艇与主图名称信息");
                return yachtListJson;
            }else{
                log.info("#####redis获取游艇与主图名称信息");
                return yachtListJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误,获取游艇列表主图失败");
            return "服务器错误";
        }
    }

    @Override
    public String getYachtMessage(Long id) {
        try {
            String yachtJson = baseRedisService.get(RedisContants.REDIS_KEY_YACHT_PREFIX+id);
            if(yachtJson == null){
                String[] json = new String[2];
                Yacht yacht = yachtDao.getYachtAndImgById(id);
                for(YachtImg yachtImg:yacht.getYachtImgList()){
                    yachtImg.setImg(FileUtils.getFileBase64(yachtImg.getImg(),FileContants.IMAGE_JPG));
                }
                json[0] = JSON.toJSONString(yacht);
                List<YachtScheme> yachtSchemeList = yachtSchemeDao.getSchemeByYachtId(id);
                json[1] = JSON.toJSONString(yachtSchemeList);
                yachtJson = JSON.toJSONString(json);
                log.info("#####根据游艇id = {} 查询到游艇",id);
                log.info("#####redis中存入游艇id = {} 的游艇信息",id);
                baseRedisService.setString(RedisContants.REDIS_KEY_YACHT_PREFIX+id,yachtJson,RedisContants.REDIS_TIMEOUT_YACHT_30DAY);
                return yachtJson;
            }else{
                log.info("#####redis中读取游艇id = {} 的游艇信息",id);
                return yachtJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####根据游艇id获取游艇信息出现服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 添加游艇业务层,完成添加游艇和游艇套餐
     * @Date 9:54 2019/9/29
     **/
    @Override
    public String addYacht(Yacht yacht) {
        try {
            yacht.setCreated(DateUtil.getTimestamp());
            yacht.setUpdated(DateUtil.getTimestamp());
            yachtDao.addYacht(yacht);
            Long id = yacht.getId();
            if (yacht.getYachtSchemeList() != null && yacht.getYachtSchemeList().size() > 0) {
                for (YachtScheme yachtScheme : yacht.getYachtSchemeList()) {
                    yachtScheme.setYachtId(id);
                    yachtScheme.setCreated(DateUtil.getTimestamp());
                    yachtScheme.setUpdated(DateUtil.getTimestamp());
                }
                yachtSchemeDao.addYachtSchemeList(yacht.getYachtSchemeList());
            }
            log.info("#####成功插入游艇和游艇套餐信息,返回游艇最新id={}", id);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_HIRE);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_SALE);
            log.info("#####移除redis中的游艇与主图列表信息");
            return id+"";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("插入游艇和游艇套餐信息失败,服务器异常");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 更新游艇业务层,完成更新游艇和游艇套餐
     * @Date 16:17 2019/10/2
     **/
    @Override
    public String updateYacht(Yacht yacht) {
        try {
            yacht.setUpdated(DateUtil.getTimestamp());
            yachtDao.updateYacht(yacht);
            Long id = yacht.getId();
            if (yacht.getYachtSchemeList() != null && yacht.getYachtSchemeList().size() > 0) {
                for (YachtScheme yachtScheme : yacht.getYachtSchemeList()) {
                    yachtScheme.setYachtId(id);
                    yachtScheme.setCreated(DateUtil.getTimestamp());
                    yachtScheme.setUpdated(DateUtil.getTimestamp());
                }
                yachtSchemeDao.deleteYachtSchemeByYachtId(yacht.getId());
                log.info("#####成功删除游艇id为{}的套餐信息", id);
                yachtSchemeDao.addYachtSchemeList(yacht.getYachtSchemeList());
                log.info("#####成功加入游艇id为{}的新套餐信息", id);
            }
            log.info("#####成功修改游艇和游艇套餐信息,游艇id={}", id);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_HIRE);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_SALE);
            log.info("#####移除redis中的游艇与主图列表信息");

            baseRedisService.delete(RedisContants.REDIS_KEY_YACHT_PREFIX+id);
            log.info("#####移除redis中的游艇id={}信息",id);

            return id+"";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("修改游艇和游艇套餐信息失败,服务器异常");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇id删除游艇服务接口实现类
     * @Date 16:03 2019/10/3
     **/
    @Override
    public String deleteYachtById(String yachtId) {
        try {
            List<Consume> consumeIdList = consumeDao.getConsumeIdByYachtId(yachtId);
            if(consumeIdList.size()>0){
                log.error("#####该游艇存在于有效订单中,无法删除");
                return "该游艇包含在有效订单中,无法删除";
            }
            yachtDao.deleteYachtMessage(Long.parseLong(yachtId));
            Integer code = yachtImgService.deleteImgFileByYachtId(Long.parseLong(yachtId));
            if(code == 2){
                return "删除游艇图片文件时服务器异常";       //删除游艇图片文件时服务器异常
            }
            log.info("#####删除游艇id为{}的游艇成功",yachtId);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_HIRE);
            baseRedisService.delete(RedisContants.REDIS_KEY_YACHTS_SALE);
            log.info("#####移除redis中的游艇与主图列表信息");

            baseRedisService.delete(RedisContants.REDIS_KEY_YACHT_PREFIX+yachtId);
            log.info("#####移除redis中的游艇id={}信息",yachtId);

            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("#####服务器异常,删除游艇id为{}的游艇失败",yachtId);
            return "服务器错误";
        }
    }
}
