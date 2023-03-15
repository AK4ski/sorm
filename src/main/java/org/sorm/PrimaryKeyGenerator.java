package org.sorm;

import org.sorm.util.Metamodel;
import org.sorm.util.PrimaryKeyField;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.UUID.*;
import static org.sorm.UnsupportedPrimaryKeyTypeException.*;

public class PrimaryKeyGenerator {
   private AtomicLong longIdGenerator = new AtomicLong(0L);
   private AtomicInteger integerIdGenerator = new AtomicInteger(0);

   public <T> void extracted(T entity, Metamodel metamodel, Class<?> primaryKeyType, PreparedStatement statement) throws Exception {

      if (primaryKeyType != long.class &&
          primaryKeyType != int.class &&
          primaryKeyType != UUID.class) {
         throw illegalPrimaryKeyType("long, int, UUID");
      }

      if (primaryKeyType == long.class) {
         long id = longIdGenerator.incrementAndGet();
         Field field = metamodel.getPrimaryKey().map(PrimaryKeyField::getField).orElseThrow();
         field.setAccessible(true);
         field.set(entity, id);

         statement.setLong(1, id);
      }
      if (primaryKeyType == int.class) {
         int id = integerIdGenerator.incrementAndGet();
         Field field = metamodel.getPrimaryKey().map(PrimaryKeyField::getField).orElseThrow();
         field.setAccessible(true);
         field.set(entity, id);

         statement.setLong(1, id);
      }
      if (primaryKeyType == UUID.class) {
         UUID id = randomUUID();
         Field field = metamodel.getPrimaryKey().map(PrimaryKeyField::getField).orElseThrow();
         field.setAccessible(true);
         field.set(entity, id);

         statement.setString(1, id.toString());
      }
   }
}
