package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LyException extends RuntimeException{

    private ExceptionEnum exceptionEnum;

}
