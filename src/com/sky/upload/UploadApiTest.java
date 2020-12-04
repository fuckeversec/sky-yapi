package com.sky.upload;

import com.sky.config.Config;
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
                + "      \"cookies\": \"# Netscape HTTP Cookie File\\n# http://curl.haxx.se/rfc/cookie_spec.html\\n# This is a generated file!  Do not edit.\\n\\n.www.kaikeba.com\\tTRUE\\t/experience\\tFALSE\\t1921562838\\t53gid2\\t10614838998013\\n.www.kaikeba.com\\tTRUE\\t/vipcourse\\tFALSE\\t1920360539\\t53gid2\\t10334270908010\\nwww.kaikeba.com\\tFALSE\\t/vipcourse\\tFALSE\\t1607592543\\tinvite_53kf_totalnum_1\\t1\\ntestmps.kaikeba.com\\tFALSE\\t/passport\\tFALSE\\t1921806094\\t_uab_collina\\t160644609438426329774043\\ntest-play.kaikeba.com\\tFALSE\\t/public\\tFALSE\\t253402300799\\tclient_id\\t84982314\\n.www.kaikeba.com\\tTRUE\\t/midui/\\tFALSE\\t1637919647\\tHm_lvt_3aca5b33cb6cf1c66b79fa7673bafb0a\\t1606383647\\n.www.kaikeba.com\\tTRUE\\t/midui\\tFALSE\\t1921743647\\t53gid2\\t10633735760013\\nwww.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1632359536\\toneapmclientid\\t174b884aabf6ef-0133ba9798abf8-316b7004-13c680-174b884aac0cca\\nwww.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1637923424\\tADHOC_MEMBERSHIP_CLIENT_ID1.0\\tdddd177a-08da-4c9f-099f-4a03769e8a13\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1921806094\\tgr_user_id\\tbda10e66-75b2-41bd-b216-a13e1af902f1\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1916183537\\tgrwng_uid\\tfa0003c2-8f70-4322-8258-71c840f536da\\nwww.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1616375537\\t_bl_uid\\tIjkCdfz8eIvpI80y42e2jzj3vpqv\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1916183537\\tkd_user_id\\ta6c93472-3c40-4580-9c21-3e1f55bc9495\\n.www.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1916183539\\t53revisit\\t1600823539206\\nwww.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1916183539\\t_uab_collina\\t160082353996840542804937\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1616918560\\tUM_distinctid\\t174ce95d631153-0dee1fa9c473d1-31697304-13c680-174ce95d6326b0\\nwiki.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1632797000\\tseraph.confluence\\t47120531%3Aa599aeb98513e5d2045d6b2d9fd0726d4f369a80\\nicode.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1922322929\\tsidebar_collapsed\\tfalse\\njenkins.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1665648338\\tjenkins-timestamper-offset\\t-28800000\\nicode.kaikeba.com\\tFALSE\\t/\\tFALSE\\t2236057821\\tdiff_view\\tparallel\\nwwwtest.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1636602809\\tADHOC_MEMBERSHIP_CLIENT_ID1.0\\t32330f3e-35cf-fda4-1a6a-86cc91e18624\\nwwwtest.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1620617718\\t_bl_uid\\t1akywhk6cvCukLoyqqCyijaabk1y\\n.wwwtest.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1920425720\\t53revisit\\t1605065720728\\n.wwwtest.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1920426812\\t53gid2\\t10335603439010\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1668240986\\t_ga\\tGA1.2.370407192.1605168675\\n.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1637923424\\tHm_lvt_156e88c022bf41570bf96e74d090ced7\\t1603888732,1604998243,1606130488\\n.www.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1921743643\\t53gid2\\t10633735408013\\nshop.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1637926896\\tECS[visit_times]\\t1\\n.shop.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1921750897\\t53revisit\\t1606390897066\\nshop.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1606995745\\tECS[display]\\tgrid\\n.shop.kaikeba.com\\tTRUE\\t/\\tFALSE\\t1921750946\\t53gid2\\t10634635495013\\nyapi.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1607506953\\t_yapi_token\\teyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjYwOCwiaWF0IjoxNjA2OTAyMTUzLCJleHAiOjE2MDc1MDY5NTN9.wmMrkNbabLKBJDtA4SFy55lXY9yAAUjco8enPAgc4b0\\nyapi.kaikeba.com\\tFALSE\\t/\\tFALSE\\t1607506953\\t_yapi_uid\\t608\\nwiki.kaikeba.com\\tFALSE\\t/\\tFALSE\\t0\\tJSESSIONID\\tD28A2C007828864399B7A519B4005180\",\n"
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
        site.setCookies(config.getMultipleConfig().get("lbgc-base-info").getCookies());
        CloseableHttpClient client = httpClientGenerator.getClient(site);

        String response = HttpClientUtil
                .ObjectToString(client.execute(getHttpGet("http://yapi.kaikeba"
                                + ".com/api/interface/getCatMenu?project_id=1894&token=99c70525c3c24957d059", "application/json", "application/json; charset=utf-8")),
                        "utf-8");

        System.out.println(response);

    }

}
