package com.xjx.mydns.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.transform.Result;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj implements Serializable {
    private String code;
    private String msg;
    private Object data;

    public static ResultObj success(String code, String msg, Object data){
        ResultObj ans = new ResultObj();
        ans.setCode(code);
        ans.setMsg(msg);
        ans.setData(data);
        return ans;
    }
    public static ResultObj success(Object data){
        ResultObj ans = success(CommonEnum.SEND_SUCCESS.getErrorCode(), CommonEnum.SEND_SUCCESS.getErrorMsg(), data);
        return ans;
    }
    public static ResultObj success(String msg){
        return success(CommonEnum.SEND_SUCCESS.getErrorCode(),msg,null);
    }

    public static ResultObj failure(String code, String msg, Object data){
        ResultObj ans = new ResultObj();
        ans.setCode(code);
        ans.setMsg(msg);
        ans.setData(data);
        return ans;
    }
    public static ResultObj failure(){
        ResultObj ans = new ResultObj();
        ans.setData(null);
        ans.setMsg(CommonEnum.INNER_ERROR.getErrorMsg());
        ans.setCode(CommonEnum.INNER_ERROR.getErrorCode());
        return ans;
    }
    public static ResultObj failure(String msg){
        ResultObj ans = failure(CommonEnum.SEND_FAILURE.getErrorCode(), msg, null);
        return ans;
    }
}
