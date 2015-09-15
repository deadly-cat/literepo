package ingvar.android.literepo.util;

import android.database.Cursor;

import java.math.BigDecimal;

/**
 * Helpful methods for working with cursor.
 *
 * Created by Igor Zubenko on 2014.10.30.
 */
public class CursorCommon {

    public static String string(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    public static int integer(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public static long longv(Cursor cursor, String key) {
        return cursor.getLong(cursor.getColumnIndex(key));
    }

    public static boolean bool(Cursor cursor, String key) {
        boolean result;
        switch (cursor.getType(cursor.getColumnIndex(key))) {
            case Cursor.FIELD_TYPE_STRING:
                result = Boolean.parseBoolean(string(cursor, key));
                break;
            case Cursor.FIELD_TYPE_FLOAT:
            case Cursor.FIELD_TYPE_INTEGER:
                result = integer(cursor, key) > 0;
                break;
            default:
                throw new IllegalArgumentException("Can't get boolean from field " + key);
        }
        return result;
    }

    public static byte[] blob(Cursor cursor, String key) {
        return cursor.getBlob(cursor.getColumnIndex(key));
    }

    public static BigDecimal bigDecimal(Cursor cursor, String key) {
        BigDecimal result = null;
        try {
            result = new BigDecimal(cursor.getDouble(cursor.getColumnIndex(key)));
        } catch (Exception e) {
            String amount = string(cursor, key);
            if(amount != null) {
                result = new BigDecimal(amount);
            }
        }
        return result;
    }

    private CursorCommon() {}
}
