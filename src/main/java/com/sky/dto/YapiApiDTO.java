package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * yapi dto
 *
 * @author gangyf
 * @since 2019/2/11 3:16 PM
 */
@Data
public class YapiApiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路径
     */
    private String path;
    /**
     * 请求参数
     */
    private List<ValueWrapper> params;
    /**
     * 头信息
     */
    private List<ValueWrapper> header;
    /**
     * title
     */
    private String title;
    /**
     * 响应
     */
    private String response;
    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求 类型 raw,form,json
     */
    @JsonProperty("req_body_type")
    private String reqBodyType;
    /**
     * 请求form
     */
    @JsonProperty("req_body_form")
    private List<Map<String, Object>> reqBodyForm;

    /**
     * 描述
     */
    private String desc;
    /**
     * 菜单
     */
    private String menu;

    /**
     * 请求参数
     */
    @JsonProperty("req_params")
    private List<ValueWrapper> reqParams;

    /**
     * 校验参数合法性
     */
    public void check() {

    }

}
