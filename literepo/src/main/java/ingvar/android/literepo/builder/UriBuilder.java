package ingvar.android.literepo.builder;

import android.net.Uri;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Simple query builder. Parsed by {@link ingvar.android.literepo.builder.Query}
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public class UriBuilder {

    /**
     * name of parameter in the query
     */
    public static final String PARAM_QUERY = "q";
    /**
     * splitter for elements in the lists.
     */
    public static final String DELIMITER_LIST = "~d";
    /**
     * splitter for condition sections. E.q.: field.operator.value
     */
    public static final String DELIMITER_QUERY = "~q";
    /**
     * 'AND' operator
     */
    public static final String QUERY_AND = "~a";
    /**
     * 'OR' operator
     */
    public static final String QUERY_OR = "~o";

    /**
     * Reserved sequences what cannot be used in the query(fields names or values)
     */
    public static final List<String> RESERVED = Collections.unmodifiableList(Arrays.asList(DELIMITER_LIST, DELIMITER_QUERY, QUERY_AND, QUERY_OR));

    private String scheme;
    private String authority;
    private String table;
    private StringBuilder query;

    public UriBuilder() {
        query = new StringBuilder();
        scheme = "content"; //default
    }

    /**
     * By default scheme is 'content'.
     *
     * @param scheme uri scheme
     * @return builder
     */
    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Content provider authority.
     *
     * @param authority query authority
     * @return builder
     */
    public UriBuilder authority(String authority) {
        this.authority = authority;
        return this;
    }

    /**
     * Table name. Set as path in the {@link android.net.Uri}.
     *
     * @param table table name
     * @return builder
     */
    public UriBuilder table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Added 'OR' between prev and next conditions instead of 'AND'.
     *
     * @return builder
     */
    public UriBuilder or() {
        query.append(QUERY_OR);
        return this;
    }

    /**
     * Equals operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder eq(String column, Object value) {
        return condition(column, Operator.EQUALS, value.toString());
    }

    /**
     * Greater than operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder gt(String column, Object value) {
        return condition(column, Operator.GREATER_THAN, value.toString());
    }

    /**
     * Greater than or equals operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder gte(String column, Object value) {
        return condition(column, Operator.GREATER_THAN_OR_EQUALS, value.toString());
    }

    /**
     * Lower than operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder lt(String column, Object value) {
        return condition(column, Operator.LOWER_THAN, value.toString());
    }

    /**
     * Lower than or equals operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder lte(String column, Object value) {
        return condition(column, Operator.LOWER_THAN_OR_EQUALS, value.toString());
    }

    /**
     * Like operator.
     * Wraps value to %value%.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder like(String column, Object value) {
        return condition(column, Operator.LIKE, "%" + value.toString() + "%");
    }

    /**
     * Like operator.
     * Does not wrap value.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder likeRaw(String column, Object value) {
        return condition(column, Operator.LIKE, value.toString());
    }

    /**
     * Match operator.
     *
     * @param column column name
     * @param value value
     * @return builder
     */
    public UriBuilder match(String column, Object value) {
        return condition(column, Operator.MATCH, value.toString());
    }

    /**
     * IN operator.
     *
     * @param column column name
     * @param values collection of values. Be aware: for big collections prefer using (selection, selectionArgs)
     * @return builder
     */
    public UriBuilder in(String column, Collection values) {
        StringBuilder value = new StringBuilder();
        for(Object v : values) {
            if(value.length() > 0) {
                value.append(DELIMITER_LIST);
            }
            checkReserved(v.toString());
            value.append(v);
        }
        return condition(column, Operator.IN, value.toString(), false);
    }

    /**
     * Between operator.
     *
     * @param column column name
     * @param min min value
     * @param max max value
     * @return builder
     */
    public UriBuilder between(String column, Object min, Object max) {
        checkReserved(min.toString());
        checkReserved(max.toString());
        String value = min.toString() + DELIMITER_LIST + max.toString();
        return condition(column, Operator.BETWEEN, value, false);
    }

    /**
     * IS NULL operator.
     *
     * @param column column name
     * @return builder
     */
    public UriBuilder isNull(String column) {
        return condition(column, Operator.IS_NULL, null);
    }

    /**
     * IS NOT NULL operator.
     *
     * @param column column name
     * @return builder
     */
    public UriBuilder isNotNull(String column) {
        return condition(column, Operator.IS_NOT_NULL, null);
    }

    /**
     * Build uri.
     *
     * @return query uri.
     */
    public Uri build() {
        if(scheme == null) {
            throw new IllegalStateException("Scheme cannot be null!");
        }
        if(authority == null) {
            throw new IllegalStateException("Authority cannot be null!");
        }
        if(table == null) {
            throw new IllegalStateException("Table cannot be null!");
        }

        return new Uri.Builder()
            .scheme(scheme)
            .authority(authority)
            .path(table)
            .appendQueryParameter(PARAM_QUERY, query.toString())
            .build();
    }

    protected UriBuilder condition(String column, Operator operator, String value) {
        return condition(column, operator, value, true);
    }

    protected UriBuilder condition(String column, Operator operator, String value, boolean checkValue) {
        checkReserved(column);
        if(value != null && checkValue) {
            checkReserved(value);
        }

        if(query.length() > 0) {
            String tmp = query.toString();
            if(!(tmp.endsWith(QUERY_AND) || tmp.endsWith(QUERY_OR))) {
                query.append(QUERY_AND);
            }
        }
        query.append(column).append(DELIMITER_QUERY).append(operator.toUri());
        if(value != null) {
            query.append(DELIMITER_QUERY).append(value);
        }
        return this;
    }

    protected void checkReserved(String str) {
        for(String reserved : RESERVED) {
            if(str.contains(reserved)) {
                throw new IllegalArgumentException(String.format("Column/Value '%s' contains reserved sequence '%s'", str, reserved));
            }
        }
    }

}
