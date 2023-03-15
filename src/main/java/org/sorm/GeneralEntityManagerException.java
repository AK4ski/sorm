package org.sorm;

class GeneralEntityManagerException extends RuntimeException {
    private GeneralEntityManagerException(String message, Exception e) {
        super(message, e);
    }

    static GeneralEntityManagerException persistFailed(Exception e) {
        return new GeneralEntityManagerException("Failed to persist: ", e);
    }
}
