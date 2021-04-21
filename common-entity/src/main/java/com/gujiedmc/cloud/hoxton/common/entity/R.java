package com.gujiedmc.cloud.hoxton.common.entity;

import lombok.Data;

/**
 * @author gujiedmc
 * @date 2021-04-02
 */
@Data
public class R<T> {

    private int code;

    private String msg;

    private T data;

    public static R<?> ok() {
        R<?> r = new R<>();
        r.setCode(0);
        r.setMsg("success");
        return r;
    }

    public static R<?> error() {
        R<?> r = new R<>();
        r.setCode(-1);
        r.setMsg("error");
        return r;
    }

    public static R<?> error(Throwable throwable) {
        R<?> r = new R<>();
        r.setCode(-1);
        r.setMsg(throwable.getMessage());
        return r;
    }
}
