package org.nanospark.versionablehelper.core.exception;

public class SystemException extends VersionableException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public static interface MessageConstants {
        String DUPLICATED_EFFECTIVE_RECORD = "Duplicated effective record in one effective date";
        String PROPERTY_IS_NOT_EXISTS = "Property path is not exists in the given entity";

        String EFFECTIVE_DATE_IS_NULL = "the effectiveDate of the given VersionablePO is null";
    }
}
