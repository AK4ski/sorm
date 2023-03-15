package org.sorm.util;

import java.lang.reflect.Field;

public class PrimaryKeyField implements CommonField {
    private Field field;

    PrimaryKeyField(Field field) {
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
