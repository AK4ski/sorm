package org.sorm.util;

class PrimaryKeyMissingException extends RuntimeException {
    private PrimaryKeyMissingException(String message) {
        super(message);
    }

    static PrimaryKeyMissingException missingPrimaryKey(String className) {
        String message = String.format("Id not specified for entity: %s", className);

        return new PrimaryKeyMissingException(message);
    }
}
