package ingvar.android.literepo.builder;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used for creation query string and args.
 * Can parse Uri created by {@link ingvar.android.literepo.builder.UriBuilder}.
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public class SqlBuilder {

    private static final String[] EA = {};

    private StringBuilder from;
    private StringBuilder selection;
    private List<String> args;

    public SqlBuilder(Uri uri) {
        this(uri, null, null);
    }

    public SqlBuilder(String selection, String[] args) {
        this(null, selection, args);
    }

    /**
     * Add to result query data from query arguments.
     *
     * @param uri query uri
     * @param selection selection string
     * @param args selection args
     */
    public SqlBuilder(Uri uri, String selection, String[] args) {
        this.selection = new StringBuilder();
        this.args = new ArrayList<>();
        this.from = new StringBuilder();

        if(uri != null) {
            parse(uri);
        }
        if(selection != null && !selection.isEmpty()) {
            appendSelection(selection, args);
        }
    }

    /**
     * Get from statement.
     *
     * @return from string
     */
    public String getFrom() {
        return from.toString();
    }

    /**
     * Get where statement.
     *
     * @return selection string
     */
    public String getSelection() {
        return selection.toString();
    }

    /**
     * Get args for replacing ?s.
     *
     * @return selection args
     */
    public String[] getArgs() {
        return args.toArray(EA);
    }

    /**
     * Parse uri created by {@link ingvar.android.literepo.builder.UriBuilder}.
     * If uri path equals to {@link UriBuilder#PATH} when parsed as query uri.
     * Otherwise only 'from' statement will be added, where path used as table name.
     *
     * @param uri Query uri
     * @return this
     */
    protected SqlBuilder parse(Uri uri) {
        String path = uri.getPath().replaceAll("/", "");

        if(UriBuilder.PATH.equals(path)) {
            String tableAlias = parseTable(from, uri.getQueryParameter(UriBuilder.PARAM_TABLE + "0"));

            int tables = Integer.valueOf(uri.getQueryParameter(UriBuilder.PARAM_TABLE_COUNT));
            if(tables > 1) {
                for(int i = 1; i < tables; i++) {
                    from.append(" join ");
                    String joinAlias = parseTable(from, uri.getQueryParameter(UriBuilder.PARAM_TABLE + Integer.toString(i)));
                    String query = uri.getQueryParameter(UriBuilder.PARAM_QUERY + Integer.toString(i));
                    if(query != null && !query.isEmpty()) {
                        from.append(" on (");
                        parseQuery(from, joinAlias, query);
                        from.append(")");
                    }
                }
            }
            parseQuery(selection, tableAlias, uri.getQueryParameter(UriBuilder.PARAM_QUERY + "0"));

        } else {
            from = new StringBuilder(path);
        }
        return this;
    }

    /**
     * Append params from {@link android.content.ContentProvider#query(Uri, String[], String, String[], String)}
     * @param selection selection
     * @param args selection args
     * @return this
     */
    protected SqlBuilder appendSelection(String selection, String... args) {
        if(this.selection.length() > 0) {
            this.selection.append(" and ");
        }
        this.selection.append(selection);
        this.args.addAll(Arrays.asList(args));
        return this;
    }

    /**
     * Parse table name and alias from uri query parameter
     *
     * @param builder builder
     * @param rawTable uri query value. See {@link UriBuilder#createTableQuery(Uri.Builder, int, UriBuilder.Table)}
     * @return table alias
     */
    protected String parseTable(StringBuilder builder, String rawTable) {
        int ts = rawTable.indexOf(".");
        String name = rawTable;
        String alias = null;
        if(ts != -1) {
            name = rawTable.substring(0, ts);
            alias = rawTable.substring(ts+1, rawTable.length());
        }
        builder.append(name);
        if(alias != null && !alias.isEmpty()) {
            builder.append(" as ").append(alias);
        }
        return alias;
    }

    /**
     * Parse query from uri query parameter and append alias to columns if it needed.
     *
     * @param builder builder
     * @param tableAlias table alias
     * @param rawQuery uri query value See {@link UriBuilder#createTableQuery(Uri.Builder, int, UriBuilder.Table)}
     */
    protected void parseQuery(StringBuilder builder, String tableAlias, String rawQuery) {
        if(rawQuery == null || rawQuery.isEmpty()) {
            return;
        }
        //first split by 'and'
        String[] split = rawQuery.split(UriQuery.QUERY_AND);
        for (int i = 0; i < split.length; i++) {
            String ac = split[i];
            if (i > 0) {
                builder.append(" and ");
            }
            //add condition or split by 'or'
            if (ac.contains(UriQuery.QUERY_OR)) {
                StringBuilder or = new StringBuilder();
                for (String oc : ac.split(UriQuery.QUERY_OR)) {
                    if (or.length() > 0) {
                        or.append(" or ");
                    }
                    appendCondition(or, tableAlias, oc);
                }
                builder.append("(").append(or).append(")");
            } else {
                appendCondition(builder, tableAlias, ac);
            }
        }
    }

    /**
     * Append condition to sql query ('where' or 'join' statement).
     *
     * @param builder builder
     * @param alias table alias
     * @param raw string from uri
     */
    protected void appendCondition(StringBuilder builder, String alias, String raw) {
        if(raw.startsWith(UriQuery.VALUE_SQL)) {
            builder.append(Uri.decode(raw.substring(UriQuery.VALUE_SQL.length())));

        } else {
            String[] condition = raw.split(UriQuery.DELIMITER_QUERY);
            if (condition.length < 2 || condition.length > 3) {
                throw new IllegalArgumentException("Wrong condition '" + raw + "'");
            }
            //empty string
            if (condition.length == 2 && raw.endsWith(UriQuery.DELIMITER_QUERY)) {
                String[] tmp = new String[3];
                System.arraycopy(condition, 0, tmp, 0, condition.length);
                tmp[2] = "";

                condition = tmp;
            }
            Operator operator = Operator.fromUri(condition[1]);

            if (alias != null && !alias.isEmpty()) {
                builder.append(alias).append(".");
            }
            builder.append(condition[0]);
            builder.append(" ").append(operator.toSql());
            if (condition.length == 3) { //contains value
                String value = condition[2];

                if (value.startsWith(UriQuery.VALUE_SQL)) {
                    value = Uri.decode(value.substring(UriQuery.VALUE_SQL.length()));
                    if(Operator.LIST_OPERATORS.contains(operator)) {
                        builder.append(" (").append(value).append(")");
                    } else {
                        builder.append(' ').append(value);
                    }

                } else if (Operator.LIST_OPERATORS.contains(operator)) {
                    StringBuilder list = new StringBuilder();
                    for (String v : value.split(UriQuery.DELIMITER_LIST)) {
                        if (list.length() > 0) {
                            list.append(", ");
                        }
                        list.append("?");
                        args.add(v);
                    }
                    builder.append(" (").append(list).append(")");

                } else {
                    switch (operator) {
                        case BETWEEN:
                            String[] btv = value.split(UriQuery.DELIMITER_LIST);
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

}
