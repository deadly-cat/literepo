package ingvar.android.literepo.builder;

import android.net.Uri;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Used for creation query string and args.
 * Can parse Uri created by {@link ingvar.android.literepo.builder.UriBuilder}.
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public class Query {

    private static final String[] EA = {};

    private StringBuilder selection;
    private List<String> args;

    /**
     * Default constructor
     */
    public Query() {
        this(null, null);
    }

    /**
     * Add to result query data from query arguments.
     *
     * @param selection selection string
     * @param args selection args
     */
    public Query(String selection, String[] args) {
        this.selection = new StringBuilder();
        this.args = new LinkedList<>();

        if(selection != null && !selection.isEmpty()) {
            this.selection.append(selection);
            this.args.addAll(Arrays.asList(args));
        }
    }

    /**
     *
     * @return selection string
     */
    public String getSelection() {
        return selection.toString();
    }

    /**
     *
     * @return selection args
     */
    public String[] getArgs() {
        return args.toArray(EA);
    }

    /**
     * Parse uri created by {@link ingvar.android.literepo.builder.UriBuilder}
     *
     * @param uri Query uri
     * @return this
     */
    public Query parse(Uri uri) {
        String query = uri.getQueryParameter(UriBuilder.PARAM_QUERY);
        if(query != null && !query.isEmpty()) {
            //first split by 'and'
            for (String ac : query.split(UriBuilder.QUERY_AND)) {
                if (selection.length() > 0) {
                    selection.append(" and ");
                }
                //add condition or split by 'or'
                if (ac.contains(UriBuilder.QUERY_OR)) {
                    StringBuilder or = new StringBuilder();
                    for (String oc : ac.split(UriBuilder.QUERY_OR)) {
                        if (or.length() > 0) {
                            or.append(" or ");
                        }
                        appendCondition(or, oc);
                    }
                    selection.append("(").append(or).append(")");
                } else {
                    appendCondition(selection, ac);
                }
            }
        }
        return this;
    }

    protected void appendCondition(StringBuilder builder, String raw) {
        String[] condition = raw.split(UriBuilder.DELIMITER_QUERY);
        if(condition.length < 2 || condition.length > 3) {
            throw new IllegalArgumentException("Wrong condition '" + raw + "'");
        }
        Operator operator = Operator.fromUri(condition[1]);

        builder.append(condition[0]);
        builder.append(" ").append(operator.toSql());
        if(condition.length == 3) { //contains value
            String value = condition[2];
            if(Operator.LIST_OPERATORS.contains(operator)) {
                StringBuilder list = new StringBuilder();
                for(String v : value.split(UriBuilder.DELIMITER_LIST)) {
                    if(list.length() > 0) {
                        list.append(", ");
                    }
                    list.append("?");
                    args.add(v);
                }
                builder.append(" (").append(list).append(")");
            } else {
                switch (operator) {
                    case BETWEEN:
                        String[] btv = value.split(UriBuilder.DELIMITER_LIST);
                        builder.append(" ? and ?");
                        args.add(btv[0]);
                        args.add(btv[1]);
                        break;
                    default:
                        builder.append(" ?");
                        args.add(value);
                        break;
                }
            }
        }
    }

}
