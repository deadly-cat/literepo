package ingvar.android.literepo.conversion.test;

import android.content.ContentValues;

import junit.framework.TestCase;

import java.util.Date;

import ingvar.android.literepo.conversion.Converter;
import ingvar.android.literepo.conversion.ConverterFactory;
import ingvar.android.literepo.conversion.test.help.Contract;
import ingvar.android.literepo.conversion.test.help.MockCursor;
import ingvar.android.literepo.conversion.test.help.TestEntity;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public class ConverterTest extends TestCase {

    public void testConversionToContentValues() {
        TestEntity test = new TestEntity();
        test.integer = 42;
        test.real = 42.42f;
        test.string = "test_string";
        test.blob = new byte[] {1, 2, 3};
        test.date = new Date();

        Converter<TestEntity> converter = ConverterFactory.create(TestEntity.class);

        ContentValues values = converter.convert(test);

        assertEquals("Integers don't match", test.integer, values.getAsInteger(Contract.INTEGER));
        assertEquals("Reals don't match", test.real, values.getAsFloat(Contract.REAL));
        assertEquals("Strings don't match", test.string, values.getAsString(Contract.STRING));
        assertEquals("Blobs don't match", test.blob, values.getAsByteArray(Contract.BLOB));
        assertEquals("Dates don't match", test.date, new Date(values.getAsLong(Contract.DATE)));

    }

    public void testConversionToEntity() {
        TestEntity test = new TestEntity();
        test.integer = 42;
        test.real = 42.42f;
        test.string = "test_string";
        test.blob = new byte[] {1, 2, 3};
        test.date = new Date();

        Converter<TestEntity> converter = ConverterFactory.create(TestEntity.class);

        TestEntity entity = converter.convert(new MockCursor(test));

        assertEquals("Integers don't match", test.integer, entity.integer);
        assertEquals("Reals don't match", test.real, entity.real);
        assertEquals("Strings don't match", test.string, entity.string);
        assertEquals("Blobs don't match", test.blob, entity.blob);
        assertEquals("Dates don't match", test.date, entity.date);
    }

}
