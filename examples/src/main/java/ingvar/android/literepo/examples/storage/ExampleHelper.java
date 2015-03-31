package ingvar.android.literepo.examples.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Igor Zubenko on 2015.03.31.
 */
public class ExampleHelper extends SQLiteOpenHelper {

    private static final String NAME = "example";
    private static final int VERSION = 1;

    public ExampleHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERSONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private static final String CREATE_PERSONS = "create table persons (name text not null unique, birthday integer not null)";

}
