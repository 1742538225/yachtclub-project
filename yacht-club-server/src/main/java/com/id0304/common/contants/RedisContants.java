package com.id0304.common.contants;

public interface RedisContants {
    Long REDIS_TIMEOUT_USER_7DAY = 60*60*24*7L;   //用户登录数据保存7天

    Long REDIS_TIMEOUT_USER_10M = 60*10L;   //订单有效支付时间10分钟

    Long REDIS_TIMEOUT_YACHT_30DAY = 60*60*24*30L;      //游艇信息保存30天

    String REDIS_KEY_YACHTS_HIRE = "hireYachtList";

    String REDIS_KEY_YACHTS_SALE = "saleYachtList";

    String REDIS_KEY_YACHT_PREFIX = "yacht_";
}
