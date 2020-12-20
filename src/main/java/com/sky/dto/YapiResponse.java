package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * yapi 返回结果
 *
 * @author gangyf
 * @since 2019/1/31 12:08 PM
 */
@Data
public class YapiResponse<T> implements Serializable {

    /**
     * 状态码
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 状态信息
     */
    @JsonProperty("errmsg")
    private String errMsg;
    /**
     * 返回结果
     */
    private T data;

    public YapiResponse() {
    }

    public YapiResponse(T data) {
        this.errCode = 0;
        this.errMsg = "success";
        this.data = data;
    }

    public YapiResponse(Integer errcode, String errmsg) {
        this.errCode = errcode;
        this.errMsg = errmsg;
    }
}
