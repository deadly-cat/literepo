package ingvar.android.literepo.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Operators constants.
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public enum Operator {

    EQUALS("eq", "="),
    GREATER_THAN("gt", ">"),
    GREATER_THAN_OR_EQUALS("gte", ">="),
    LOWER_THAN("lt", "<"),
    LOWER_THAN_OR_EQUALS("lte", "<="),
    LIKE("lk", "like"),
    MATCH("mh", "match"),
    IN("in", "in"),
    NOT_IN("nin", "not in"),
    BETWEEN("bn", "between"),
    IS_NULL("isn", "is null"),
    IS_NOT_NULL("isnn", "is not null");

    /**
     * Collection of list operators. Like as 'IN'
     */
    public static final List<Operator> LIST_OPERATORS = Collections.unmodifiableList(Arrays.asList(IN));

    /**
     * Get operator by Uri representation.
     *
     * @param operator uri operator
     * @return {@link ingvar.android.literepo.builder.Operator}
     */
    public static Operator fromUri(String operator) {
        for(Operator o : values()) {
            if(o.toUri().equals(operator)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    /**
     * Get operator by SQL representation.
     *
     * @param operator sql operator
     * @return {@link ingvar.android.literepo.builder.Operator}
     */
    public static Operator fromSql(String operator) {
        for(Operator o : values()) {
            if(o.toSql().equals(operator)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    private String uri;
    private String sql;

    Operator(String uri, String sql) {
        this.uri = uri;
        this.sql = sql;
    }

    /**
     * Get operator for Uri.
     *
     * @return operator
     */
    public String toUri() {
        return uri;
    }

    /**
     * Get operator for SQL.
     *
     * @return operator
     */
    public String toSql() {
        return sql;
    }

}
