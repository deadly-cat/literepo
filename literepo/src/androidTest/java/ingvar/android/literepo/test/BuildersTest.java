package ingvar.android.literepo.test;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

import ingvar.android.literepo.Query;
import ingvar.android.literepo.UriBuilder;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class BuildersTest extends TestCase {

    public void testUriBuilder() {
        String expected = "content://example.com/example?f1.eq=v1&f2.gt=v2&f3.in=1~2~3~4";

        Uri uri = new UriBuilder()
            .scheme("content")
            .authority("example.com")
            .table("example")
            .condition().field("f1").eq().value("v1").build()
            .condition().field("f2").gt().value("v2").build()
            .condition().field("f3").in().values(Arrays.asList(1,2,3,4)).build()
            .build();

        assertEquals("Built Uri not match", expected, uri.toString());
    }

    public void testSqlBuilder() {
        String expected = "f0 = ? and f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)";
        List<String> expectedValues = Arrays.asList("42", "v1", "v2", "1", "2", "3", "4");

        Uri uri = Uri.parse("content://example.com/example?f1.eq=v1&f2.gt=v2&f3.in=1~2~3~4");
        Query query = new Query("f0 = ?", new String[] {"42"}).parse(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

}
