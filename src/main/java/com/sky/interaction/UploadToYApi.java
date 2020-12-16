package com.sky.interaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.sky.build.YapiApiExport;
import com.sky.config.Config;
import com.sky.config.ConfigEntity;
import com.sky.config.PersistentState;
import com.sky.util.JsonUtil;
import com.sky.util.NotifyUtil;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

/**
 * Description: UploadToYApi
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @author sky
 * @version 1.0 2019年06月13日 15:27
 */
public class UploadToYApi extends AnAction {

    public static Project project;

    public final static NotificationGroup NOTIFICATION_GROUP;

    /**
     * 持久化的setting
     */
    private PersistentState persistentState = PersistentState.getInstance();

    static {
        NOTIFICATION_GROUP = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON,
                true);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) {
            return;
        }

        project = editor.getProject();

        // 获取配置
        ConfigEntity configEntity;
        try {
            configEntity = this.getConfigEntity(anActionEvent, project);
        } catch (JsonProcessingException e) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "config deserializer fail, please check config",
                    NotificationType.ERROR);

            return;
        }

        validateConfig(configEntity);

        ReadAction.nonBlocking(() -> {
            YapiApiExport yapiApiExport = new YapiApiExport();

            yapiApiExport.yapiUpload(anActionEvent, project, configEntity);

        }).submit(AppExecutorUtil.getAppExecutorService());

    }

    /**
     * 检测配置文件
     *
     * @param configEntity the config entity
     */
    private void validateConfig(ConfigEntity configEntity) {
        // 配置校验
        boolean tokenCookiesAllEmpty = Strings.isNullOrEmpty(configEntity.getProjectToken()) && Strings
                .isNullOrEmpty(configEntity.getCookies());
        if (tokenCookiesAllEmpty) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [projectToken, cookies], must set one",
                    NotificationType.ERROR);
        }

        if (Objects.isNull(configEntity.getProjectId())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [projectId]", NotificationType.ERROR);
        }

        if (Strings.isNullOrEmpty(configEntity.getYApiUrl())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [yApiUrl]", NotificationType.ERROR);
        }

        if (Strings.isNullOrEmpty(configEntity.getProjectType())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [projectType]", NotificationType.ERROR);
        }
    }


    /**
     * 持久化的配置参数转化为object
     *
     * @param anActionEvent the an action event
     * @param project the project
     * @return the config entity
     */
    private ConfigEntity getConfigEntity(AnActionEvent anActionEvent, Project project) throws JsonProcessingException {
        ConfigEntity configEntity = null;
        // 获取配置
        if (StringUtils.isBlank(persistentState.getConfig())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "get config error: config is blank", NotificationType.ERROR);
        }
        Config config = JsonUtil.OBJECT_MAPPER.readValue(persistentState.getConfig(), Config.class);
        String cookies = persistentState.getCookies();
        if (config.isSingle()) {
            configEntity = config.getSingleConfig();
        } else {
            Map<String, ConfigEntity> multipleConfig = config.getMultipleConfig();
            PsiFile psiFile = anActionEvent.getDataContext().getData(CommonDataKeys.PSI_FILE);
            // current file's module's name
            String moduleName = Objects.requireNonNull(ProjectRootManager.getInstance(project).getFileIndex()
                    .getModuleForFile(Objects.requireNonNull(psiFile).getVirtualFile())).getName();
            if (StringUtils.isNotBlank(psiFile.getVirtualFile().getPath())) {
                configEntity = multipleConfig.entrySet().stream()
                        .filter(m -> m.getKey().equals(moduleName))
                        .map(Entry::getValue).findFirst().orElse(null);
            }
            if (configEntity == null) {
                throw new RuntimeException("未找到配置, module: " + moduleName);
            }
        }
        configEntity.setCookies(cookies);
        return configEntity;
    }

}
