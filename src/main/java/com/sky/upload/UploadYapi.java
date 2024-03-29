package com.sky.upload;

import static com.sky.interaction.UploadToYapi.NOTIFICATION_GROUP;
import static com.sky.interaction.UploadToYapi.project;
import static com.sky.util.JsonUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.intellij.notification.NotificationType;
import com.sky.config.ConfigEntity;
import com.sky.constant.YapiConstant;
import com.sky.dto.ValueWrapper;
import com.sky.dto.YApiSaveParam;
import com.sky.dto.YApiSaveResponse;
import com.sky.dto.YapiCatMenuParam;
import com.sky.dto.YapiCatResponse;
import com.sky.dto.YapiResponse;
import com.sky.util.HttpClientUtil;
import com.sky.util.NotifyUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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


    // projectId -> {method: Set<urlPath>}
    public static Map<Integer, Map<String, Set<String>>> uploadedUrlPath = new ConcurrentHashMap<>();
    public static Map<Integer, Map<String, Integer>> catMap = new ConcurrentHashMap<>();

    /**
     * Upload save yapi response.
     *
     * @param yapiSaveParam the yapi save param
     * @param configEntity the configEntity
     * @return the yapi response
     * @throws IOException the io exception
     * @description: 调用保存接口
     * @param: [yapiSaveParam, attachUpload, path]
     * @return: com.qbb.dto.YapiResponse
     * @author gangyf
     * @since: 2019 /5/15
     */
    public YapiResponse<YApiSaveResponse> uploadSave(YApiSaveParam yapiSaveParam, ConfigEntity configEntity)
            throws IOException {

        if (Strings.isNullOrEmpty(yapiSaveParam.getTitle())) {
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        ValueWrapper yapiHeaderDTO = new ValueWrapper();
        if ("form".equals(yapiSaveParam.getReqBodyType())) {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/x-www-form-urlencoded");
            yapiSaveParam.setReqBodyForm(yapiSaveParam.getReqBodyForm());
        } else {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/json");
            yapiSaveParam.setReqBodyType("json");
        }

        if (Objects.isNull(yapiSaveParam.getReqHeaders())) {
            List<ValueWrapper> list = new ArrayList<>();
            list.add(yapiHeaderDTO);
            yapiSaveParam.setReqHeaders(list);
        } else {
            yapiSaveParam.getReqHeaders().add(yapiHeaderDTO);
        }

        YapiResponse<Integer> yapiCatId = getCatIdOrCreate(yapiSaveParam, configEntity.getCookies());

        if (yapiCatId.getErrCode() == 0 && yapiCatId.getData() != null) {
            yapiSaveParam.setCatId(yapiCatId.getData().toString());
            CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(configEntity.getCookies());

            YapiResponse<YApiSaveResponse> yapiResponse;
            if (uploadedUrlPath.computeIfAbsent(yapiSaveParam.getProjectId(), key -> new HashMap<>())
                    .computeIfAbsent(yapiSaveParam.getMethod(), key -> new HashSet<>())
                    .contains(yapiSaveParam.getPath())) {

                yapiResponse = saveNewYapi(httpclient, yapiSaveParam);
            } else {

                yapiResponse = addNewYapi(httpclient, yapiSaveParam);
                uploadedUrlPath.get(yapiSaveParam.getProjectId())
                        .get(yapiSaveParam.getMethod())
                        .add(yapiSaveParam.getPath());
            }

            if (yapiResponse.getErrCode() != 40022) {
                return yapiResponse;
            }

            return saveNewYapi(httpclient, yapiSaveParam);

        }

        throw new RuntimeException("创建分类失败: " + yapiSaveParam.getMenu());
    }

    /**
     * 新增或更新
     *
     * @param httpclient the httpclient
     * @param yapiSaveParam the yapi save param
     * @return the yapi response
     * @throws IOException the io exception
     */
    private YapiResponse<YApiSaveResponse> saveNewYapi(CloseableHttpClient httpclient, YApiSaveParam yapiSaveParam)
            throws IOException {

        CloseableHttpResponse httpResponse = httpclient.execute(
                getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiSave,
                        OBJECT_MAPPER.writeValueAsString(yapiSaveParam)));

        String response = HttpClientUtil.objectToString(httpResponse, StandardCharsets.UTF_8.toString());

        YapiResponse<List<YApiSaveResponse>> listYapiResponse = OBJECT_MAPPER.readValue(response,
                new TypeReference<YapiResponse<List<YApiSaveResponse>>>() {});

        YapiResponse<YApiSaveResponse> yapiResponse = new YapiResponse<>();
        yapiResponse.setErrCode(listYapiResponse.getErrCode());
        yapiResponse.setErrMsg(listYapiResponse.getErrMsg());
        if (CollectionUtils.isNotEmpty(listYapiResponse.getData())) {
            yapiResponse.setData(listYapiResponse.getData().get(0));
        }
        return yapiResponse;
    }

    /**
     * 新增接口
     *
     * @param httpclient the httpclient
     * @param yapiSaveParam the yapi save param
     * @return the yapi response
     * @throws IOException the io exception
     */
    private YapiResponse<YApiSaveResponse> addNewYapi(CloseableHttpClient httpclient, YApiSaveParam yapiSaveParam)
            throws IOException {

        CloseableHttpResponse httpResponse = httpclient.execute(
                getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiAdd,
                        OBJECT_MAPPER.writeValueAsString(yapiSaveParam)));

        String response = HttpClientUtil.objectToString(httpResponse, StandardCharsets.UTF_8.toString());

        return OBJECT_MAPPER.readValue(response, new TypeReference<YapiResponse<YApiSaveResponse>>() {});
    }


    /**
     * 获得httpPost
     *
     * @param url the url
     * @param body the body
     * @return http post
     */
    private HttpPost getHttpPost(String url, String body) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            HttpEntity reqEntity = new StringEntity(body == null ? "" : body, "UTF-8");
            httpPost.setEntity(reqEntity);
        } catch (Exception ignored) {
        }
        return httpPost;
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
     * 获得分类或者创建分类或者
     *
     * @param yapiSaveParam the yapi save param
     * @param cookies the cookies
     * @return the cat id or create
     * @throws IOException the io exception
     */
    public YapiResponse<Integer> getCatIdOrCreate(YApiSaveParam yapiSaveParam, List<Cookie> cookies)
            throws IOException {

        YapiResponse<Integer> yapiResponse = findFromExist(yapiSaveParam, cookies);

        if (yapiResponse != null) {
            return yapiResponse;
        }

        return createCatMenu(yapiSaveParam, cookies);
    }

    @Nullable
    private YapiResponse<Integer> findFromExist(YApiSaveParam yapiSaveParam, List<Cookie> cookies) throws IOException {

        String response;
        CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(cookies);

        String catListUrl = String.format("%s%s?project_id=%s&token=%s", yapiSaveParam.getYapiUrl(),
                YapiConstant.yapiCatMenu, yapiSaveParam.getProjectId(), yapiSaveParam.getToken());

        response = HttpClientUtil
                .objectToString(httpclient.execute(getHttpGet(catListUrl)), "utf-8");

        YapiResponse<List<YapiCatResponse>> yapiResponse = OBJECT_MAPPER.readValue(response,
                new TypeReference<YapiResponse<List<YapiCatResponse>>>() {});

        boolean success = yapiResponse.getErrCode() == 0;

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
                    "yapi api error: " + yapiResponse.getErrCode() + ", " + yapiResponse.getErrCode(),
                    NotificationType.ERROR);
            throw new RuntimeException(
                    "yapi api error: " + yapiResponse.getErrCode() + ", " + yapiResponse.getErrCode());
        }
        return null;
    }

    /**
     * 创建一个信息的cat
     *
     * @param yapiSaveParam yapi相关配置
     * @param cookies the cookies
     * @return yapi response
     * @throws IOException the io exception
     */
    @NotNull
    private YapiResponse<Integer> createCatMenu(YApiSaveParam yapiSaveParam, List<Cookie> cookies) throws IOException {
        CloseableHttpClient httpclient = HttpClientUtil.getHttpclient(cookies);
        YapiCatMenuParam createCatMenuParam = new YapiCatMenuParam(yapiSaveParam.getMenu(),
                yapiSaveParam.getProjectId(), yapiSaveParam.getToken());

        String createCatUrl = yapiSaveParam.getYapiUrl() + YapiConstant.yapiAddCat;

        String responseCat = HttpClientUtil.objectToString(
                httpclient.execute(
                        getHttpPost(createCatUrl, OBJECT_MAPPER.writeValueAsString(createCatMenuParam))),
                "utf-8");

        YapiResponse<YapiCatResponse> yapiResponse = OBJECT_MAPPER.readValue(responseCat,
                new TypeReference<YapiResponse<YapiCatResponse>>() {});

        cacheCatResponse(yapiSaveParam, yapiResponse.getData());
        return new YapiResponse<>(yapiResponse.getData().getId());
    }

    /**
     * Cache cat response.
     *
     * @param yapiSaveParam the yapi save param
     * @param catResponse the cat response
     */
    private void cacheCatResponse(YApiSaveParam yapiSaveParam, YapiCatResponse catResponse) {

        catMap.computeIfAbsent(yapiSaveParam.getProjectId(),
                        (Function<Integer, Map<String, Integer>>) integer -> new HashMap<>(16))
                .put(catResponse.getName(), catResponse.getId());

    }

}
