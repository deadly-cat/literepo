package ingvar.android.literepo.test.mock;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Igor Zubenko on 2015.05.06.
 */
public class TestContract {

    public static final String AUTHORITY = "ingvar.android.literepo.test.provider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);

    public static class Parent {

        public static final String TABLE_NAME = "parent";
        public static final String TABLE_ALIAS = "p";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(URI, TABLE_NAME);
        public static final String[] PROJECTION = {"rowid as " + Col._ID, Col.NAME};

        public static class Col implements BaseColumns {
            public static final String NAME = "name";
        }

    }

    public static class Child {

        public static final String TABLE_NAME = "child";
        public static final String TABLE_ALIAS = "c";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(URI, TABLE_NAME);
        public static final String[] PROJECTION = {"rowid as " + Col._ID, Col.PARENT_ID, Col.NAME};
        public static final String[] PROJECTION_ALIASED = {"c.rowid as id", "p.name as parent_name", "c.name"};

        public static class Col implements BaseColumns {
            public static final String PARENT_ID = "parent_id";
            public static final String NAME = "name";
        }

    }

}
