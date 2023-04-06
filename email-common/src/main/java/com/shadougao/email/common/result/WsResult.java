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
public class WsResult<T> implements Serializable {
    public static final int NEW_MAIL = 1;       // 新邮件提醒
    public static final int PULL_MAIL_ING = 2;      // 邮件同步中
    public static final int PULL_MAIL_SUCCESS = 3;  // 邮件同步完成
    public static final int PULL_READY = 4; // 邮件同步准备
    public static final int PULL_READY_SUCCESS = 5; // 邮件同步准备完毕

    @Serial
    private static final long serialVersionUID = -1360434234406302434L;

    // 返回数据体
    private T data;
    // 是否成功
    private int type;


    @NotNull
    public static <T> WsResult<T> message(int type, T data) {
        return new WsResult<>(data, type);
    }

}

