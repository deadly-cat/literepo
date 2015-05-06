package ingvar.android.literepo.test.mock;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ingvar.android.literepo.LiteProvider;

/**
 * Created by Igor Zubenko on 2015.05.06.
 */
public class TestProvider extends LiteProvider {

    public SQLiteDatabase reader() {
        return reader;
    }

    public SQLiteDatabase writer() {
        return writer;
    }

    @Override
    protected SQLiteOpenHelper provideOpenHelper() {
        return new TestHelper(getContext());
    }

}
