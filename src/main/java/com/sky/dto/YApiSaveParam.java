package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * yApi 保存请求参数
 *
 * @author gangyf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class YApiSaveParam implements Serializable {

    private String id;


    /**
     * 项目 token  唯一标识
     */
    private String token;

    /**
     * 请求参数
     */
    @JsonProperty("req_query")
    private List<ValueWrapper> reqQuery;

    /**
     * header
     */
    @JsonProperty("req_headers")
    private List<ValueWrapper> reqHeaders;

    /**
     * 请求参数 form 类型
     */
    @JsonProperty("req_body_form")
    private List<Map<String, Object>> reqBodyForm;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类id
     */
    @JsonProperty("catid")
    private String catId;

    /**
     * 请求数据类型   raw,form,json
     */
    @JsonProperty("req_body_type")
    private String reqBodyType;

    /**
     * 请求数据body
     */
    @JsonProperty("req_body_other")
    private String reqBodyOther;

    /**
     * 请求参数body 是否为json_schema
     */
    @JsonProperty("req_body_is_json_schema")
    private boolean reqBodyIsJsonSchema;

    /**
     * 路径
     */
    private String path;

    /**
     * 状态 undone,默认done
     */
    private String status;

    /**
     * 返回参数类型  json
     */
    @JsonProperty("res_body_type")
    private String resBodyType;

    /**
     * 返回参数
     */
    @JsonProperty("res_body")
    private String resBody;

    /**
     * 返回参数是否为json_schema
     */
    @JsonProperty("res_body_is_json_schema")
    private boolean resBodyIsJsonSchema;

    /**
     * 创建的用户名
     */
    @JsonProperty("edit_uid")
    private Integer editUid;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 邮件开关
     */
    @JsonProperty("switch_notice")
    private boolean switchNotice;

    private String message;

    /**
     * 文档描述
     */
    private String desc;

    /**
     * 请求方式
     */
    private String method;
    /**
     * 请求参数
     */
    @JsonProperty("req_params")
    private List<ValueWrapper> reqParams;


    /**
     * 项目id
     */
    @JsonProperty("project_id")
    private Integer projectId;

    /**
     * yapi 地址
     */
    private String yapiUrl;
    /**
     * 菜单名称
     */
    private String menu;


}
