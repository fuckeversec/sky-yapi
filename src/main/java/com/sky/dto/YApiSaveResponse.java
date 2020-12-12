package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author gangyf
 * @since 2020/12/10 9:41 PM
 */
public class YApiSaveResponse {

    @JsonProperty("_id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
