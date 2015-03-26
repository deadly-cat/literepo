package ingvar.android.literepo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public enum Operator {

    EQUALS("eq", "="),
    GREATER_THAN("gt", ">"),
    GREATER_THAN_OR_EQUALS("gte", ">="),
    LOWER_THAN("lt", "<"),
    LOWER_THAN_OR_EQUALS("lte", "<="),
    LIKE("like", "like"),
    MATCH("match", "match"),
    IN("in", "in"),
    BETWEEN("bn", "between"),
    IS_NULL("isn", "is null"),
    IS_NOT_NULL("isnn", "is not null");

    public static final List<Operator> LIST_OPERATORS = Collections.unmodifiableList(Arrays.asList(IN));

    public static Operator fromUri(String operator) {
        for(Operator o : values()) {
            if(o.toUri().equals(operator)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

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

    public String toUri() {
        return uri;
    }

    public String toSql() {
        return sql;
    }

}
