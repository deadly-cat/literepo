package ingvar.android.literepo.examples.storage;

import android.database.sqlite.SQLiteOpenHelper;

import ingvar.android.literepo.LiteProvider;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public class ExampleProvider extends LiteProvider {

    @Override
    protected SQLiteOpenHelper provideOpenHelper() {
        return null;
    }

}
