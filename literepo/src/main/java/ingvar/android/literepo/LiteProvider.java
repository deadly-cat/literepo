package ingvar.android.literepo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import ingvar.android.literepo.builder.SqlBuilder;

/**
 * Basic provider. Can process requests created by {@link ingvar.android.literepo.builder.UriBuilder}.
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public abstract class LiteProvider extends ContentProvider {

    protected SQLiteOpenHelper helper;
    protected SQLiteDatabase writer;
    protected SQLiteDatabase reader;

    @Override
    public boolean onCreate() {
        helper = provideOpenHelper();
        writer = helper.getWritableDatabase();
        reader = helper.getReadableDatabase();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlBuilder query = new SqlBuilder(uri, selection, selectionArgs);
        Cursor result = reader.query(query.getFrom(), projection, query.getSelection(), query.getArgs(), null, null, sortOrder);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = writer.insertOrThrow(new SqlBuilder(uri).getFrom(), null, values);
        return Uri.withAppendedPath(uri, Long.toString(id));
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int inserted = 0;

        writer.beginTransaction();
        try {
            String tableName = new SqlBuilder(uri).getFrom();
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
        SqlBuilder query = new SqlBuilder(uri, selection, selectionArgs);
        int updated = writer.update(query.getFrom(), values, query.getSelection(), query.getArgs());
        return updated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlBuilder query = new SqlBuilder(uri, selection, selectionArgs);
        int deleted = writer.delete(query.getFrom(), query.getSelection(), query.getArgs());
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

}
