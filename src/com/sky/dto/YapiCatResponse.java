package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * 分类列表
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/1 10:30 AM
 */
public class YapiCatResponse implements Serializable {

    /**
     * id
     */
    @JsonProperty("_id")
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 项目id
     */
    private Integer project_id;
    /**
     * 描述
     */
    private String desc;
    /**
     * uid
     */
    private Integer uid;
    /**
     * 排序
     */
    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
