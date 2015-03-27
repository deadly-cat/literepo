package ingvar.android.literepo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import ingvar.android.literepo.builder.Query;

/**
 * Basic provider. Can process requests created by {@link ingvar.android.literepo.builder.UriBuilder}.
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public abstract class LiteProvider extends ContentProvider {

    private SQLiteOpenHelper helper;
    private SQLiteDatabase writer;
    private SQLiteDatabase reader;

    @Override
    public boolean onCreate() {
        helper = provideOpenHelper();
        writer = helper.getWritableDatabase();
        reader = helper.getReadableDatabase();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Query query = new Query(selection, selectionArgs).parse(uri);
        Cursor result = reader.query(extractTableName(uri), projection, query.getSelection(), query.getArgs(), null, null, sortOrder);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = writer.insertOrThrow(extractTableName(uri), null, values);
        return Uri.withAppendedPath(uri, Long.toString(id));
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int inserted = 0;

        writer.beginTransaction();
        try {
            String tableName = extractTableName(uri);
            for(ContentValues value : values) {
                writer.insertOrThrow(tableName, null, value);
                inserted++;
            }
            writer.setTransactionSuccessful();
        } finally {
            writer.endTransaction();
        }

        return inserted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Query query = new Query(selection, selectionArgs).parse(uri);
        int updated = writer.update(extractTableName(uri), values, query.getSelection(), query.getArgs());
        return updated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Query query = new Query(selection, selectionArgs).parse(uri);
        int deleted = writer.delete(extractTableName(uri), query.getSelection(), query.getArgs());
        return deleted;
    }

    @Override
    public String getType(Uri uri) {
        //vnd.android.cursor.item for a single record
        //vnd.android.cursor.dir/ for multiple items.
        return null;
    }

    /**
     * Create {@link android.database.sqlite.SQLiteOpenHelper} for connect to DB.
     * @return DB connector
     */
    protected abstract SQLiteOpenHelper provideOpenHelper();

    /**
     * Extracted table name from Uri.
     * Default implementation used path as table name.
     * @param uri - query uri.
     * @return table name
     */
    protected String extractTableName(Uri uri) {
        return uri.getPath().replaceAll("/", "");
    }

}
