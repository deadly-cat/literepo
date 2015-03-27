package ingvar.android.literepo.examples.storage;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Igor Zubenko on 2015.03.27.
 */
public class ExampleContract {

    public static final String AUTHORITY = "ingvar.android.literepo.examples.provider";
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).build();

    public static class Person {

        public static final String TABLE_NAME = "person";
        public static final Uri URI = Uri.withAppendedPath(CONTENT_URI, TABLE_NAME);

        public static class Col implements BaseColumns {
            public static final String NAME = "name";
            public static final String BIRTHDAY = "birthday";
        }

    }

}
