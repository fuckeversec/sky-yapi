package com.sky.config;

/**
 * Description: ConfigEntity
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @version 1.0 2019年06月14日 18:45
 */
public class ConfigEntity {

    private String projectToken;
    private String projectType;
    private Integer projectId;
    private String yApiUrl;
    private String menu;
    private String cookies;
    private boolean reqBodyIsJsonSchema = true;
    private boolean resBodyIsJsonSchema = true;

    /**
     * Sets project token.
     *
     * @param projectToken the project token
     */
    public void setProjectToken(String projectToken) {
        this.projectToken = projectToken;
    }

    /**
     * Sets project type.
     *
     * @param projectType the project type
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /**
     * Sets project id.
     *
     * @param projectId the project id
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * Sets yapi url.
     *
     * @param yApiUrl the yapi url
     */
    public void setyApiUrl(String yApiUrl) {
        this.yApiUrl = yApiUrl;
    }

    /**
     * Gets project token.
     *
     * @return the project token
     */
    public String getProjectToken() {
        return projectToken;
    }

    /**
     * Gets project type.
     *
     * @return the project type
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * Gets project id.
     *
     * @return the project id
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * Gets yapi url.
     *
     * @return the yapi url
     */
    public String getyApiUrl() {
        return yApiUrl;
    }


    /**
     * Gets menu.
     *
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * Sets menu.
     *
     * @param menu the menu
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public boolean isReqBodyIsJsonSchema() {
        return reqBodyIsJsonSchema;
    }

    public void setReqBodyIsJsonSchema(boolean reqBodyIsJsonSchema) {
        this.reqBodyIsJsonSchema = reqBodyIsJsonSchema;
    }

    public boolean isResBodyIsJsonSchema() {
        return resBodyIsJsonSchema;
    }

    public void setResBodyIsJsonSchema(boolean resBodyIsJsonSchema) {
        this.resBodyIsJsonSchema = resBodyIsJsonSchema;
    }
}
