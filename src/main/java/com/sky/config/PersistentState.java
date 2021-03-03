package com.sky.config;

import static com.sky.interaction.UploadToYapi.NOTIFICATION_GROUP;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.sky.upload.Cookie;
import com.sky.util.JsonUtil;
import com.sky.util.NotifyUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
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
@Setter
@State(name = "YApiConfig", storages = {
        @com.intellij.openapi.components.Storage(value = "$APP_CONFIG$/YApiConfig.xml")})
public class PersistentState implements PersistentStateComponent<Element> {

    private String config;
    private String cookies;

    protected static Map<String, ConfigEntity> CONFIG_CACHE = new HashMap<>(16);
    protected static Map<String, List<Cookie>> COOKIES_CACHE = new HashMap<>(16);
    protected static Map<String, String> MODULE_NAME_TO_HOST = new HashMap<>(16);

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

        refreshCookiesCache(cookies);
        refreshConfigCache(config);
    }


    public String getConfig() {
        return config;
    }

    /**
     * Gets config.
     *
     * @param moduleName the module name
     * @return the config
     */
    public Optional<ConfigEntity> getConfig(String moduleName) {

        return Optional.ofNullable(CONFIG_CACHE.get(moduleName));

    }

    /**
     * Gets cookies.
     *
     * @return the cookies
     */
    public String getCookies() {
        return cookies;
    }

    public void setConfig(String config) {
        this.config = config;
        refreshConfigCache(config);
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
        refreshCookiesCache(cookies);
    }

    /**
     * Refresh cookies cache.
     */
    protected void refreshCookiesCache(String cookies) {
        Optional<Map<String, List<Cookie>>> optional = JsonUtil
                .readValue(Optional.ofNullable(Strings.emptyToNull(cookies)).orElse("{}"),
                        new TypeReference<Map<String, List<Cookie>>>() {});

        optional.ifPresent(val -> COOKIES_CACHE = val);
    }

    /**
     * Refresh config cache.
     *
     * if config is null or blank string, then return []
     */
    protected void refreshConfigCache(String config) {
        Optional<List<ConfigEntity>> optional = JsonUtil
                .readValue(Optional.ofNullable(Strings.emptyToNull(config)).orElse("[]"), new TypeReference<List<ConfigEntity>>() {});

        optional.ifPresent(configEntities -> {
            CONFIG_CACHE = configEntities.stream().collect(Collectors.groupingBy(ConfigEntity::getModuleName,
                            Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));

            CONFIG_CACHE.values().forEach(configEntity -> {

                MODULE_NAME_TO_HOST.compute(configEntity.getModuleName(), (key, value) -> {
                    try {
                        String host = new URL(configEntity.getYApiUrl()).getHost();
                        configEntity.setCookies(COOKIES_CACHE.get(host));
                        return host;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

                if (CollectionUtils.isNotEmpty(configEntity.getCookies())) {
                    configEntity.setProjectToken(null);
                }

                validateConfig(configEntity);
            });
        });
    }


    /**
     * 检测配置文件
     *
     * @param configEntity the config entity
     */
    private void validateConfig(ConfigEntity configEntity) {
        // 配置校验
        boolean tokenCookiesAllEmpty =
                Strings.isNullOrEmpty(configEntity.getProjectToken()) && CollectionUtils.isEmpty(configEntity.getCookies());

        if (tokenCookiesAllEmpty) {
            NotifyUtil.log(NOTIFICATION_GROUP, null, "please check config, [projectToken, cookies], must set one",
                    NotificationType.ERROR);
        }

        if (Objects.isNull(configEntity.getProjectId())) {
            NotifyUtil.log(NOTIFICATION_GROUP, null, "please check config, [projectId]", NotificationType.ERROR);
        }

        if (Strings.isNullOrEmpty(configEntity.getYApiUrl())) {
            NotifyUtil.log(NOTIFICATION_GROUP, null, "please check config, [yApiUrl]", NotificationType.ERROR);
        }

        if (Strings.isNullOrEmpty(configEntity.getProjectType())) {
            NotifyUtil.log(NOTIFICATION_GROUP, null, "please check config, [projectType]", NotificationType.ERROR);
        }
    }

}
