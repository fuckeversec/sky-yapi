package com.sky.interaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.inject.Injector;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.itangcent.intellij.jvm.PsiClassHelper;
import com.sky.build.BuildJsonForDubbo;
import com.sky.build.BuildJsonForYApi;
import com.sky.config.Config;
import com.sky.config.ConfigEntity;
import com.sky.config.PersistentState;
import com.sky.constant.ProjectTypeConstant;
import com.sky.constant.YapiConstant;
import com.sky.dto.YApiSaveParam;
import com.sky.dto.YapiApiDTO;
import com.sky.dto.YapiDubboDTO;
import com.sky.dto.YapiResponse;
import com.sky.upload.UploadYapi;
import com.sky.util.JsonUtil;
import com.sky.util.NotifyUtil;
import java.util.List;
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
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class UploadToYApi extends AnAction {

    public static Project project;

    public final static NotificationGroup NOTIFICATION_GROUP;

    /**
     * 持久化的setting
     */
    private PersistentState persistentState = PersistentState.getInstance();

    public static Injector injector;
    public static PsiClassHelper psiClassHelper;

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

        if (Strings.isNullOrEmpty(configEntity.getyApiUrl())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [yApiUrl]", NotificationType.ERROR);
        }

        if (Strings.isNullOrEmpty(configEntity.getProjectType())) {
            NotifyUtil.log(NOTIFICATION_GROUP, project, "please check config, [projectType]", NotificationType.ERROR);
        }

        // 判断项目类型
        if (ProjectTypeConstant.dubbo.equals(configEntity.getProjectType())) {
            this.dubboApiUpload(anActionEvent, project, configEntity);
        } else if (ProjectTypeConstant.api.equals(configEntity.getProjectType())) {
            this.webApiUpload(anActionEvent, project, configEntity);
        }
    }


    /**
     * Web api upload.
     *
     * @param anActionEvent the e
     * @param project the project
     * @param configEntity 配置文件
     */
    private void webApiUpload(AnActionEvent anActionEvent, Project project, ConfigEntity configEntity) {
        //获得api 需上传的接口列表 参数对象
        List<YapiApiDTO> yApiApiDTOS = new BuildJsonForYApi().actionPerformedList(anActionEvent, null);
        if (Objects.nonNull(yApiApiDTOS)) {
            for (YapiApiDTO yapiApiDTO : yApiApiDTOS) {
                YApiSaveParam yapiSaveParam = new YApiSaveParam(configEntity.getProjectToken(), yapiApiDTO.getTitle(),
                        yapiApiDTO.getPath(),
                        yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(),
                        configEntity.getProjectId(), configEntity.getyApiUrl(), true,
                        yapiApiDTO.getMethod(),
                        yapiApiDTO.getDesc(),
                        yapiApiDTO.getHeader());
                yapiSaveParam.setReq_body_form(yapiApiDTO.getReq_body_form());
                yapiSaveParam.setReq_body_type(yapiApiDTO.getReq_body_type());
                yapiSaveParam.setReq_params(yapiApiDTO.getReq_params());
                yapiSaveParam.setReq_body_is_json_schema(configEntity.isReqBodyIsJsonSchema());
                yapiSaveParam.setRes_body_is_json_schema(configEntity.isResBodyIsJsonSchema());
                if (!Strings.isNullOrEmpty(yapiApiDTO.getMenu())) {
                    yapiSaveParam.setMenu(yapiApiDTO.getMenu());
                } else {
                    yapiSaveParam.setMenu(configEntity.getMenu());
                }
                try {
                    // 上传
                    YapiResponse<?> yapiResponse = new UploadYapi()
                            .uploadSave(yapiSaveParam, configEntity.getCookies());
                    if (yapiResponse.getErrcode() != 0) {
                        NotifyUtil.log(NOTIFICATION_GROUP, project,
                                "sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                    } else {
                        String url =
                                configEntity.getyApiUrl() + "/project/" + configEntity.getProjectId() + "/interface"
                                        + "/api/cat_" + UploadYapi.catMap.get(configEntity.getProjectId())
                                        .get(yapiSaveParam.getMenu());
                        NotifyUtil
                                .log(NOTIFICATION_GROUP, project, "success ,url:  " + url,
                                        NotificationType.INFORMATION);
                    }
                } catch (Exception e) {
                    NotifyUtil.log(NOTIFICATION_GROUP, project, "sorry ,upload api error cause:" + e,
                            NotificationType.ERROR);
                }
            }
        }
    }

    /**
     * Dubbo api upload.
     *
     * @param anActionEvent the e
     * @param project the project
     * @param configEntity configEntity
     */
    private void dubboApiUpload(AnActionEvent anActionEvent, Project project, ConfigEntity configEntity) {
        // 获得dubbo需上传的接口列表 参数对象
        List<YapiDubboDTO> yapiDubboDTOs = new BuildJsonForDubbo().actionPerformedList(anActionEvent);
        if (yapiDubboDTOs != null) {
            for (YapiDubboDTO yapiDubboDTO : yapiDubboDTOs) {
                YApiSaveParam yapiSaveParam = new YApiSaveParam(configEntity.getProjectToken(), yapiDubboDTO.getTitle(),
                        yapiDubboDTO.getPath(), yapiDubboDTO.getParams(), yapiDubboDTO.getResponse(),
                        Integer.valueOf(configEntity.getProjectId()), configEntity.getyApiUrl(),
                        yapiDubboDTO.getDesc());
                if (!Strings.isNullOrEmpty(configEntity.getMenu())) {
                    yapiSaveParam.setMenu(configEntity.getMenu());
                } else {
                    yapiSaveParam.setMenu(YapiConstant.menu);
                }
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, project.getBasePath()
                    );
                    if (yapiResponse.getErrcode() != 0) {
                        NotifyUtil.log(NOTIFICATION_GROUP, project,
                                "sorry ,upload api error cause:" + yapiResponse.getErrmsg(), NotificationType.ERROR);
                    } else {
                        String url =
                                configEntity.getyApiUrl() + "/project/" + configEntity.getProjectId() + "/interface"
                                        + "/api/cat_" + UploadYapi.catMap
                                        .get(configEntity.getProjectId());
                        NotifyUtil
                                .log(NOTIFICATION_GROUP, project, "success ,url: " + url, NotificationType.INFORMATION);
                    }
                } catch (Exception e) {
                    NotifyUtil.log(NOTIFICATION_GROUP, project, "sorry ,upload api error cause:" + e,
                            NotificationType.ERROR);
                }
            }
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
