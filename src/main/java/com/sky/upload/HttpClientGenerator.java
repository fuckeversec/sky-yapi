package com.sky.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * HttpClient连接池配置
 *
 * @author gangyanfeng@eversec.cn
 */
public class HttpClientGenerator {

    private final ObjectMapper objectMapper;

    private final PoolingHttpClientConnectionManager connectionManager;

    public HttpClientGenerator() {

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setMaxTotal(3);
        connectionManager.setDefaultMaxPerRoute(3);
    }

    private SSLContext createIgnoreVerifySSL()
            throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, null, null);
        SSLContext.setDefault(ctx);
        return ctx;
    }

    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
            // 优先绕过安全证书
            return new SSLConnectionSocketFactory(createIgnoreVerifySSL());
        } catch (KeyManagementException | NoSuchAlgorithmException ignored) {
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    public CloseableHttpClient getClient(List<Cookie> cookies) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(connectionManager);

        //解决post/redirect/post 302跳转问题
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
        socketConfigBuilder.setSoKeepAlive(true).setTcpNoDelay(true);
        socketConfigBuilder.setSoTimeout(500);
        SocketConfig socketConfig = socketConfigBuilder.build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);
        httpClientBuilder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(1, true));
        generateCookie(httpClientBuilder, cookies);
        return httpClientBuilder.build();
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, List<Cookie> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }

        CookieStore cookieStore = new BasicCookieStore();
        for (Cookie cookie : cookies) {

            BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
            basicClientCookie.setDomain(cookie.getDomain());
            cookieStore.addCookie(basicClientCookie);
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }

    private List<Cookie> parseCookies(String cookies) {
        try {
            return objectMapper.readValue(cookies, new TypeReference<List<Cookie>>() {});
        } catch (JsonProcessingException ignored) {
        }
        return new ArrayList<>();
    }

}
