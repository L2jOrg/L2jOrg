package org.l2j.commons.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

import static java.util.Objects.nonNull;

public class AnnotationNamingStrategy implements NamingStrategy {

    @Override
    public String getTableName(Class<?> type) {
        Table tableAnnotation = type.getAnnotation(Table.class);
        if(nonNull(tableAnnotation)) {
            return tableAnnotation.value();
        }
        return type.getSimpleName();
    }

    @Override
    public String getColumnName(RelationalPersistentProperty property) {
        Column columnAnnotation = property.getField().getAnnotation(Column.class);
        if(nonNull(columnAnnotation)) {
            return columnAnnotation.value();
        }
        return property.getName();
    }

    @Override
    public String getReverseColumnName(RelationalPersistentProperty property) {
        return getColumnName(property);

    }
}
