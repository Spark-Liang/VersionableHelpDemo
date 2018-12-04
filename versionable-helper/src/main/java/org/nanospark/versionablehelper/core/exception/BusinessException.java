package org.nanospark.versionablehelper.core.exception;

public class BusinessException extends VersionableException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }


}
