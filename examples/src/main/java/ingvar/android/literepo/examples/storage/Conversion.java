package ingvar.android.literepo.examples.storage;

import java.util.HashMap;
import java.util.Map;

import ingvar.android.literepo.conversion.Converter;
import ingvar.android.literepo.conversion.ConverterFactory;
import ingvar.android.literepo.examples.domain.Person;

/**
 * Created by Igor Zubenko on 2015.03.27.
 */
public class Conversion {

    private static final Map<Class, Converter> converters = new HashMap<>();

    static {
        synchronized (Conversion.class) {
            converters.put(Person.class, ConverterFactory.create(Person.class));
        }
    }

    public static <T> Converter<T> getConverter(Class<T> clazz) {
        return converters.get(clazz);
    }

}
