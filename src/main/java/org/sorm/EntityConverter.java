package org.sorm;

import org.sorm.util.ColumnField;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collection;

import static org.sorm.GeneralEntityManagerException.parseFailed;

public class EntityConverter {

   <T> void convert(
           T entity,
           ResultSet resultSet,
           Field primaryKeyField,
           String primaryKeyColumnName,
           Class<?> primaryKeyType,
           Collection<ColumnField> columnFields) {
      try {

         resultSet.next();

         //TODO: add for int and UUID or check if it is even necessary
         if (primaryKeyType == long.class) {
            long primaryKey = resultSet.getLong(primaryKeyColumnName);

            primaryKeyField.setAccessible(true);
            primaryKeyField.set(entity, primaryKey);
         }

         for (ColumnField columnField : columnFields) {
            Field field = columnField.getField();
            field.setAccessible(true);

            Class<?> columnType = columnField.getType();
            String columnName = columnField.getName();

            //TODO: add for other data types
            if (columnType == int.class) {
               int value = resultSet.getInt(columnName);
               field.set(entity, value);
            }

            if (columnType == String.class) {
               String value = resultSet.getString(columnName);
               field.set(entity, value);
            }

         }
      } catch (Exception e) {
         throw parseFailed(e);
      }
   }
}
