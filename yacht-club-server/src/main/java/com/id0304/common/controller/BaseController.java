package com.id0304.common.controller;

import com.id0304.common.contants.BaseApiConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 公共控制层 用于响应map集合格式报文数据
 * @Date 2019/10/9 17:25
 */
public class BaseController {
    /**
     * @Author WuZhengHua
     * @Description TODO 响应成功(不带数据)
     * @Date 17:26 2019/10/9
     **/
    public static Map<String, Object> setSuccess() {
        return setResult(BaseApiConstants.HTTP_200_CODE,BaseApiConstants.HTTP_SUCCESS_NAME,null);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 响应成功(带数据)
     * @Date 17:26 2019/10/9
     **/
    public static Map<String, Object> setSuccess(String data) {
        return setResult(BaseApiConstants.HTTP_200_CODE,BaseApiConstants.HTTP_SUCCESS_NAME,data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 响应错误(参数错误)
     * @Date 17:31 2019/10/9
     **/
    public static Map<String,Object> setParamError(){
        return setResult(BaseApiConstants.HTTP_400_CODE,BaseApiConstants.HTTP_ERROR_NAME,null);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 响应错误(服务器异常)
     * @Date 17:31 2019/10/9
     **/
    public static Map<String,Object> setError(){
        return setResult(BaseApiConstants.HTTP_500_CODE,BaseApiConstants.HTTP_ERROR_NAME,null);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 自定义返回,供上面直接调用
     * @Date 17:29 2019/10/9
     **/
    private static Map<String, Object> setResult(Integer code, String msg, Object data) {
        HashMap<String, Object> result = new HashMap<>();
        if(code != null){
            result.put(BaseApiConstants.HTTP_CODE_NAME, code);
        }
        if(msg!=null){
            result.put(BaseApiConstants.HTTP_MESSAGE_NAME, msg);
        }
        if (data != null){
            result.put(BaseApiConstants.HTTP_DATA_NAME, data);
        }
        return result;
    }
}
