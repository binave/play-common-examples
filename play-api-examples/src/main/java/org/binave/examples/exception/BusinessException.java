package org.binave.examples.exception;

/**
 * 业务异常
 *
 * @author by bin jin on 2017/3/27.
 */
public abstract class BusinessException extends Exception {

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    protected abstract long getId();

}
