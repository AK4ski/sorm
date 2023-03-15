package org.sorm;

import org.sorm.util.ColumnField;
import org.sorm.util.Metamodel;
import org.sorm.util.PrimaryKeyField;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static org.sorm.GeneralEntityManagerException.persistFailed;

public class EntityManagerImpl<T> implements EntityManager<T> {
   private final String dbUrl;
   private final String dbUsername;
   private final String dbPassword;
   private final PrimaryKeyGenerator idGenerator;

   public EntityManagerImpl(EntityManagerConfig entityManagerConfig) {
      this.dbUrl = entityManagerConfig.getDbUrl();
      this.dbUsername = entityManagerConfig.getDbUsername();
      this.dbPassword = entityManagerConfig.getDbPassword();
      this.idGenerator = entityManagerConfig.getIdGenerator();
   }

   @Override
   public void persist(T entity) {
      try {
         buildMetamodelPrepareAndExecuteStatement(entity);
      } catch (Exception e) {
         throw persistFailed(e);
      }
   }

   @Override
   public T read(long primaryKey, Class<T> clzz) {
      return null;
   }

   private void buildMetamodelPrepareAndExecuteStatement(T entity) throws Exception {
      try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
         Metamodel metamodel = Metamodel.of(entity.getClass());
         PreparedStatement statement = connection.prepareStatement(metamodel.buildInsertRequest());
         setPrimaryKeyValue(entity, metamodel, statement);
         setColumnValues(entity, metamodel, statement);

         statement.executeUpdate();
      }
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

      idGenerator.extracted(entity, metamodel, primaryKeyType, statement);
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
