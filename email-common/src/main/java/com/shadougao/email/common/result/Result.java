package com.shadougao.email.common.result;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4359709211352400087L;

    // 自定义状态码
    private Integer code;
    // 返回数据体
    private T data;
    // 是否成功
    private String type;
    // 提示内容
    private String message;

    @NotNull
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), data, "success", ResultEnum.SUCCESS.getMsg());
    }

    @NotNull
    public static Result<?> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), new String[]{}, "success", ResultEnum.SUCCESS.getMsg());
    }

    @NotNull
    public static <T> Result<T> error(ResultEnum code, String message) {
        return new Result<>(code.getCode(), null, "error", message);
    }

}

