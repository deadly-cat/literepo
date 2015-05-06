package ingvar.android.literepo.test.mock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Igor Zubenko on 2015.05.06.
 */
public class TestHelper extends SQLiteOpenHelper {

    public TestHelper(Context context) {
        super(context, "test", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            db.execSQL(CREATE_TABLE_PARENT);
            db.execSQL(CREATE_TABLE_CHILD);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public static final String CREATE_TABLE_PARENT = "create table parent(name not null unique)";
    public static final String CREATE_TABLE_CHILD = "create table child(parent_id integer, name not null unique)";

}
