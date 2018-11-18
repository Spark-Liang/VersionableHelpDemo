package com.lzh.versionablehelper.exception;

public class VersionableException extends RuntimeException {

    public VersionableException(String message) {
        super(message);
    }

    public VersionableException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionableException(Throwable cause) {
        super(cause);
    }

    public static interface MessageConstants{
        String SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID = "should provide effective date or master id to search versionablePO";
    }
}
