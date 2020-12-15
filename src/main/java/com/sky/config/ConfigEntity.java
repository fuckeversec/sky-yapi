package com.sky.config;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String projectToken;

    private String projectType;

    private Integer projectId;

    @JsonProperty("yApiUrl")
    private String yApiUrl;

    private String menu;

    private String cookies;

    private String status = "undone";

    private boolean reqBodyIsJsonSchema = false;

    private boolean resBodyIsJsonSchema = false;

}
