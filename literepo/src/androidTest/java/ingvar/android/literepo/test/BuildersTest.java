package ingvar.android.literepo.test;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

import ingvar.android.literepo.builder.Operator;
import ingvar.android.literepo.builder.SqlBuilder;
import ingvar.android.literepo.builder.UriBuilder;
import ingvar.android.literepo.builder.UriQuery;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class BuildersTest extends TestCase {

    public void testParsingTableName() {
        Uri uri = new UriBuilder()
            .authority("example.com")
            .table("test")
            .build();

        SqlBuilder query = new SqlBuilder(uri);
        assertEquals("Table name not match", "test", query.getFrom());
    }

    public void testUriBuilder() {
        //f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)
        Uri uri = new UriBuilder()
            .authority("example.com")
            .table("example")
            .where()
                .eq("f1", "v1")
                .gt("f2", "v2")
                .in("f3", Arrays.asList(1, 2, 3, 4))
            .end()
            .build();

        assertEquals("Built Uri not match", URI, uri.toString());
    }

    public void testUriBuilderWithOr() {
        //(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)
        Uri uri = new UriBuilder()
            .authority("example.com")
            .table("example")
            .where()
                .eq("f1", "v1").or().eq("f1", "v11")
                .gt("f2", "v2").or().gt("f2", "v22")
            .end()
            .build();

        assertEquals("Built Uri not match", URI_OR, uri.toString());
    }

    public void testUriBuilderWithOr2() {
        //(f1 = ? or f1 = ? or f2 > ?)
        Uri uri = new UriBuilder()
            .authority("example.com")
            .table("example")
            .where()
                .eq("f1", "v1")
                .or().eq("f1", "v11")
                .or().gt("f2", "v2")
            .end()
            .build();

        assertEquals("Built Uri not match", URI_OR_2, uri.toString());
    }

    public void testSqlBuilderSimple() {
        String expected = "f0 = ?";
        List<String> expectedValues = Arrays.asList("42");
        Uri uri = Uri.parse("content://example.com/example_path?some_param=some_value");

        SqlBuilder query = new SqlBuilder(uri, "f0 = ?", new String[] {"42"});

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
        assertEquals("Mistake in from", "example_path", query.getFrom());
    }

    public void testSqlBuilder() {
        String expected = "f1 = ? and f2 > ? and f3 in (?, ?, ?, ?) and f0 = ?";
        List<String> expectedValues = Arrays.asList("v1", "v2", "1", "2", "3", "4", "42");

        Uri uri = Uri.parse(URI);
        SqlBuilder query = new SqlBuilder(uri, "f0 = ?", new String[] {"42"});

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
        assertEquals("Mistake in from", "example", query.getFrom());
    }

    public void testSqlBuilderWithOr() {
        String expected = "(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)";
        List<String> expectedValues = Arrays.asList("v1", "v11", "v2", "v22");

        Uri uri = Uri.parse(URI_OR);
        SqlBuilder query = new SqlBuilder(uri);

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
            .where().between("f1", 0, 10).end()
            .build();

        SqlBuilder query = new SqlBuilder(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testEmptyValue() {
        String expected = "f1 = ?";
        List<String> expectedValues = Arrays.asList("");

        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("test")
        .where().eq("f1", "").end()
        .build();

        SqlBuilder query = new SqlBuilder(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testEmpty() {
        String expected = "";

        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("test")
        .build();

        SqlBuilder query = new SqlBuilder(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertTrue("Mistake in args", query.getArgs().length == 0);
    }

    public void testAlias() {
        //alias.f1 = ? and alias.f2 > ?
        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("example", "alias")
        .where()
            .eq("f1", "v1")
            .gt("f2", "v2")
        .end()
        .build();

        assertEquals("Built Uri not match", URI_ALIAS, uri.toString());


        String expected = "alias.f1 = ? and alias.f2 > ?";
        List<String> expectedValues = Arrays.asList("v1", "v2");
        SqlBuilder query = new SqlBuilder(uri);
        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
        assertEquals("Mistake in from", "example as alias", query.getFrom());
    }

    public void testSqlValue() {
        //f1 = (select field from test_table)
        UriQuery.SqlValue value = new UriQuery.SqlValue("(select field from test_table)");
        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("example")
        .where().eq("f1", value).end()
        .build();

        assertEquals("Built Uri not match", URI_VALUE_SQL, uri.toString());

        String expected = "f1 = (select field from test_table)";
        List<String> expectedValues = Arrays.asList();
        SqlBuilder query = new SqlBuilder(uri);
        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testInSqlValue() {
        //f1 in (select field from test_table)
        UriQuery.SqlValue value = new UriQuery.SqlValue("select field from test_table");
        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("test")
        .where().in("f1", value).end()
        .build();

        String expected = "f1 in (select field from test_table)";
        List<String> expectedValues = Arrays.asList();
        SqlBuilder query = new SqlBuilder(uri);
        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    public void testRawSql() {
        String rawQuery = "f1 = (select * from another_table)";
        String expected = "f0 > ? and " + rawQuery + " and f2 = ?";
        List<String> expectedValues = Arrays.asList("v0", "v2");

        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("example")
        .where()
            .gt("f0", "v0")
            .raw(rawQuery)
            .eq("f2", "v2")
        .end()
        .build();

        SqlBuilder query = new SqlBuilder(uri);

        assertEquals("Mistake in selection", expected, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
        assertEquals("Mistake in from", "example", query.getFrom());
    }

    public void testJoin() {
        //from example1 as a1
        //  join example2 as a2 on ((a2.f1 = a1.fe1 or a2.f2 > ve2))
        //  join example3 as a3 on (a2.f1 = a1.fe2)
        //  join example4 as a4
        //  join example5 as a5 on (a5.f5 in (select * from some_table))
        //where a1.f1 = ? and a2.f2 > ? and f0 = 42
        Uri uri = new UriBuilder()
        .authority("example.com")
        .table("example1", "a1")
        .where()
            .eq("f1", "v1")
            .gt("f2", "v2")
        .end()
        .join("example2", "a2")
            .eq("f1", new UriQuery.SqlValue("a1.fe1"))
            .or().gt("f2", "ve2")
        .end()
        .join("example3", "a3")
            .eq("f1", new UriQuery.SqlValue("a1.fe2"))
        .end()
        .join("example4", "a4")
        .end()
        .join("example5", "a5")
            .raw("a5.f5 in (select * from some_table)")
        .end()
        .build();

        assertEquals("Built Uri not match", URI_JOIN, uri.toString());

        String expectedFrom = "example1 as a1" +
                " join example2 as a2 on ((a2.f1 = a1.fe1 or a2.f2 > ?))" +
                " join example3 as a3 on (a3.f1 = a1.fe2)" +
                " join example4 as a4" +
                " join example5 as a5 on (a5.f5 in (select * from some_table))";
        String expectedWhere = "a1.f1 = ? and a1.f2 > ? and f0 = ?";
        List<String> expectedValues = Arrays.asList("ve2", "v1", "v2", "42");

        SqlBuilder query = new SqlBuilder(uri, "f0 = ?", new String[] {"42"});

        assertEquals("Mistake in from", expectedFrom, query.getFrom());
        assertEquals("Mistake in selection", expectedWhere, query.getSelection());
        assertEquals("Mistake in args", expectedValues, Arrays.asList(query.getArgs()));
    }

    private static final String DQ = UriQuery.DELIMITER_QUERY;
    private static final String VS = UriQuery.VALUE_SQL;

    private static final String COND_1 =
            "f1" + UriQuery.DELIMITER_QUERY
                    + Operator.EQUALS.toUri() + UriQuery.DELIMITER_QUERY
                    + "v1";

    private static final String COND_11 =
            "f1" + UriQuery.DELIMITER_QUERY
                    + Operator.EQUALS.toUri() + UriQuery.DELIMITER_QUERY
                    + "v11";

    private static final String COND_2 =
            "f2" + UriQuery.DELIMITER_QUERY
                    + Operator.GREATER_THAN.toUri() + UriQuery.DELIMITER_QUERY
                    + "v2";

    private static final String COND_22 =
            "f2" + UriQuery.DELIMITER_QUERY
                    + Operator.GREATER_THAN.toUri() + UriQuery.DELIMITER_QUERY
                    + "v22";

    private static final String COND_3 =
            "f3" + UriQuery.DELIMITER_QUERY
                    + Operator.IN.toUri() + UriQuery.DELIMITER_QUERY
                    + "1" + UriQuery.DELIMITER_LIST + "2" + UriQuery.DELIMITER_LIST + "3" + UriQuery.DELIMITER_LIST + "4";

    //f1 = ? and f2 > ? and f3 in (?, ?, ?, ?)
    private static final String URI = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=1&"
        + UriBuilder.PARAM_TABLE + "0=example&"
        + UriBuilder.PARAM_QUERY + "0=" + COND_1 + UriQuery.QUERY_AND + COND_2 + UriQuery.QUERY_AND + COND_3;

    //(f1 = ? or f1 = ?) and (f2 > ? or f2 > ?)
    private static final String URI_OR = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=1&"
        + UriBuilder.PARAM_TABLE + "0=example&"
        + UriBuilder.PARAM_QUERY + "0=" + COND_1 + UriQuery.QUERY_OR + COND_11 + UriQuery.QUERY_AND + COND_2 + UriQuery.QUERY_OR + COND_22;

    //(f1 = ? or f1 = ? or f2 > ?)
    private static final String URI_OR_2 = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=1&"
        + UriBuilder.PARAM_TABLE + "0=example&"
        + UriBuilder.PARAM_QUERY + "0=" + COND_1 + UriQuery.QUERY_OR + COND_11 + UriQuery.QUERY_OR + COND_2;

    //alias.f1 = ? and alias.f2 > ?
    private static final String URI_ALIAS = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=1&"
        + UriBuilder.PARAM_TABLE + "0=example.alias&"
        + UriBuilder.PARAM_QUERY + "0=" + COND_1 + UriQuery.QUERY_AND + COND_2;

    //f1 = (select field from test_table)
    private static final String URI_VALUE_SQL = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=1&"
        + UriBuilder.PARAM_TABLE + "0=example&"
        + UriBuilder.PARAM_QUERY + "0=f1"+UriQuery.DELIMITER_QUERY + Operator.EQUALS.toUri() + UriQuery.DELIMITER_QUERY
            + UriQuery.VALUE_SQL + "(select%20field%20from%20test_table)";

    //from example1 as a1
    //  join example2 as a2 on ((a2.f1 = a1.fe1 or a2.f2 > ve2))
    //  join example3 as a3 on (a3.f1 = a1.fe2)
    //  join example4 as a4
    //  join example5 as a5 on (a5.f5 in (select * from some_table))
    //where a1.f1 = ? and a2.f2 > ?
    private static final String URI_JOIN = "content://example.com/" + UriBuilder.PATH + "?"
        + UriBuilder.PARAM_TABLE_COUNT + "=5&"
        + UriBuilder.PARAM_TABLE + "0=example1.a1&"
        + UriBuilder.PARAM_QUERY + "0=" + COND_1 + UriQuery.QUERY_AND + COND_2 + "&"

        + UriBuilder.PARAM_TABLE + "1=example2.a2&"
        + UriBuilder.PARAM_QUERY + "1="
            + "f1" + DQ + Operator.EQUALS.toUri() + DQ + VS + "a1.fe1"
            + UriQuery.QUERY_OR
            + "f2" + DQ + Operator.GREATER_THAN.toUri() + DQ + "ve2" + "&"

        + UriBuilder.PARAM_TABLE + "2=example3.a3&"
        + UriBuilder.PARAM_QUERY + "2=" + "f1" + DQ + Operator.EQUALS.toUri() + DQ + VS + "a1.fe2" + "&"

        + UriBuilder.PARAM_TABLE + "3=example4.a4&"

        + UriBuilder.PARAM_TABLE + "4=example5.a5&"
        + UriBuilder.PARAM_QUERY + "4=" + VS + "a5.f5%20in%20(select%20*%20from%20some_table)";
}
