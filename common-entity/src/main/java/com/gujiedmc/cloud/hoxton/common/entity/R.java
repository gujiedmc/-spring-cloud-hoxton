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
}
