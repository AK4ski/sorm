package org.sorm.util;

import org.sorm.Column;
import org.sorm.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static org.sorm.util.PrimaryKeyMissingException.missingPrimaryKey;

public class Metamodel {

   private Class<?> clss;

   public static Metamodel of(Class<?> clss) {
      return new Metamodel(clss);
   }

   private Metamodel(Class<?> clss) {
      this.clss = clss;
   }

   public Class<?> getClzz() {
      return clss;
   }

   public String buildInsertRequest() {
      String className = getClassName();
      String primaryKeyColumnName = getPrimaryKeyColumnName(className);
      List<String> columnNames = getColumnNames();
      columnNames.add(0, primaryKeyColumnName);

      String columnElement = columnNames.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
      String questionMarksElement = getQuestionMarksElement(columnNames);


      return String.format("INSERT INTO %s (%s) VALUES (%s)", className, columnElement, questionMarksElement);
   }

   public String buildSelectRequest() {
      String className = getClassName();
      String primaryKeyColumnName = getPrimaryKeyColumnName(className);
      List<String> columnNames = getColumnNames();
      columnNames.add(0, primaryKeyColumnName);
      String columnElement = columnNames.stream().map(String::toUpperCase).collect(Collectors.joining(", "));

      return String.format("SELECT %s FROM %s WHERE %s = ?", columnElement, className, primaryKeyColumnName);
   }

   private String getClassName() {
      return clss.getSimpleName().toUpperCase();
   }
   private String getPrimaryKeyColumnName(String className) {
      return getPrimaryKey()
              .map(PrimaryKeyField::getName)
              .orElseThrow(() -> missingPrimaryKey(className));
   }

   private List<String> getColumnNames() {
      return getColumns()
              .stream()
              .map(ColumnField::getName)
              .collect(Collectors.toList());
   }

   private String getQuestionMarksElement(List<String> columnNames) {
      return IntStream
              .range(0, columnNames.size())
              .mapToObj(index -> "?")
              .collect(Collectors.joining(", "));
   }

   public Optional<PrimaryKeyField> getPrimaryKey() {
      return stream(clss.getDeclaredFields())
              .map(Metamodel::extractAnnotatedPrimaryKeyField)
              .filter(TempHolder::isPresent)
              .map(TempHolder::unwrap)
              .filter(PrimaryKeyField.class::isInstance)
              .map(PrimaryKeyField.class::cast)
              .findAny();
   }

   public Collection<ColumnField> getColumns() {
      return stream(clss.getDeclaredFields())
              .map(Metamodel::extractAnnotatedColumnField)
              .filter(TempHolder::isPresent)
              .map(TempHolder::unwrap)
              .filter(ColumnField.class::isInstance)
              .map(ColumnField.class::cast)
              .collect(Collectors.toList());
   }

   private static TempHolder extractAnnotatedPrimaryKeyField(Field field) {
      if (field.getAnnotation(PrimaryKey.class) != null) {
         return TempHolder.of(new PrimaryKeyField(field));
      }
      return TempHolder.empty();
   }

   private static TempHolder extractAnnotatedColumnField(Field field) {
      if (field.getAnnotation(Column.class) != null) {
         return TempHolder.of(new ColumnField(field));
      }
      return TempHolder.empty();
   }

   private static class TempHolder {
      private final boolean isPresent;
      private final CommonField value;

      private TempHolder(
              boolean isPresent,
              CommonField value) {
         this.value = value;
         this.isPresent = isPresent;
      }

      private static TempHolder of(CommonField value) {
         return new TempHolder(true, value);
      }

      private static TempHolder empty() {
         return new TempHolder(false, null);
      }

      private boolean isPresent() {
         return isPresent;
      }

      private CommonField unwrap() {
         return value;
      }
   }
}
