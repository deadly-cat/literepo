package ingvar.android.literepo.conversion.test;

import android.content.ContentValues;

import junit.framework.TestCase;

import java.util.Date;

import ingvar.android.literepo.conversion.Converter;
import ingvar.android.literepo.conversion.ConverterFactory;
import ingvar.android.literepo.conversion.annotation.Column;
import ingvar.android.literepo.conversion.annotation.Type;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public class ConverterTest extends TestCase {

    public void testConversionToContentValues() {
        Test test = new Test();
        test.integer = 42;
        test.real = 42.42f;
        test.string = "test_string";
        test.blob = new byte[] {1, 2, 3};
        test.date = new Date();

        Converter<Test> converter = ConverterFactory.create(Test.class);

        ContentValues values = converter.convert(test);

        assertEquals("Integers don't match", test.integer, values.getAsInteger(Contract.INTEGER));
        assertEquals("Reals don't match", test.real, values.getAsFloat(Contract.REAL));
        assertEquals("Strings don't match", test.string, values.getAsString(Contract.STRING));
        assertEquals("Blobs don't match", test.blob, values.getAsByteArray(Contract.BLOB));
        assertEquals("Dates don't match", test.date, new Date(values.getAsLong(Contract.DATE)));

    }

    public void testConversionToEntity() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public static class Contract {
        public static final String INTEGER = "integer";
        public static final String REAL = "real";
        public static final String STRING = "string";
        public static final String BLOB = "blob";
        public static final String DATE = "date";


    }

    public static class Test {

        @Column(type = Type.INTEGER)
        private Integer integer;
        @Column(type = Type.REAL)
        private Float real;
        @Column(value = Contract.STRING, nullable = false)
        private String string;
        @Column(type = Type.BLOB)
        private byte[] blob;
        @Column(nullable = false, type = Type.DATE)
        private Date date;

        public Test() {}

    }

}
