package org.sorm;

import org.sorm.util.ColumnField;
import org.sorm.util.Metamodel;
import org.sorm.util.PrimaryKeyField;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.sorm.GeneralEntityManagerException.*;

public class EntityManagerImpl<T> implements EntityManager<T> {
   private final String dbUrl;
   private final String dbUsername;
   private final String dbPassword;
   private final PrimaryKeyGenerator idGenerator;
   private final EntityConverter converter;

   public EntityManagerImpl(EntityManagerConfig entityManagerConfig) {
      this.dbUrl = entityManagerConfig.getDbUrl();
      this.dbUsername = entityManagerConfig.getDbUsername();
      this.dbPassword = entityManagerConfig.getDbPassword();
      this.idGenerator = entityManagerConfig.getIdGenerator();
      this.converter = entityManagerConfig.getConverter();
   }

   @Override
   public void persist(T entity) {
      try {
         buildMetamodelPrepareAndExecuteCreateStatement(entity);
      } catch (Exception e) {
         throw persistFailed(e);
      }
   }

   @Override
   public T find(Object primaryKey, Class<T> clzz) {
      try {
         return buildMetamodelPrepareAndExecuteReadStatement(primaryKey, clzz);
      } catch (Exception e) {
         throw findFailed(e);
      }
   }

   private void buildMetamodelPrepareAndExecuteCreateStatement(T entity) throws Exception {
      try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
         Metamodel metamodel = Metamodel.of(entity.getClass());

         PreparedStatement statement = connection.prepareStatement(metamodel.buildInsertRequest());
         setPrimaryKeyValue(entity, metamodel, statement);
         setColumnValues(entity, metamodel, statement);

         statement.executeUpdate();
      }
   }

   private T buildMetamodelPrepareAndExecuteReadStatement(Object primaryKey, Class<T> clzz) throws Exception {
      try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
         Metamodel metamodel = Metamodel.of(clzz);

         PreparedStatement statement = connection.prepareStatement(metamodel.buildSelectRequest());
         idGenerator.setPKStatement(primaryKey, statement);

         ResultSet resultSet = statement.executeQuery();

         return buildInstanceFrom(metamodel, resultSet);
      }
   }

   private T buildInstanceFrom(Metamodel metamodel, ResultSet resultSet) throws Exception {
      T entity = (T) metamodel.getClzz().getConstructor().newInstance();
      Collection<ColumnField> columns = metamodel.getColumns();
      metamodel
              .getPrimaryKey()
              .ifPresent(pk -> setEntityFields(pk, entity, resultSet, columns));

      return entity;
   }

   private void setEntityFields(
           PrimaryKeyField pk,
           T entity,
           ResultSet resultSet,
           Collection<ColumnField> columns) {
      Field primaryKeyField = pk.getField();
      String primaryKeyColumnName = pk.getName();
      Class<?> primaryKeyType = pk.getType();
         converter.convert(entity, resultSet, primaryKeyField, primaryKeyColumnName, primaryKeyType, columns);
   }

   private void setPrimaryKeyValue(
           T entity,
           Metamodel metamodel,
           PreparedStatement statement)
           throws Exception {
      Class<?> primaryKeyType = metamodel
              .getPrimaryKey()
              .map(PrimaryKeyField::getType)
              .orElseThrow();

      idGenerator.setPKFieldAndStatement(entity, metamodel, primaryKeyType, statement);
   }

   private void setColumnValues(
           T entity,
           Metamodel metamodel,
           PreparedStatement statement)
           throws Exception {
      List<ColumnField> columns = new ArrayList<>(metamodel.getColumns());
      for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
         ColumnField columnField = columns.get(columnIndex);
         Field field = columnField.getField();
         Class<?> fieldType = columnField.getType();

         field.setAccessible(true);
         Object value = field.get(entity);

         //TODO: add for all primitive types, String and different Date types
         if (fieldType == int.class) {
            statement.setInt(columnIndex + 2, (int) value);
         }
         if (fieldType == String.class) {
            statement.setString(columnIndex + 2, (String) value);
         }
      }
   }
}
