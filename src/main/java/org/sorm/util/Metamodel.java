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

    public String buildInsertRequest() {
        String className = clss.getSimpleName().toUpperCase();
        String primaryKeyColumnName = getPrimaryKeyColumnName(className);
        List<String> columnNames = getColumnNames();
        columnNames.add(0, primaryKeyColumnName);

        String columnElement = columnNames.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
        String questionMarksElement = getQuestionMarksElement(columnNames);

        return "INSERT INTO " + className +
                " (" + columnElement + ") values" +
                " (" + questionMarksElement + ")";
    }

    private String getQuestionMarksElement(List<String> columnNames) {
        return IntStream.range(0, columnNames.size())
                        .mapToObj(index -> "?")
                        .collect(Collectors.joining(", "));
    }

    private List<String> getColumnNames() {
        return getColumns()
                .stream()
                .map(ColumnField::getName)
                .collect(Collectors.toList());
    }

    private String getPrimaryKeyColumnName(String className) {
        return getPrimaryKey()
                .map(PrimaryKeyField::getName)
                .orElseThrow(() -> missingPrimaryKey(className));
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

    static class TempHolder {
        private final boolean isPresent;
        private final CommonField value;

        private TempHolder(
                boolean isPresent,
                CommonField value) {
            this.value = value;
            this.isPresent = isPresent;
        }

        static TempHolder of(CommonField value) {
            return new TempHolder(true, value);
        }

        static TempHolder empty() {
            return new TempHolder(false, null);
        }

        boolean isPresent() {
            return isPresent;
        }

        CommonField unwrap() {
            return value;
        }
    }
}
