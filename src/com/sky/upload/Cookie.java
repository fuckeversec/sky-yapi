package com.sky.upload;

/**
 * 存储浏览器导出的cookie信息
 * @author gangyanfeng@eversec.cn
 * 2019/1/15 13:18
 */
public class Cookie {

    private String domain;
    private String name;
    private String value;
    private String path;
    private long maxAge;
    private String secure;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }
}