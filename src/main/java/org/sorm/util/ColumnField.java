package org.sorm.util;

import org.sorm.Column;

import java.lang.reflect.Field;

public class ColumnField implements CommonField {
   private Field field;
   private Column column;

   ColumnField(Field field) {
      this.field = field;
      this.column = field.getAnnotation(Column.class);
   }

   public String getName() {
      return column.name();
   }

   public Class<?> getType() {
      return field.getType();
   }

   public Field getField() {
      return field;
   }
}
