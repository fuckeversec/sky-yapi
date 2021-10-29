package com.sky.interaction;

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
import com.sky.config.ConfigEntity;
import com.sky.config.PersistentState;
import com.sky.util.NotifyUtil;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;

/**
 * Description: UploadToYApi
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @author sky
 * @version 1.0 2019年06月13日 15:27
 */
public class UploadToYapi extends AnAction {

    public static Project project;

    public final static NotificationGroup NOTIFICATION_GROUP;

    /**
     * 持久化的setting
     */
    private final PersistentState persistentState = PersistentState.getInstance();

    static {
        NOTIFICATION_GROUP = new NotificationGroup("sky-yapi.NotificationGroup", NotificationDisplayType.BALLOON,
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
        ConfigEntity configEntity = getConfigEntity(anActionEvent, project);

        ReadAction.nonBlocking(() -> {
            YapiApiExport yapiApiExport = new YapiApiExport();

            yapiApiExport.yapiUpload(anActionEvent, project, configEntity);

        }).submit(AppExecutorUtil.getAppExecutorService());

    }

    /**
     * 持久化的配置参数转化为object
     *
     * @param anActionEvent the an action event
     * @param project the project
     * @return the config entity
     */
    private ConfigEntity getConfigEntity(AnActionEvent anActionEvent, Project project) {
        // 获取配置
        if (StringUtils.isBlank(persistentState.getConfig())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "get config error: config is blank", NotificationType.ERROR);
        }

        PsiFile psiFile = anActionEvent.getDataContext().getData(CommonDataKeys.PSI_FILE);
        // current file's module's name
        String moduleName = Objects.requireNonNull(ProjectRootManager.getInstance(project).getFileIndex()
                .getModuleForFile(Objects.requireNonNull(psiFile).getVirtualFile())).getName();

        Optional<ConfigEntity> configOptional = persistentState.getConfig(moduleName);
        if (!configOptional.isPresent()) {
            throw new RuntimeException("未找到配置, module: " + moduleName);
        }

        return configOptional.get();
    }

}
