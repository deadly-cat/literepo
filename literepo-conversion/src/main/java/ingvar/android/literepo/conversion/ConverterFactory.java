package ingvar.android.literepo.conversion;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
public class ConverterFactory {

    public static <T> Converter<T> create(Class entity) {
        //TODO: learn about dynamic code generation.
        return new ReflectionConverter<T>(entity);
    }

    private ConverterFactory() {}

}
