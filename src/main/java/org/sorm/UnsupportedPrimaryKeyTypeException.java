package org.sorm;

public class UnsupportedPrimaryKeyTypeException extends IllegalArgumentException {

   private UnsupportedPrimaryKeyTypeException(String message) {
      super(message);
   }

   static UnsupportedPrimaryKeyTypeException illegalPrimaryKeyType(String allowedPrimaryKeyTypes) {
      return new UnsupportedPrimaryKeyTypeException(
              String.format("Allowed primary key types: %s", allowedPrimaryKeyTypes));
   }
}
