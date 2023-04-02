package org.sorm.util;

import java.lang.reflect.Field;

interface CommonField {
   String getName();

   Class<?> getType();

   Field getField();
}
