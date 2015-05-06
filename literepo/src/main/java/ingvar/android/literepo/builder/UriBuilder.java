package ingvar.android.literepo.builder;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple query builder. Parsed by {@link SqlBuilder}
 *
 * Created by Igor Zubenko on 2015.03.25.
 */
public class UriBuilder {

    public static final String PATH = "generated";
    public static final String PARAM_TABLE = "t";
    public static final String PARAM_QUERY = "q";
    public static final String PARAM_TABLE_COUNT = "tc";

    private String scheme;
    private String authority;
    private Table table;
    private List<Table> join;

    public UriBuilder() {
        scheme = "content"; //default
        join = new ArrayList<>(1);
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
     * Set table name.
     *
     * @param name table name
     * @param alias table alias or null
     * @return builder
     */
    public UriBuilder table(String name, String alias) {
        if(table == null) {
            table = new Table();
        }
        table.name = name;
        table.alias = alias;
        return this;
    }

    /**
     * See {@link UriBuilder#table(String, String)}
     *
     * @param name table name
     * @return builder
     */
    public UriBuilder table(String name) {
        return table(name, null);
    }

    /**
     * Create query for table.
     * For return instance of {@link UriBuilder} call {@link UriQuery#end()} after setting params.
     *
     * @return query instance
     */
    public UriQuery query() {
        if(table == null) {
            table = new Table();
        }
        return table.query = new UriQuery(this);
    }

    /**
     * Add join.
     *
     * @param name table name
     * @param alias table alias
     * @return join query
     */
    public UriQuery join(String name, String alias) {
        Table table = new Table(name, alias);
        join.add(table);
        return table.query = new UriQuery(this);
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
        Uri.Builder builder = new Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(PATH);
        builder.appendQueryParameter(PARAM_TABLE_COUNT, Integer.toString(join.size() + 1));
        int index = 0;
        createTableQuery(builder, index++, table);
        for(Table j : join) {
            createTableQuery(builder, index++, j);
        }
        return builder.build();
    }

    protected void createTableQuery(Uri.Builder builder, int index, Table table) {
        String tableQuery = table.name;
        if(table.alias != null && !table.alias.isEmpty()) {
            tableQuery += "." + table.alias;
        }
        builder.appendQueryParameter(PARAM_TABLE + index, tableQuery);
        if(table.query != null && !table.query.isEmpty()) {
            builder.appendQueryParameter(PARAM_QUERY + index, table.query.createQuery());
        }
    }

    protected class Table {

        private String name;
        private String alias;
        private UriQuery query;

        public Table() {}

        public Table(String name, String alias) {
            this.name = name;
            this.alias = alias;
        }

    }

}
