package ingvar.android.literepo.util;

import android.database.Cursor;

import java.math.BigDecimal;

/**
 * Created by Igor Zubenko on 2014.10.30.
 */
public class CursorCommon {

    public static String string(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    public static int integer(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public static Long longv(Cursor cursor, String key) {
        return cursor.getLong(cursor.getColumnIndex(key));
    }

    public static boolean bool(Cursor cursor, String key) {
        return Boolean.valueOf(cursor.getString(cursor.getColumnIndex(key)));
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
