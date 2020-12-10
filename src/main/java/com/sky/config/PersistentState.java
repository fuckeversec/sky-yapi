package com.sky.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import java.util.Objects;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;


/**
 * Description: 持久化
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 * 持久化文件地址
 *
 * @author gangyf
 * @version 1.0 2019年06月14日 15:02
 */
@State(name = "YApiConfig", storages = {@com.intellij.openapi.components.Storage(value = "$APP_CONFIG$/YApiConfig.xml")})
public class PersistentState implements PersistentStateComponent<Element> {

    private String config = "";
    private String cookies = "";

    /**
     * 服务管理器获取实例
     *
     * @return PersistentState instance
     */
    public static PersistentState getInstance() {
        return ServiceManager.getService(PersistentState.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("YApiConfig");
        element.setAttribute("config", this.getConfig());
        element.setAttribute("cookies", this.getCookies());
        return element;
    }

    @Override
    public void loadState(Element element) {
        this.config = element.getAttributeValue("config");
        this.cookies = element.getAttributeValue("cookies");
    }


    /**
     * Gets config.
     *
     * @return the config
     */
    public String getConfig() {
        return Objects.isNull(config) ? "" : config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * Gets cookies.
     *
     * @return the cookies
     */
    public String getCookies() {
        return Objects.isNull(cookies) ? "" : cookies;
    }

    /**
     * Sets cookies.
     *
     * @param cookies the cookies
     */
    public void setCookies(String cookies) {
        this.cookies = cookies;
    }
}
