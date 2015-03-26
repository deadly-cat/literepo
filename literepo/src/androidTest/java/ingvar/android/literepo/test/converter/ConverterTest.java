package ingvar.android.literepo.test.converter;

import android.content.ContentValues;

import junit.framework.TestCase;

import java.util.Date;

import ingvar.android.literepo.annotation.Column;
import ingvar.android.literepo.annotation.Entity;
import ingvar.android.literepo.annotation.Type;
import ingvar.android.literepo.converter.Converter;
import ingvar.android.literepo.converter.ConverterFactory;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public class ConverterTest extends TestCase {

    public void testConversionToContentValues() {
        Person person = new Person("Alice", new Date());

        Converter<Person> converter = ConverterFactory.create(Person.class);

        ContentValues values = converter.convert(person);

        assertEquals("Names don't match", person.name, values.getAsString(Contract.COL_NAME));
        assertEquals("Dates don't match", person.birthday, values.getAsLong(Contract.COL_BIRTHDAY));
    }

    public void testConversionToEntity() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public static class Contract {
        public static final String TABLE_NAME = "item";
        public static final String COL_NAME = "name";
        public static final String COL_BIRTHDAY = "birthday";
    }

    @Entity(Contract.TABLE_NAME)
    public class Person {

        @Column(value = Contract.COL_NAME, nullable = false)
        private String name;
        @Column(nullable = false, type = Type.DATE)
        private Date birthday;

        public Person() {}

        public Person(String name, Date birthday) {
            this.name = name;
            this.birthday = birthday;
        }

    }

}
