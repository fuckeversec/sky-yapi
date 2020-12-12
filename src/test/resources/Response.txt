package com.sky.api.rpc;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 响应信息封装
 *
 * @author gangyf
 * @since 2020/10/10 2:18 PM
 */
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Response<T> implements Serializable {

    public static final int CODE_OK = 200;
    public static final int CODE_ERROR = 500;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <T> Response<T> ok() {
        return Response.<T>builder().code(CODE_OK).message("成功").build();
    }

    public static <T> Response<T> ok(T data) {
        return Response.<T>builder().code(CODE_OK).message("成功").data(data).build();
    }

    public static <T> Response<T> no() {
        return Response.<T>builder().code(CODE_ERROR).message("失败").build();
    }

    public static <T> Response<T> no(Integer code, String message) {
        return Response.<T>builder().code(code).message(message).build();
    }

    public static <T> Response<T> no(Integer code, String message, T data) {
        return Response.<T>builder().code(code).message(message).data(data).build();
    }

    public static Response<String> no(ErrorCode errorCode) {
        return Response.<String>builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
    }

}
