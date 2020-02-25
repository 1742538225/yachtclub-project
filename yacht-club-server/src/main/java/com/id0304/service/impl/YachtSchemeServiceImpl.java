package com.id0304.service.impl;

import com.id0304.dao.YachtDao;
import com.id0304.dao.YachtSchemeDao;
import com.id0304.entity.YachtScheme;
import com.id0304.service.YachtSchemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author WuZhengHua
 * @Description TODO 套餐服务接口实现类
 * @Date 2019/9/15 23:04
 */
@Service
@Slf4j
public class YachtSchemeServiceImpl implements YachtSchemeService {
    @Autowired
    private YachtSchemeDao yachtSchemeDao;

    @Override
    public YachtScheme getSchemeById(Long schemeId) {
        try {
            return yachtSchemeDao.getSchemeById(schemeId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
