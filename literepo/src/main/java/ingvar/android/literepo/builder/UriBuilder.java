package ingvar.android.literepo.builder;

import android.net.Uri;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class UriBuilder {

    public static final String PARAM_QUERY = "q";
    public static final String DELIMITER_LIST = "~d";
    public static final String DELIMITER_QUERY = "~q";
    public static final String QUERY_AND = "~a";
    public static final String QUERY_OR = "~o";

    public static final List<String> RESERVED = Collections.unmodifiableList(Arrays.asList(DELIMITER_LIST, DELIMITER_QUERY, QUERY_AND, QUERY_OR));

    private String scheme;
    private String authority;
    private String table;
    private StringBuilder query;

    public UriBuilder() {
        query = new StringBuilder();
        scheme = "content"; //default
    }

    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public UriBuilder authority(String authority) {
        this.authority = authority;
        return this;
    }

    public UriBuilder table(String table) {
        this.table = table;
        return this;
    }

    public UriBuilder or() {
        query.append(QUERY_OR);
        return this;
    }

    public UriBuilder eq(String field, Object value) {
        return condition(field, Operator.EQUALS, value.toString());
    }

    public UriBuilder gt(String field, Object value) {
        return condition(field, Operator.GREATER_THAN, value.toString());
    }

    public UriBuilder gte(String field, Object value) {
        return condition(field, Operator.GREATER_THAN_OR_EQUALS, value.toString());
    }

    public UriBuilder lt(String field, Object value) {
        return condition(field, Operator.LOWER_THAN, value.toString());
    }

    public UriBuilder lte(String field, Object value) {
        return condition(field, Operator.LOWER_THAN_OR_EQUALS, value.toString());
    }

    public UriBuilder like(String field, Object value) {
        return condition(field, Operator.LIKE, value.toString());
    }

    public UriBuilder match(String field, Object value) {
        return condition(field, Operator.MATCH, value.toString());
    }

    public UriBuilder in(String field, Collection values) {
        StringBuilder value = new StringBuilder();
        for(Object v : values) {
            if(value.length() > 0) {
                value.append(DELIMITER_LIST);
            }
            checkReserved(v.toString());
            value.append(v);
        }
        return condition(field, Operator.IN, value.toString(), false);
    }

    public UriBuilder between(String field, Object min, Object max) {
        checkReserved(min.toString());
        checkReserved(max.toString());
        String value = min.toString() + DELIMITER_LIST + max.toString();
        return condition(field, Operator.BETWEEN, value, false);
    }

    public UriBuilder isNull(String field) {
        return condition(field, Operator.IS_NULL, null);
    }

    public UriBuilder isNotNull(String field) {
        return condition(field, Operator.IS_NOT_NULL, null);
    }

    public Uri build() {
        return new Uri.Builder()
            .scheme(scheme)
            .authority(authority)
            .path(table)
            .appendQueryParameter(PARAM_QUERY, query.toString())
            .build();
    }

    protected UriBuilder condition(String field, Operator operator, String value) {
        return condition(field, operator, value, true);
    }

    protected UriBuilder condition(String field, Operator operator, String value, boolean checkValue) {
        checkReserved(field);
        if(value != null && checkValue) {
            checkReserved(value);
        }

        if(query.length() > 0) {
            String tmp = query.toString();
            if(!(tmp.endsWith(QUERY_AND) || tmp.endsWith(QUERY_OR))) {
                query.append(QUERY_AND);
            }
        }
        query.append(field).append(DELIMITER_QUERY).append(operator.toUri());
        if(value != null) {
            query.append(DELIMITER_QUERY).append(value);
        }
        return this;
    }

    protected void checkReserved(String str) {
        for(String reserved : RESERVED) {
            if(str.contains(reserved)) {
                throw new IllegalArgumentException(String.format("Name/Value '%s' contains reserved sequence '%s'", str, reserved));
            }
        }
    }

}
