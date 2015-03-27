package ingvar.android.literepo.conversion;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ingvar.android.literepo.conversion.annotation.Column;
import ingvar.android.literepo.conversion.annotation.Type;

/**
 * Created by Igor Zubenko on 2015.03.27.
 */
public class ReflectionConverter<T> implements Converter<T> {

    private Class<T> entityClass;
    private Set<Col> columns;

    public ReflectionConverter(Class<T> entityClass) {
        this.entityClass = entityClass;
        columns = new HashSet<>();

        //try to instantiate
        try {
            entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Entity class must have a default constructor without params", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        Class tmp = entityClass;
        while(!Object.class.equals(tmp)) {
            for(Field field : tmp.getDeclaredFields()) {
                Column ca = field.getAnnotation(Column.class);
                if(ca != null) {
                    field.setAccessible(true);
                    String name = (ca.value() == null || ca.value().isEmpty()) ? field.getName() : ca.value();
                    columns.add(new Col(field, ca.type(), name, ca.nullable()));
                }
            }
            tmp = tmp.getSuperclass();
        }
    }

    @Override
    public ContentValues convert(T entity) {
        ContentValues values = new ContentValues(columns.size());

        for(Col col : columns) {
            try {
                Object value = col.field.get(entity);
                if(value == null) {
                    if(col.nullable) {
                        values.putNull(col.name);
                    } else {
                        throw new IllegalStateException("Field '" + col.name + "' can't be null!");
                    }
                } else {
                    switch (col.type) {
                        case INTEGER:
                            values.put(col.name, ((Number) value).longValue());
                            break;
                        case REAL:
                            values.put(col.name, ((Number) value).doubleValue());
                            break;
                        case TEXT:
                            values.put(col.name, (String) value);
                            break;
                        case BLOB:
                            values.put(col.name, (byte[]) value);
                            break;
                        case DATE:
                            values.put(col.name, ((Date) value).getTime());
                            break;
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        return values;
    }

    @Override
    public T convert(Cursor cursor) {
        T entity;
        try {
            entity = entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Entity class must have a default constructor without params", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        for(Col col : columns) {
            try {
                switch (col.type) {
                    case INTEGER:
                        if(col.field.getType().equals(Integer.class)) {
                            col.field.set(entity, cursor.getInt(cursor.getColumnIndex(col.name)));
                        } else {
                            col.field.set(entity, cursor.getLong(cursor.getColumnIndex(col.name)));
                        }
                        break;
                    case REAL:
                        if(col.field.getType().equals(Float.class)) {
                            col.field.set(entity, cursor.getFloat(cursor.getColumnIndex(col.name)));
                        } else {
                            col.field.set(entity, cursor.getDouble(cursor.getColumnIndex(col.name)));
                        }
                        break;
                    case TEXT:
                        col.field.set(entity, cursor.getString(cursor.getColumnIndex(col.name)));
                        break;
                    case BLOB:
                        col.field.set(entity, cursor.getBlob(cursor.getColumnIndex(col.name)));
                        break;
                    case DATE:
                        col.field.set(entity, new Date(cursor.getLong(cursor.getColumnIndex(col.name))));
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    private class Col {
        private Field field;
        private Type type;
        private String name;
        private boolean nullable;

        public Col() {}

        public Col(Field field, Type type, String name, boolean nullable) {
            this.field = field;
            this.type = type;
            this.name = name;
            this.nullable = nullable;
        }
    }

}
