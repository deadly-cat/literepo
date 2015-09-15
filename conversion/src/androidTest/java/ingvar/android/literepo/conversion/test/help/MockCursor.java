package ingvar.android.literepo.conversion.test.help;

import android.database.AbstractCursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ingvar.android.literepo.conversion.annotation.Column;

/**
 * Created by Igor Zubenko on 2015.04.03.
 */
public class MockCursor extends AbstractCursor {

    private static final String[] EA = {};

    private Object object;
    private List<String> names;
    private List<Field> columns;

    public MockCursor(Object object) {
        this.object = object;
        this.names = new ArrayList<>();
        this.columns = new ArrayList<>();

        Class tmp = object.getClass();
        while(!Object.class.equals(tmp)) {
            for(Field field : tmp.getDeclaredFields()) {
                Column ca = field.getAnnotation(Column.class);
                if(ca != null) {
                    field.setAccessible(true);
                    String name = (ca.value() == null || ca.value().isEmpty()) ? field.getName() : ca.value();
                    names.add(name);
                    columns.add(field);
                }
            }
            tmp = tmp.getSuperclass();
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String[] getColumnNames() {
        return names.toArray(EA);
    }

    @Override
    public String getString(int column) {
        return get(column);
    }

    @Override
    public short getShort(int column) {
        return get(column);
    }

    @Override
    public int getInt(int column) {
        return get(column);
    }

    @Override
    public long getLong(int column) {
        Object value = get(column);
        if(value instanceof Date) {
            return ((Date) value).getTime();
        }
        return (long) value;
    }

    @Override
    public float getFloat(int column) {
        return get(column);
    }

    @Override
    public double getDouble(int column) {
        return get(column);
    }

    @Override
    public boolean isNull(int column) {
        return get(column) != null;
    }

    @Override
    public byte[] getBlob(int column) {
        return get(column);
    }

    private <T> T get(int column) {
        try {
            return (T) columns.get(column).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
