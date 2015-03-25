package ingvar.android.literepo;

import android.net.Uri;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class UriBuilder {

    private String scheme;
    private String authority;
    private String table;
    private List<UriCondition> conditions;

    public UriBuilder() {
        conditions = new LinkedList<>();
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

    public UriCondition condition() {
        UriCondition condition = new UriCondition(this);
        conditions.add(condition);
        return condition;
    }

    public UriBuilder condition(String field, Operator operator, String value) {
        return condition().field(field).operator(operator).value(value).build();
    }

    public UriBuilder condition(String field, Operator operator, Collection values) {
        return condition().field(field).operator(operator).values(values).build();
    }

    public Uri build() {
        Uri.Builder builder = new Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(table);

        for(UriCondition condition : conditions) {
            builder.appendQueryParameter(
                condition.field + "." + condition.operator,
                condition.value
            );
        }
        return builder.build();
    }

    public static class UriCondition {

        public static final String LIST_DELIMITER = "~";

        private UriBuilder builder;
        private String field;
        private String operator;
        private String value;

        private UriCondition(UriBuilder builder) {
            this.builder = builder;
        }

        public UriCondition field(String field) {
            this.field = field;
            return this;
        }

        public UriCondition operator(Operator operator) {
            this.operator = operator.getUri();
            return this;
        }

        public UriCondition value(Object value) {
            this.value = value.toString();
            return this;
        }

        public UriCondition values(Collection values) {
            StringBuilder vb = new StringBuilder();
            for(Object v : values) {
                if(vb.length() > 0) {
                    vb.append(LIST_DELIMITER);
                }
                vb.append(v.toString());
            }
            value = vb.toString();
            return this;
        }

        public UriBuilder build() {
            return builder;
        }

        public UriCondition eq() {
            operator = "eq";
            return this;
        }

        public UriCondition gt() {
            operator = "gt";
            return this;
        }

        public UriCondition gte() {
            operator = "gte";
            return this;
        }

        public UriCondition lt() {
            operator = "lt";
            return this;
        }

        public UriCondition lte() {
            operator = "lte";
            return this;
        }

        public UriCondition like() {
            operator = "like";
            return this;
        }

        public UriCondition match() {
            operator = "match";
            return this;
        }

        public UriCondition in() {
            operator = "in";
            return this;
        }

    }

}
