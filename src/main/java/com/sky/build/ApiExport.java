package com.sky.build;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.sky.config.ConfigEntity;

/**
 * @author gangyf
 * @since 2020/12/11 8:57 PM
 */
public interface ApiExport {

    /**
     * Yapi upload.
     *
     * @param anActionEvent the an action event
     * @param project the project
     * @param configEntity the config entity
     */
    void yapiUpload(AnActionEvent anActionEvent, Project project, ConfigEntity configEntity);

    /**
     * 判断当前实现类, 是否支持此API
     *
     * @param apiType the apiType
     * @return the boolean
     */
    default boolean support(String apiType) {
        return apiType().equals(apiType);
    }

    /**
     * Support type string.
     *
     * @return the string
     */
    String apiType();

}
