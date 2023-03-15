package org.sorm.util;

import java.lang.reflect.Field;

public class ColumnField implements CommonField {
    private Field field;

    ColumnField(Field field) {
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Field getField() {
        return field;
    }
}
