package com.sky.dto;

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
    private String method = "POST";

    /**
     * 请求 类型 raw,form,json
     */
    private String req_body_type;
    /**
     * 请求form
     */
    private List<Map<String, String>> req_body_form;

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
    private List<?> req_params;


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

    public String getReq_body_type() {
        return req_body_type;
    }

    public void setReq_body_type(String req_body_type) {
        this.req_body_type = req_body_type;
    }

    public List<Map<String, String>> getReq_body_form() {
        return req_body_form;
    }

    public void setReq_body_form(List<Map<String, String>> req_body_form) {
        this.req_body_form = req_body_form;
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

    public List<?> getReq_params() {
        return req_params;
    }

    public void setReq_params(List<?> req_params) {
        this.req_params = req_params;
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
                ", req_body_type='" + req_body_type + '\'' +
                ", req_body_form=" + req_body_form +
                ", desc='" + desc + '\'' +
                ", menu='" + menu + '\'' +
                ", req_params=" + req_params +
                '}';
    }
}
