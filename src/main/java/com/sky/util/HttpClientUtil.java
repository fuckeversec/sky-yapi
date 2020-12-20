package com.sky.util;

import com.sky.upload.Cookie;
import com.sky.upload.HttpClientGenerator;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * http client 请求工具类
 *
 * @author gangyf
 * @since 2018/1/26.
 */
public class HttpClientUtil {

    private static final HttpClientGenerator HTTP_CLIENT_GENERATOR = new HttpClientGenerator();


    /**
     * TLSv1.2协议对应client
     */
    private static volatile CloseableHttpClient httpclient;
    private static volatile CloseableHttpClient httpclientWithCookie;

    public static CloseableHttpClient getHttpclient(List<Cookie> cookies) {

        if (CollectionUtils.isEmpty(cookies)) {
            return getHttpclient();
        } else {
            if (httpclientWithCookie == null) {
                synchronized (HttpClientUtil.class) {
                    if (httpclientWithCookie == null) {
                        try {
                            // 如果配置了cookies, 优先使用cookies, 旧版本使用token创建接口, 会创建空用户
                            httpclientWithCookie = HTTP_CLIENT_GENERATOR.getClient(cookies);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return httpclientWithCookie;
        }
    }

    public static CloseableHttpClient getHttpclient() {
        if (httpclient == null) {
            synchronized (HttpClientUtil.class) {
                if (httpclient == null) {
                    try {
                        httpclient = HTTP_CLIENT_GENERATOR.getClient(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return httpclient;
    }


    /**
     * 获取HttpGet
     *
     * @param url the url
     * @param accept the accept
     * @param contentType the content type
     * @return HttpGet http get
     * @throws IOException the io exception
     */
    public static HttpGet getHttpGet(String url, String accept, String contentType) throws IOException {
        HttpGet httpGet;
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
     * CloseableHttpResponse 转字符串
     *
     * @param response the response
     * @param charset the charset
     * @return String string
     * @throws IOException the io exception
     */
    public static String objectToString(CloseableHttpResponse response, String charset) throws IOException {
        try {
            HttpEntity resEntity = response.getEntity();
            String responseBaby = null;
            if (resEntity != null) {
                responseBaby = EntityUtils.toString(resEntity, charset);
            }
            return responseBaby;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Create ignore verify ssl ssl context.
     *
     * @return the ssl context
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyManagementException   the key management exception
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * Sets httpclient with cookie.
     *
     * @param httpclientWithCookie the httpclient with cookie
     */
    public static void setHttpclientWithCookie(CloseableHttpClient httpclientWithCookie) {
        HttpClientUtil.httpclientWithCookie = httpclientWithCookie;
    }
}
