package org.sorm;

class GeneralEntityManagerException extends RuntimeException {
    private GeneralEntityManagerException(String message, Exception e) {
        super(message, e);
    }

    static GeneralEntityManagerException persistFailed(Exception e) {
        return new GeneralEntityManagerException("Failed to persist entity: ", e);
    }

    static GeneralEntityManagerException findFailed(Exception e) {
        return new GeneralEntityManagerException("Failed to find entity: ", e);
    }

    static GeneralEntityManagerException parseFailed(Exception e) {
        return new GeneralEntityManagerException("Failed to parse the result set to entity: ", e);
    }
}
