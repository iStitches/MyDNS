package com.xjx.mydns.constant;

import com.xjx.mydns.exception.GlobalException;

public enum  CommonEnum implements GlobalException {
    SEND_SUCCESS("200","消息发送成功"),
    SEND_FAILURE("400","消息发送失败"),
    INNER_ERROR("500","服务器内部出错"),
    DATA_NOTFOUND("500100","没有找到对应的资源数据"),
    PARAMS_FROMAT_ERROR("500101","请求参数异常"),
    PARAMS_IP_FROMAT_ERROR("500102","配置的IP地址格式错误,请检查,范围为 1.0.0.1 ~ 223.255.255.255"),
    PARAMS_TIME_FORMAT_ERROR("500103","配置的起始和终止访问时间格式错误,请检查"),
    PARAMS_MATCHMODE_ERROR("500104","域名匹配模式错误,只能为prefix、suffix、contains中的一种"),
    PRAMAS_DISPATCHMODE_ERROR("500105","IP地址分配模式错误,只能为random、hash、round-robin中的一种"),
    PRAMAS_RULENAME_ERROR("500106","域名匹配格式错误,请检查"),
    MESSAGE_MQ_RECEIVE_ERROR("500107","消息队列接收消息失败")
    ;

    private String code;
    private String msg;
    @Override
    public String getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
    CommonEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
