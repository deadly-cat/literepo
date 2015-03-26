package ingvar.android.literepo.conversion;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public interface Converter<T> {

    ContentValues convert(T entity);

    T convert(Cursor cursor);

}
