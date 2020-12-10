package com.sky.upload;

import com.sky.config.Config;
import com.sky.constant.YapiConstant;
import com.sky.util.HttpClientUtil;
import com.sky.util.JsonUtil;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author gangyf
 * @since 2020/11/30 3:49 PM
 */
public class UploadApiTest {

    private static HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    /**
     * 获取HttpGet
     *
     * @param url
     * @param accept
     * @param contentType
     * @return HttpGet
     * @throws IOException
     */
    public static HttpGet getHttpGet(String url, String accept, String contentType) throws IOException {
        HttpGet httpGet = null;
        httpGet = new HttpGet(url);
        if (accept != null) {
            httpGet.setHeader("Accept", accept);
        }

        if (contentType != null) {
            httpGet.setHeader("Content-Type", contentType);
        }
        return httpGet;
    }

    /**
     * 获得httpPost
     *
     * @return
     */
    private static HttpPost getHttpPost(String url, String body) {
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

    public static void main(String[] args) throws IOException {

        Config config = JsonUtil.OBJECT_MAPPER.readValue("{\n"
                + "  \"isSingle\": false,\n"
                + "  \"multipleConfig\": {\n"
                + "    \"lbgc-base-info\": {\n"
                + "      \"projectToken\": \"99c70525c3c24957d059\",\n"
                + "      \"projectId\": 1894,\n"
                + "      \"yApiUrl\": \"http://yapi.kaikeba.com/\",\n"
                + "      \"menu\": \"B端-师资时间管理\",\n"
                + "      \"projectType\": \"api\",\n"
                + "      \"reqBodyIsJsonSchema\": false,\n"
                + "      \"resBodyIsJsonSchema\": false\n"
                + "    }\n"
                + "  }\n"
                + "}", Config.class);

        Site site = new Site();
        site.setCookies("[\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1637923424,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"Hm_lvt_156e88c022bf41570bf96e74d090ced7\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"1603888732,1604998243,1606130488\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1616918560,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"UM_distinctid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"174ce95d631153-0dee1fa9c473d1-31697304-13c680-174ce95d6326b0\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1916183537,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"grwng_uid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"fa0003c2-8f70-4322-8258-71c840f536da\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1921806094,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"gr_user_id\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"bda10e66-75b2-41bd-b216-a13e1af902f1\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \"yapi.kaikeba.com\",\n"
                + "        \"expirationDate\": 1607506953.13208,\n"
                + "        \"hostOnly\": true,\n"
                + "        \"httpOnly\": true,\n"
                + "        \"name\": \"_yapi_token\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjYwOCwiaWF0IjoxNjA2OTAyMTUzLCJleHAiOjE2MDc1MDY5NTN9.wmMrkNbabLKBJDtA4SFy55lXY9yAAUjco8enPAgc4b0\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1668240986,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"_ga\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"GA1.2.370407192.1605168675\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \"yapi.kaikeba.com\",\n"
                + "        \"expirationDate\": 1607506953.132145,\n"
                + "        \"hostOnly\": true,\n"
                + "        \"httpOnly\": true,\n"
                + "        \"name\": \"_yapi_uid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"608\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1916183537,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"kd_user_id\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"a6c93472-3c40-4580-9c21-3e1f55bc9495\"\n"
                + "    }\n"
                + "]");
        CloseableHttpClient client = httpClientGenerator.getClient(site);

        String response = HttpClientUtil
                .ObjectToString(client.execute(getHttpPost("http://yapi.kaikeba.com/" + YapiConstant.yapiSave,
                        "{\"method\":\"GET\",\"catid\":\"1470\",\"title\":\"lkdfaslkdfjl\",\"path\":\"/lkjopipoij\",\"project_id\":\"1798\"}")),
                        "utf-8");

        System.out.println(response);

    }

}
