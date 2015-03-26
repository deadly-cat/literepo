package ingvar.android.literepo.test;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

import ingvar.android.literepo.Operator;
import ingvar.android.literepo.Query;
import ingvar.android.literepo.UriBuilder;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class BuildersTest extends TestCase {

    public void testUriBuilder() {
        //f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)
        Uri uri = new UriBuilder()
            .scheme("content")
            .authority("example.com")
            .table("example")
            .eq("f1", "v1")
            .gt("f2", "v2")
            .in("f3", Arrays.asList(1, 2, 3, 4))
            .build();

        assertEquals("Built Uri not match", URI, uri.toString());
    }

    public void testUriBuilderWithOr() {
        //(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)
        Uri uri = new UriBuilder()
            .scheme("content")
            .authority("example.com")
            .table("example")
            .eq("f1", "v1").or().eq("f1", "v11")
            .gt("f2", "v2").or().gt("f2", "v22")
            .build();

        assertEquals("Built Uri not match", URI_OR, uri.toString());
    }

    public void testUriBuilderWithOr2() {
        //(f1 = ? or f1 = ? or f2 > ?)
        Uri uri = new UriBuilder()
            .scheme("content")
            .authority("example.com")
            .table("example")
            .eq("f1", "v1")
            .or().eq("f1", "v11")
            .or().gt("f2", "v2")
            .build();

        assertEquals("Built Uri not match", URI_OR_2, uri.toString());
    }

    public void testSqlBuilder() {
        String expected = "f0 = ? and f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)";
        List<String> expectedValues = Arrays.asList("42", "v1", "v2", "1", "2", "3", "4");

        Uri uri = Uri.parse(URI);
        Query query = new Query("f0 = ?", new String[] {"42"}).parse(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testSqlBuilderWithOr() {
        String expected = "(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)";
        List<String> expectedValues = Arrays.asList("v1", "v11", "v2", "v22");

        Uri uri = Uri.parse(URI_OR);
        Query query = new Query().parse(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testSqlBuilderBetween() {
        String expected = "f1 between ? and ?";
        List<String> expectedValues = Arrays.asList("0", "10");

        Uri uri = new UriBuilder()
            .scheme("content")
            .authority("example.com")
            .table("example")
            .between("f1", 0, 10)
            .build();

        Query query = new Query().parse(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }


    private static final String COND_1 =
            "f1" + UriBuilder.DELIMITER_QUERY
                    + Operator.EQUALS.toUri() + UriBuilder.DELIMITER_QUERY
                    + "v1";

    private static final String COND_11 =
            "f1" + UriBuilder.DELIMITER_QUERY
                    + Operator.EQUALS.toUri() + UriBuilder.DELIMITER_QUERY
                    + "v11";

    private static final String COND_2 =
            "f2" + UriBuilder.DELIMITER_QUERY
                    + Operator.GREATER_THAN.toUri() + UriBuilder.DELIMITER_QUERY
                    + "v2";

    private static final String COND_22 =
            "f2" + UriBuilder.DELIMITER_QUERY
                    + Operator.GREATER_THAN.toUri() + UriBuilder.DELIMITER_QUERY
                    + "v22";

    private static final String COND_3 =
            "f3" + UriBuilder.DELIMITER_QUERY
                    + Operator.IN.toUri() + UriBuilder.DELIMITER_QUERY
                    + "1" + UriBuilder.DELIMITER_LIST + "2" + UriBuilder.DELIMITER_LIST + "3" + UriBuilder.DELIMITER_LIST + "4";

    //f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)
    private static final String URI = "content://example.com/example?" + UriBuilder.PARAM_QUERY + "="
            + COND_1 + UriBuilder.QUERY_AND + COND_2 + UriBuilder.QUERY_AND + COND_3;

    //(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)
    private static final String URI_OR = "content://example.com/example?" + UriBuilder.PARAM_QUERY + "="
            + COND_1 + UriBuilder.QUERY_OR + COND_11 + UriBuilder.QUERY_AND + COND_2 + UriBuilder.QUERY_OR + COND_22;

    //(f1 = ? or f1 = ? or f2 > ?)
    private static final String URI_OR_2 = "content://example.com/example?" + UriBuilder.PARAM_QUERY + "="
            + COND_1 + UriBuilder.QUERY_OR + COND_11 + UriBuilder.QUERY_OR + COND_2;

}
