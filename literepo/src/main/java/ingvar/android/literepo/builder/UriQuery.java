package ingvar.android.literepo.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Igor Zubenko on 2015.05.06.
 */
public class UriQuery {

    /**
     * Used for setting value as is without any replacing
     */
    public static class SqlValue {

        private String sql;

        public SqlValue(String sql) {
            this.sql = sql;
        }

        @Override
        public String toString() {
            return sql;
        }

    }

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
     * Indicates SQL query in value
     */
    public static final String VALUE_SQL = "~s";

    /**
     * Reserved sequences what cannot be used in the query(fields names or values)
     */
    public static final List<String> RESERVED = Collections.unmodifiableList(Arrays.asList(DELIMITER_LIST, DELIMITER_QUERY, QUERY_AND, QUERY_OR, VALUE_SQL));

    private UriBuilder builder;
    private StringBuilder query;

    public UriQuery(UriBuilder builder) {
        this.builder = builder;
        this.query = new StringBuilder();
    }

    /**
     * Return builder's reference.
     *
     * @return uri builder
     */
    public UriBuilder end() {
        return builder;
    }

    /**
     * Create query string for adding to {@link android.net.Uri}.
     *
     * @return query string
     */
    public String createQuery() {
        return query.toString();
    }

    /**
     * Check if query is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return query.length() == 0;
    }

    /**
     * Added 'OR' between prev and next conditions instead of 'AND'.
     *
     * @return query
     */
    public UriQuery or() {
        query.append(QUERY_OR);
        return this;
    }

    /**
     * Equals operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery eq(String column, Object value) {
        return condition(column, Operator.EQUALS, value);
    }

    /**
     * Greater than operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery gt(String column, Object value) {
        return condition(column, Operator.GREATER_THAN, value);
    }

    /**
     * Greater than or equals operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery gte(String column, Object value) {
        return condition(column, Operator.GREATER_THAN_OR_EQUALS, value);
    }

    /**
     * Lower than operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery lt(String column, Object value) {
        return condition(column, Operator.LOWER_THAN, value);
    }

    /**
     * Lower than or equals operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery lte(String column, Object value) {
        return condition(column, Operator.LOWER_THAN_OR_EQUALS, value);
    }

    /**
     * Like operator.
     * Wraps value to %value%.
     * Note: For {@link ingvar.android.literepo.builder.UriQuery.SqlValue} use {@link UriQuery#likeRaw(String, Object)}.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery like(String column, Object value) {
        return condition(column, Operator.LIKE, "%" + value.toString() + "%");
    }

    /**
     * Like operator.
     * Does not wrap value.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery likeRaw(String column, Object value) {
        return condition(column, Operator.LIKE, value);
    }

    /**
     * Match operator.
     *
     * @param column column name
     * @param value value
     * @return query
     */
    public UriQuery match(String column, Object value) {
        return condition(column, Operator.MATCH, value);
    }

    /**
     * IN operator.
     *
     * @param column column name
     * @param values collection of values. Be aware: for big collections prefer using (selection, selectionArgs)
     * @return query
     */
    public UriQuery in(String column, Collection values) {
        StringBuilder value = new StringBuilder();
        for(Object v : values) {
            if(value.length() > 0) {
                value.append(DELIMITER_LIST);
            }
            checkReserved(v.toString());
            value.append(v);
        }
        return condition(column, Operator.IN, value, false);
    }

    /**
     * IN operator.
     *
     * @param column column name
     * @param value raw sql query
     * @return query
     */
    public UriQuery in(String column, SqlValue value) {
        return condition(column, Operator.IN, value, true);
    }

    /**
     * Between operator.
     * Note: {@link ingvar.android.literepo.builder.UriQuery.SqlValue} does not work.
     *
     * @param column column name
     * @param min min value
     * @param max max value
     * @return query
     */
    public UriQuery between(String column, Object min, Object max) {
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
    public UriQuery isNull(String column) {
        return condition(column, Operator.IS_NULL, null);
    }

    /**
     * IS NOT NULL operator.
     *
     * @param column column name
     * @return query
     */
    public UriQuery isNotNull(String column) {
        return condition(column, Operator.IS_NOT_NULL, null);
    }

    /**
     * Append raw sql to query.
     *
     * @param rawSql sql query
     * @return query
     */
    public UriQuery raw(String rawSql) {
        if(query.length() > 0) {
            String tmp = query.toString();
            if(!(tmp.endsWith(QUERY_AND) || tmp.endsWith(QUERY_OR))) {
                query.append(QUERY_AND);
            }
        }
        query.append(VALUE_SQL).append(rawSql);
        return this;
    }

    protected UriQuery condition(String column, Operator operator, Object value) {
        return condition(column, operator, value, true);
    }

    protected UriQuery condition(String column, Operator operator, Object value, boolean checkValue) {
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
            query.append(DELIMITER_QUERY);
            if(value instanceof SqlValue) {
                query.append(VALUE_SQL);
            }
            query.append(value);
        }
        return this;
    }

    protected void checkReserved(Object value) {
        String str = value.toString();
        for(String reserved : RESERVED) {
            if(str.contains(reserved)) {
                throw new IllegalArgumentException(String.format("Column/Value '%s' contains reserved sequence '%s'", str, reserved));
            }
        }
    }

}
