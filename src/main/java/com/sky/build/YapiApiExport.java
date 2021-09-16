package com.sky.build;

import static com.sky.interaction.UploadToYapi.NOTIFICATION_GROUP;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.sky.build.domain.SpringApiParserImpl;
import com.sky.config.ConfigEntity;
import com.sky.dto.YApiSaveParam;
import com.sky.dto.YApiSaveResponse;
import com.sky.dto.YapiApiDTO;
import com.sky.dto.YapiResponse;
import com.sky.upload.UploadYapi;
import com.sky.util.NotifyUtil;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gangyf
 * @since 2020/12/11 9:02 PM
 */
public class YapiApiExport extends AbstractApiExport {

    @Override
    public void yapiUpload(AnActionEvent anActionEvent, Project project, ConfigEntity configEntity) {
        //获得api 需上传的接口列表 参数对象

        List<PsiMethod> selectedMethods = actionPerform(anActionEvent);

        JsonApiParser jsonApiParser = new SpringApiParserImpl();

        List<YapiApiDTO> yapiApiDTOList = selectedMethods.stream()
                .map(selectedMethod -> jsonApiParser.parseRequestMethod(project, selectedMethod))
                .collect(Collectors.toList());

        for (YapiApiDTO yapiApiDTO : yapiApiDTOList) {

            YApiSaveParam yapiSaveParam = YApiSaveParam.builder()
                    .title(yapiApiDTO.getTitle())
                    .path(yapiApiDTO.getPath())
                    // configure
                    .status(configEntity.getStatus())
                    .yapiUrl(configEntity.getYApiUrl())
                    .token(configEntity.getProjectToken())
                    .projectId(configEntity.getProjectId())
                    .reqBodyIsJsonSchema(configEntity.isReqBodyIsJsonSchema())
                    .resBodyIsJsonSchema(configEntity.isResBodyIsJsonSchema())
                    // request
                    .reqQuery(yapiApiDTO.getParams())
                    .reqBodyOther(yapiApiDTO.getRequestBody())
                    .reqHeaders(yapiApiDTO.getHeader())
                    .reqBodyForm(yapiApiDTO.getReqBodyForm())
                    .reqBodyType(yapiApiDTO.getReqBodyType())
                    .reqParams(yapiApiDTO.getReqParams())
                    // response
                    .resBodyType("json")
                    .resBody(yapiApiDTO.getResponse())
                    .method(yapiApiDTO.getMethod())
                    .desc(yapiApiDTO.getDesc())
                    // default
                    .message("yapi生成")
                    .build();

            if (!Strings.isNullOrEmpty(yapiApiDTO.getMenu())) {
                yapiSaveParam.setMenu(yapiApiDTO.getMenu());
            } else {
                yapiSaveParam.setMenu(configEntity.getMenu());
            }
            try {
                // 上传
                YapiResponse<YApiSaveResponse> yapiResponse = new UploadYapi().uploadSave(yapiSaveParam, configEntity);

                if (yapiResponse.getErrCode() != 0) {
                    NotifyUtil.log(NOTIFICATION_GROUP, project,
                            "sorry ,upload api error cause:" + yapiResponse.getErrMsg(), NotificationType.ERROR);
                } else {
                    notifyCatUrl(project, configEntity, yapiResponse.getData());
                }

            } catch (Exception e) {
                NotifyUtil
                        .log(NOTIFICATION_GROUP, project, "sorry ,upload api error cause:" + e, NotificationType.ERROR);
                e.printStackTrace();
            }
        }

    }

    /**
     * Notify cat url.
     *
     * @param project the project
     * @param configEntity the config entity
     * @param yApiSaveResponse the yapi save param
     */
    private void notifyCatUrl(Project project, ConfigEntity configEntity, YApiSaveResponse yApiSaveResponse) {

        if (yApiSaveResponse == null) {
            return;
        }

        String url = configEntity.getYApiUrl().replaceAll("/$", "") + "/project/" + configEntity.getProjectId()
                + "/interface/api/" + yApiSaveResponse.getId();

        Notification notification = NOTIFICATION_GROUP.createNotification(
                "Congratulation \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
                "<html><a href=\"" + url + "\" target=\"blank\">" + url + "</a></html>",
                NotificationType.INFORMATION, new NotificationListener.UrlOpeningListener(true));

        Notifications.Bus.notify(notification, project);
    }

    @Override
    public String apiType() {
        return "api";
    }
}
