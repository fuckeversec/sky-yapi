package com.sky.upload;

import com.sky.constant.YapiConstant;
import com.sky.util.HttpClientUtil;
import java.io.IOException;
import org.apache.http.HttpEntity;
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

        Site site = new Site();
        site.setCookies("# Netscape HTTP Cookie File\n"
                + "# http://curl.haxx.se/rfc/cookie_spec.html\n"
                + "# This is a generated file!  Do not edit.\n"
                + "\n"
                + ".www.kaikeba.com\tTRUE\t/experience\tFALSE\t1921562838\t53gid2\t10614838998013\n"
                + ".www.kaikeba.com\tTRUE\t/experience\tFALSE\t0\tvisitor_type\tnew\n"
                + ".www.kaikeba.com\tTRUE\t/experience\tFALSE\t0\t53uvid\t1\n"
                + "www.kaikeba.com\tFALSE\t/experience\tFALSE\t0\tonliner_zdfq72216850\t0\n"
                + ".www.kaikeba.com\tTRUE\t/vipcourse\tFALSE\t1920360539\t53gid2\t10334270908010\n"
                + "www.kaikeba.com\tFALSE\t/vipcourse\tFALSE\t1607592543\tinvite_53kf_totalnum_1\t1\n"
                + "testmps.kaikeba.com\tFALSE\t/passport\tFALSE\t1921806094\t_uab_collina\t160644609438426329774043\n"
                + "test-play.kaikeba.com\tFALSE\t/public\tFALSE\t253402300799\tclient_id\t84982314\n"
                + ".www.kaikeba.com\tTRUE\t/midui/\tFALSE\t1637919647\tHm_lvt_3aca5b33cb6cf1c66b79fa7673bafb0a\t1606383647\n"
                + ".www.kaikeba.com\tTRUE\t/midui/\tFALSE\t0\tHm_lpvt_3aca5b33cb6cf1c66b79fa7673bafb0a\t1606383647\n"
                + ".www.kaikeba.com\tTRUE\t/midui\tFALSE\t1921743647\t53gid2\t10633735760013\n"
                + ".www.kaikeba.com\tTRUE\t/midui\tFALSE\t0\tvisitor_type\tnew\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t1632359536\toneapmclientid\t174b884aabf6ef-0133ba9798abf8-316b7004-13c680-174b884aac0cca\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t1637923424\tADHOC_MEMBERSHIP_CLIENT_ID1.0\tdddd177a-08da-4c9f-099f-4a03769e8a13\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1921806094\tgr_user_id\tbda10e66-75b2-41bd-b216-a13e1af902f1\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1916183537\tgrwng_uid\tfa0003c2-8f70-4322-8258-71c840f536da\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t1616375537\t_bl_uid\tIjkCdfz8eIvpI80y42e2jzj3vpqv\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1916183537\tkd_user_id\ta6c93472-3c40-4580-9c21-3e1f55bc9495\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t1916183539\t53revisit\t1600823539206\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t1916183539\t_uab_collina\t160082353996840542804937\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1616918560\tUM_distinctid\t174ce95d631153-0dee1fa9c473d1-31697304-13c680-174ce95d6326b0\n"
                + "wiki.kaikeba.com\tFALSE\t/\tFALSE\t1632797000\tseraph.confluence\t47120531%3Aa599aeb98513e5d2045d6b2d9fd0726d4f369a80\n"
                + "icode.kaikeba.com\tFALSE\t/\tFALSE\t1921804623\tsidebar_collapsed\tfalse\n"
                + "jenkins.kaikeba.com\tFALSE\t/\tFALSE\t1665648338\tjenkins-timestamper-offset\t-28800000\n"
                + "icode.kaikeba.com\tFALSE\t/\tFALSE\t2236057821\tdiff_view\tparallel\n"
                + "wwwtest.kaikeba.com\tFALSE\t/\tFALSE\t1636602809\tADHOC_MEMBERSHIP_CLIENT_ID1.0\t32330f3e-35cf-fda4-1a6a-86cc91e18624\n"
                + "wwwtest.kaikeba.com\tFALSE\t/\tFALSE\t1620617718\t_bl_uid\t1akywhk6cvCukLoyqqCyijaabk1y\n"
                + ".wwwtest.kaikeba.com\tTRUE\t/\tFALSE\t1920425720\t53revisit\t1605065720728\n"
                + ".wwwtest.kaikeba.com\tTRUE\t/\tFALSE\t1920426812\t53gid2\t10335603439010\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1668240986\t_ga\tGA1.2.370407192.1605168675\n"
                + "icode.kaikeba.com\tFALSE\t/\tFALSE\t0\tevent_filter\tall\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t1637923424\tHm_lvt_156e88c022bf41570bf96e74d090ced7\t1603888732,1604998243,1606130488\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_from_host\twww.kaikeba.com\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_land_page\thttps%253A%252F%252Fwww.kaikeba.com%252Fexperience%252Fdetail%253Fid%253D51\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\tkf_72216850_land_page_ok\t1\n"
                + "yapi.kaikeba.com\tFALSE\t/\tFALSE\t1606895682\t_yapi_token\teyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjYwOCwiaWF0IjoxNjA2MjkwODgyLCJleHAiOjE2MDY4OTU2ODJ9.AqCmoQv6U7P1zBS1JmHTOhqWHLpNfAqEKZpxIkLDWkI\n"
                + "yapi.kaikeba.com\tFALSE\t/\tFALSE\t1606895682\t_yapi_uid\t608\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t0\tkd_5d6526d7-3c9f-460b-b6cf-ba75397ce1ac_log_id\tDxBZwaENCP1GwIOi5p2%3A224f9855-b3ea-4e91-8cb5-701fba51c5a4%3Afdecbfef-4320-4fa9-a9ad-ec951d631e61\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t1921743643\t53gid2\t10633735408013\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\tvisitor_type\tnew\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\t53uvid\t1\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t0\tonliner_zdfq72216850\t0\n"
                + ".www.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_keyword\thttps%3A%2F%2Fwww.kaikeba.com%2F\n"
                + "www.kaikeba.com\tFALSE\t/\tFALSE\t0\tinvite_53kf_totalnum_1\t3\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t0\tkd_5d6526d7-3c9f-460b-b6cf-ba75397ce1ac_view_log_id\t8ciRquIZP0v1yydvE4m\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t0\tkd_5d6526d7-3c9f-460b-b6cf-ba75397ce1ac_kuickDeal_pageIndex\t0\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t0\tkd_5d6526d7-3c9f-460b-b6cf-ba75397ce1ac_kuickDeal_leaveTime\t1606387424433\n"
                + ".kaikeba.com\tTRUE\t/\tFALSE\t0\tHm_lpvt_156e88c022bf41570bf96e74d090ced7\t1606387425\n"
                + "shop.kaikeba.com\tFALSE\t/\tFALSE\t0\tPHPSESSID\tjt2hcr33sssotqi2od8h6mmpku\n"
                + "shop.kaikeba.com\tFALSE\t/\tFALSE\t0\tECS_ID\t1ce995c0967612f11c85aafbeb7d0885b7104b44\n"
                + "shop.kaikeba.com\tFALSE\t/\tFALSE\t1637926896\tECS[visit_times]\t1\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t1921750897\t53revisit\t1606390897066\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_from_host\tshop.kaikeba.com\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_land_page\thttps%253A%252F%252Fshop.kaikeba.com%252F\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\tkf_72216850_land_page_ok\t1\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\t53uvid\t1\n"
                + "shop.kaikeba.com\tFALSE\t/\tFALSE\t0\tonliner_zdfq72216850\t0\n"
                + "shop.kaikeba.com\tFALSE\t/\tFALSE\t1606995745\tECS[display]\tgrid\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\t53kf_72216850_keyword\thttps%3A%2F%2Fshop.kaikeba.com%2F\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t0\tvisitor_type\tnew\n"
                + ".shop.kaikeba.com\tTRUE\t/\tFALSE\t1921750946\t53gid2\t10634635495013\n"
                + "icode.kaikeba.com\tFALSE\t/\tFALSE\t0\t_gitlab_session\t42880cd0bd58f9888b0c363570f3d974\n"
                + "wiki.kaikeba.com\tFALSE\t/\tFALSE\t0\tJSESSIONID\t8DE9FE48F8CDDF2D9688096CE0EFD972");
        CloseableHttpClient client = httpClientGenerator.getClient(site);

        String response = HttpClientUtil
                .ObjectToString(client.execute(getHttpPost("http://yapi.kaikeba.com/" + YapiConstant.yapiSave,
                        "{\"req_query\":[],\"req_headers\":[{\"name\":\"Content-Type\","
                                + "\"value\":\"application/json\"}],\"title\":\"师资管理-修改状态接口\",\"catid\":\"1340\","
                                + "\"req_body_type\":\"json\",\"req_body_other\":\"{\\n  \\\"type\\\": \\\"object\\\",\\n  \\\"required\\\": [],\\n  \\\"title\\\": \\\"UpdateStateReq\\\",\\n  \\\"description\\\": \\\"UpdateStateReq :UpdateStateReq\\\",\\n  \\\"properties\\\": {\\n    \\\"enable\\\": {\\n      \\\"type\\\": \\\"enum\\\",\\n      \\\"description\\\": \\\"状态，0禁用下架，1启用上架；\\\"\\n    },\\n    \\\"enableSell\\\": {\\n      \\\"type\\\": \\\"enum\\\",\\n      \\\"description\\\": \\\"是否平台售卖\\\"\\n    }\\n  }\\n}\",\"req_body_is_json_schema\":false,\"path\":\"/console/professors/{id}/state/1\",\"status\":\"done\",\"res_body_type\":\"json\",\"res_body\":\"{\\n  \\\"type\\\": \\\"object\\\",\\n  \\\"title\\\": \\\"RadishResponse\\\\u003cBoolean\\\\u003e\\\",\\n  \\\"required\\\": [],\\n  \\\"description\\\": \\\"RadishResponse\\\\u003cBoolean\\\\u003e :RadishResponse\\\",\\n  \\\"properties\\\": {\\n    \\\"code\\\": {\\n      \\\"type\\\": \\\"integer\\\"\\n    },\\n    \\\"msg\\\": {\\n      \\\"type\\\": \\\"string\\\"\\n    },\\n    \\\"data\\\": {\\n      \\\"type\\\": \\\"object\\\",\\n      \\\"description\\\": \\\"response对象 ,Boolean\\\",\\n      \\\"properties\\\": {},\\n      \\\"required\\\": []\\n    }\\n  }\\n}\",\"res_body_is_json_schema\":false,\"edit_uid\":11,\"switch_notice\":false,\"message\":\" \",\"desc\":\" \\u003cpre\\u003e\\u003ccode\\u003e  /**\\n     * 师资管理-修改状态接口\\n     *\\n     * @param id\\n     * @param updateStateReq\\n     * @return\\n     */\\n    @PutMapping(\\\"/{id}/state/1\\\")\\n    public RadishResponse\\u0026lt;Boolean\\u0026gt; changeState(@PathVariable(\\\"id\\\") Integer id,\\n            @RequestBody UpdateStateReq updateStateReq) \\u003c/code\\u003e \\u003c/pre\\u003e\",\"method\":\"PUT\",\"req_params\":[{\"name\":\"id\",\"desc\":\"(Integer)\",\"example\":\"0\"}],\"project_id\":1806,\"yapiUrl\":\"http://yapi.kaikeba.com/\",\"menu\":\"B端-师资管理\"}")),
                        "utf-8");

        System.out.println(response);

    }

}
