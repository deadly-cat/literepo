package ingvar.android.literepo.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import ingvar.android.literepo.builder.UriBuilder;
import ingvar.android.literepo.builder.UriQuery;
import ingvar.android.literepo.test.mock.TestProvider;
import ingvar.android.literepo.util.CursorCommon;

import static ingvar.android.literepo.test.mock.TestContract.AUTHORITY;
import static ingvar.android.literepo.test.mock.TestContract.Child;
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
        write("insert into parent(name) values ('test')");

        cursor = getMockContentResolver().query(Parent.CONTENT_URI, Parent.PROJECTION, null, null, null);
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "test", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testQuery() {
        write("insert into parent(name) values ('test')");

        Uri uri = new UriBuilder()
        .authority(AUTHORITY)
        .table("parent")
        .query().eq(Parent.Col.NAME, "test").end()
        .build();

        cursor = getMockContentResolver().query(uri, Parent.PROJECTION, null, null, null);
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "test", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testJoinedQuery() {
        write("insert into parent(name) values ('parent')");
        write("insert into child(parent_id, name) values (1, 'child')");

        Uri uri = new UriBuilder()
        .authority(AUTHORITY)
        .table(Parent.TABLE_NAME, "p")
        .query().eq(Parent.Col.NAME, "parent").end()
        .join(Child.TABLE_NAME, "c")
            .eq(Child.Col.PARENT_ID, new UriQuery.SqlValue("p.rowid"))
        .end()
        .build();

        cursor = getMockContentResolver().query(uri, Child.PROJECTION_ALIASED, "p.rowid = ?", new String[] {"1"}, null);
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, "c.id"));
        assertEquals("Parent name mismatch", "parent", CursorCommon.string(cursor, "c.parent_name"));
        assertEquals("Child name mismatch", "child", CursorCommon.string(cursor, "c.name"));
    }

    public void testInsert() {
        ContentValues value = new ContentValues();
        value.put(Parent.Col.NAME, "insertTest");

        getMockContentResolver().insert(Parent.CONTENT_URI, value);

        cursor = read("select rowid as _id, name from parent");
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "insertTest", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testBulkInsert() {
        ContentValues[] values = new ContentValues[2];
        values[0] = createParent("bulk1");
        values[1] = createParent("bulk2");

        getMockContentResolver().bulkInsert(Parent.CONTENT_URI, values);

        cursor = read("select rowid as _id, name from parent");
        assertTrue("First value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 2, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "bulk1", CursorCommon.string(cursor, Parent.Col.NAME));
        assertTrue("Second value not found", cursor.moveToNext());
        assertEquals("ID mismatch", 2, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "bulk2", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testUpdate() {
        write("insert into parent(name) values ('parent')");

        ContentValues values = createParent("changed");

        Uri uri = new UriBuilder()
        .authority(AUTHORITY)
        .table(Parent.TABLE_NAME)
        .query().eq("rowid", 1).end()
        .build();

        getMockContentResolver().update(uri, values, null, null);

        cursor = read("select rowid as _id, name from parent");
        assertTrue("Value not found", cursor.moveToFirst());
        assertEquals("Values more than required", 1, cursor.getCount());
        assertEquals("ID mismatch", 1, CursorCommon.integer(cursor, Parent.Col._ID));
        assertEquals("Name mismatch", "changed", CursorCommon.string(cursor, Parent.Col.NAME));
    }

    public void testDelete() {
        write("insert into parent(name) values ('parent')");

        Uri uri = new UriBuilder()
        .authority(AUTHORITY)
        .table(Parent.TABLE_NAME)
        .query().eq(Parent.Col.NAME, "parent").end()
        .build();

        getMockContentResolver().delete(uri, null, null);

        cursor = read("select rowid as _id, name from parent");
        assertEquals("Values more than required", 0, cursor.getCount());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        write("delete from child");
        write("delete from parent");
        if(cursor != null) {
            cursor.close();
        }
    }

    protected void write(String sql) {
        getProvider().writer().execSQL(sql);
    }

    protected Cursor read(String sql, String... args) {
        return getProvider().reader().rawQuery(sql, args);
    }

    protected ContentValues createParent(String name) {
        ContentValues value = new ContentValues();
        value.put(Parent.Col.NAME, name);
        return value;
    }

}
