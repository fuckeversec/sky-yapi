package com.sky.build;

import static com.sky.interaction.UploadToYApi.NOTIFICATION_GROUP;

import com.google.common.base.Strings;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.sky.config.ConfigEntity;
import com.sky.dto.YApiSaveParam;
import com.sky.dto.YApiSaveResponse;
import com.sky.dto.YapiApiDTO;
import com.sky.dto.YapiResponse;
import com.sky.upload.UploadYapi;
import com.sky.util.NotifyUtil;
import java.util.List;
import java.util.Objects;
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

        List<YapiApiDTO> yApiApiDTOS = selectedMethods.stream()
                .map(method -> new BuildJsonForYApi().actionPerformed(null, method, project, null)).collect(
                        Collectors.toList());

        if (Objects.nonNull(yApiApiDTOS)) {
            for (YapiApiDTO yapiApiDTO : yApiApiDTOS) {
                YApiSaveParam yapiSaveParam = new YApiSaveParam(configEntity.getProjectToken(), yapiApiDTO.getTitle(),
                        yapiApiDTO.getPath(),
                        yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(),
                        configEntity.getProjectId(), configEntity.getyApiUrl(), true,
                        yapiApiDTO.getMethod(),
                        yapiApiDTO.getDesc(),
                        yapiApiDTO.getHeader());
                yapiSaveParam.setReq_body_form(yapiApiDTO.getReqBodyForm());
                yapiSaveParam.setReq_body_type(yapiApiDTO.getReqBodyType());
                yapiSaveParam.setReq_params(yapiApiDTO.getReqParams());
                yapiSaveParam.setReq_body_is_json_schema(configEntity.isReqBodyIsJsonSchema());
                yapiSaveParam.setRes_body_is_json_schema(configEntity.isResBodyIsJsonSchema());
                if (!Strings.isNullOrEmpty(yapiApiDTO.getMenu())) {
                    yapiSaveParam.setMenu(yapiApiDTO.getMenu());
                } else {
                    yapiSaveParam.setMenu(configEntity.getMenu());
                }
                try {
                    // 上传
                    YapiResponse<List<YApiSaveResponse>> yapiResponse = new UploadYapi()
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

    @Override
    public String apiType() {
        return "api";
    }
}
