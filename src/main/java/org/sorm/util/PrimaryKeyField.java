package org.sorm.util;

import org.sorm.PrimaryKey;

import java.lang.reflect.Field;

public class PrimaryKeyField implements CommonField {
   private Field field;
   private PrimaryKey primaryKey;

   PrimaryKeyField(Field field) {
      this.field = field;
      this.primaryKey = this.field.getAnnotation(PrimaryKey.class);
   }

   public String getName() {
      return primaryKey.name();
   }

   public Class<?> getType() {
      return field.getType();
   }

   public Field getField() {
      return field;
   }
}
