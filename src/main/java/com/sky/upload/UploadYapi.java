package com.sky.upload;

import static com.sky.interaction.UploadToYApi.NOTIFICATION_GROUP;
import static com.sky.interaction.UploadToYApi.project;
import static com.sky.util.JsonUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.intellij.notification.NotificationType;
import com.sky.constant.YapiConstant;
import com.sky.dto.YApiSaveParam;
import com.sky.dto.YApiSaveResponse;
import com.sky.dto.YapiCatMenuParam;
import com.sky.dto.YapiCatResponse;
import com.sky.dto.YapiHeaderDTO;
import com.sky.dto.YapiResponse;
import com.sky.util.HttpClientUtil;
import com.sky.util.NotifyUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 上传到yapi
 *
 * @author gangyf
 * @since 2019/1/31 11:41 AM
 */
public class UploadYapi {


    public static Map<Integer, Map<String, Integer>> catMap = new HashMap<>();

    /**
     * @description: 调用保存接口
     * @param: [yapiSaveParam, attachUpload, path]
     * @return: com.qbb.dto.YapiResponse
     * @author gangyf chengsheng@qbb6.com
     * @since: 2019/5/15
     */
    public YapiResponse<List<YApiSaveResponse>> uploadSave(YApiSaveParam yapiSaveParam, String cookies) throws IOException {
        if (Strings.isNullOrEmpty(yapiSaveParam.getTitle())) {
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        YapiHeaderDTO yapiHeaderDTO = new YapiHeaderDTO();
        if ("form".equals(yapiSaveParam.getReq_body_type())) {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/x-www-form-urlencoded");
            yapiSaveParam.setReq_body_form(yapiSaveParam.getReq_body_form());
        } else {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/json");
            yapiSaveParam.setReq_body_type("json");
        }
        if (Objects.isNull(yapiSaveParam.getReq_headers())) {
            List<Object> list = new ArrayList<>();
            list.add(yapiHeaderDTO);
            yapiSaveParam.setReq_headers(list);
        } else {
            yapiSaveParam.getReq_headers().add(yapiHeaderDTO);
        }

        YapiResponse<Integer> yapiCatId = getCatIdOrCreate(yapiSaveParam, cookies);

        if (yapiCatId.getErrcode() == 0 && yapiCatId.getData() != null) {
            yapiSaveParam.setCatid(yapiCatId.getData().toString());
            CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(cookies);
            if (!Strings.isNullOrEmpty(cookies)) {
                // 使用cookie时, token置空
                yapiSaveParam.setToken(null);
            }
            String response = HttpClientUtil.ObjectToString(httpclient.execute(
                    getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiSave,
                            OBJECT_MAPPER.writeValueAsString(yapiSaveParam))), "utf-8");
            return OBJECT_MAPPER.readValue(response, new TypeReference<YapiResponse<List<YApiSaveResponse>>>() {});
        }

        throw new RuntimeException("创建分类失败: " + yapiSaveParam.getMenu());
    }


    /**
     * 获得httpPost
     *
     * @return
     */
    private HttpPost getHttpPost(String url, String body) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            HttpEntity reqEntity = new StringEntity(body == null ? "" : body, "UTF-8");
            httpPost.setEntity(reqEntity);
        } catch (Exception e) {
        }
        return httpPost;
    }

    /**
     * @description: 上传文件
     * @param: [url, filePath]
     * @return: java.lang.String
     * @author gangyf chengsheng@qbb6.com
     * @since: 2019/5/15
     */
    public String uploadFile(String url, String filePath) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            FileBody bin = new FileBody(new File(filePath));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();
            httpPost.setEntity(reqEntity);
            return HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(httpPost), "utf-8");
        } catch (Exception e) {
        }
        return "";
    }


    private HttpGet getHttpGet(String url) {
        try {
            return HttpClientUtil.getHttpGet(url, "application/json", "application/json; charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @description: 获得分类或者创建分类或者
     * @param: [yapiSaveParam]
     * @return: com.qbb.dto.YapiResponse
     * @author gangyf chengsheng@qbb6.com
     * @since: 2019/5/15
     */
    public YapiResponse<Integer> getCatIdOrCreate(YApiSaveParam yapiSaveParam, String cookies) throws IOException {

        YapiResponse<Integer> yapiResponse = findFromExist(yapiSaveParam, cookies);

        if (yapiResponse != null) {
            return yapiResponse;
        }

        return createCatMenu(yapiSaveParam, cookies);
    }

    @Nullable
    private YapiResponse<Integer> findFromExist(YApiSaveParam yapiSaveParam, String cookies) throws IOException {

        String response;
        CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(cookies);

        String catListUrl = String.format("%s%s?project_id=%s&token=%s", yapiSaveParam.getYapiUrl(),
                YapiConstant.yapiCatMenu, yapiSaveParam.getProjectId(), yapiSaveParam.getToken());

        response = HttpClientUtil
                .ObjectToString(httpclient.execute(getHttpGet(catListUrl)), "utf-8");

        YapiResponse<List<YapiCatResponse>> yapiResponse = OBJECT_MAPPER.readValue(response,
                new TypeReference<YapiResponse<List<YapiCatResponse>>>() {});

        boolean success = yapiResponse.getErrcode() == 0;

        if (success) {

            List<YapiCatResponse> existCatList = yapiResponse.getData();

            Optional<YapiCatResponse> optional = existCatList.stream()
                    .filter(catResponse -> yapiSaveParam.getMenu().equals(catResponse.getName()))
                    .findFirst();

            optional.ifPresent(catResponse -> cacheCatResponse(yapiSaveParam, catResponse));

            if (optional.isPresent()) {
                return new YapiResponse<>(optional.get().getId());
            }
        } else {
            NotifyUtil.log(NOTIFICATION_GROUP, project,
                    "yapi api error: " + yapiResponse.getErrcode() + ", " + yapiResponse.getErrcode(),
                    NotificationType.ERROR);
            throw new RuntimeException(
                    "yapi api error: " + yapiResponse.getErrcode() + ", " + yapiResponse.getErrcode());
        }
        return null;
    }

    /**
     * 创建一个信息的cat
     *
     * @param yapiSaveParam yapi相关配置
     * @param cookies
     * @return
     * @throws IOException
     */
    @NotNull
    private YapiResponse<Integer> createCatMenu(YApiSaveParam yapiSaveParam, String cookies) throws IOException {
        CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(cookies);
        YapiCatMenuParam createCatMenuParam = new YapiCatMenuParam(yapiSaveParam.getMenu(),
                yapiSaveParam.getProjectId(), yapiSaveParam.getToken());

        String createCatUrl = yapiSaveParam.getYapiUrl() + YapiConstant.yapiAddCat;

        String responseCat = HttpClientUtil.ObjectToString(
                httpclient.execute(
                        getHttpPost(createCatUrl, OBJECT_MAPPER.writeValueAsString(createCatMenuParam))),
                "utf-8");

        YapiResponse<YapiCatResponse> yapiResponse = OBJECT_MAPPER.readValue(responseCat,
                new TypeReference<YapiResponse<YapiCatResponse>>() {});

        cacheCatResponse(yapiSaveParam, yapiResponse.getData());
        return new YapiResponse<>(yapiResponse.getData().getId());
    }

    @NotNull
    private void cacheCatResponse(YApiSaveParam yapiSaveParam, YapiCatResponse catResponse) {

        catMap.computeIfAbsent(yapiSaveParam.getProjectId(),
                (Function<Integer, Map<String, Integer>>) integer -> new HashMap<>(16))
                .put(catResponse.getName(), catResponse.getId());

    }

}
