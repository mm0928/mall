package com.leyou.common.pojo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 * 异常结果处理对象
 */
@Data
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum exceptionEnum) {
        this.status = exceptionEnum.getCode();
        this.message = exceptionEnum.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
