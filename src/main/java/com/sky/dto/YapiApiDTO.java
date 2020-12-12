package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * yapi dto
 *
 * @author gangyf
 * @since 2019/2/11 3:16 PM
 */
public class YapiApiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路径
     */
    private String path;
    /**
     * 请求参数
     */
    private List<YapiQueryDTO> params;
    /**
     * 头信息
     */
    private List<?> header;
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
    private List<Map<String, String>> reqBodyForm;

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
    private List<?> reqParams;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<YapiQueryDTO> getParams() {
        return params;
    }

    public void setParams(List<YapiQueryDTO> params) {
        this.params = params;
    }

    public List<?> getHeader() {
        return header;
    }

    public void setHeader(List<?> header) {
        this.header = header;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReqBodyType() {
        return reqBodyType;
    }

    public void setReqBodyType(String reqBodyType) {
        this.reqBodyType = reqBodyType;
    }

    public List<Map<String, String>> getReqBodyForm() {
        return reqBodyForm;
    }

    public void setReqBodyForm(List<Map<String, String>> reqBodyForm) {
        this.reqBodyForm = reqBodyForm;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public List<?> getReqParams() {
        return reqParams;
    }

    public void setReqParams(List<?> reqParams) {
        this.reqParams = reqParams;
    }

    @Override
    public String toString() {
        return "YapiApiDTO{" +
                "path='" + path + '\'' +
                ", params=" + params +
                ", header=" + header +
                ", title='" + title + '\'' +
                ", response='" + response + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", method='" + method + '\'' +
                ", req_body_type='" + reqBodyType + '\'' +
                ", req_body_form=" + reqBodyForm +
                ", desc='" + desc + '\'' +
                ", menu='" + menu + '\'' +
                ", req_params=" + reqParams +
                '}';
    }
}
