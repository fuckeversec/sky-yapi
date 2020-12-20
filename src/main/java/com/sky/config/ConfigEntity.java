package com.sky.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sky.upload.Cookie;
import java.util.List;
import lombok.Data;

/**
 * Description: ConfigEntity
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @author gangyf
 * @version 1.0 2019年06月14日 18:45
 */
@Data
public class ConfigEntity {

    private String moduleName;

    private String projectToken;

    private String projectType;

    private Integer projectId;

    @JsonProperty("yApiUrl")
    private String yApiUrl;

    /**
     * The Menu.
     */
    private String menu;

    @JsonIgnore
    private List<Cookie> cookies;

    private String status = "undone";

    private boolean reqBodyIsJsonSchema = false;

    private boolean resBodyIsJsonSchema = false;

}
