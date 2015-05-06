package ingvar.android.literepo.test;

import android.database.Cursor;
import android.test.ProviderTestCase2;

import ingvar.android.literepo.test.mock.TestProvider;
import ingvar.android.literepo.util.CursorCommon;

import static ingvar.android.literepo.test.mock.TestContract.AUTHORITY;
import static ingvar.android.literepo.test.mock.TestContract.Parent;

/**
 * Created by Igor Zubenko on 2015.05.06.
 */
public class ProviderTest extends ProviderTestCase2<TestProvider> {

    private Cursor cursor;

    public ProviderTest() {
        super(TestProvider.class, AUTHORITY);
    }

    public void testSimpleQuery() {
        execute("insert into parent(name) values ('test')");

        cursor = getMockContentResolver().query(Parent.CONTENT_URI, Parent.PROJECTION, null, null, null);
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "test", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testQuery() {

    }

    public void testJoinedQuery() {

    }

    public void testInsert() {

    }

    public void testBulkInsert() {

    }

    public void testUpdate() {

    }

    public void testDelete() {

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        execute("delete from child");
        execute("delete from parent");
        if(cursor != null) {
            cursor.close();
        }
    }

    protected void execute(String sql) {
        getProvider().writer().execSQL(sql);
    }

}
