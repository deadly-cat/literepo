package ingvar.android.literepo;

import android.net.Uri;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Igor Zubenko on 2015.03.25.
 */
public class Query {

    private static final String[] EA = {};

    private StringBuilder selection;
    private List<String> args;

    public Query(String selection, String[] args) {
        this.selection = new StringBuilder();
        this.args = new LinkedList<>();

        if(selection != null && !selection.isEmpty()) {
            this.selection.append(selection);
            this.args.addAll(Arrays.asList(args));
        }
    }

    public String getSelection() {
        return selection.toString();
    }

    public String[] getArgs() {
        return args.toArray(EA);
    }

    public Query parse(Uri uri) {
        for(String param : uri.getQueryParameterNames()) {
            if(selection.length() > 0) {
                selection.append(" and ");
            }
            int od = param.lastIndexOf(".");
            Operator op = Operator.fromUri(param.substring(od+1, param.length()));
            String field = param.substring(0, od);
            String operator = op.getSql();
            String value = uri.getQueryParameter(param);

            selection.append(field).append(" ").append(operator).append(" ");
            if(Operator.LIST_OPERATORS.contains(op)) {
                StringBuilder ivs = new StringBuilder();
                for(String v : value.split(UriBuilder.UriCondition.LIST_DELIMITER)) {
                    if(ivs.length() > 0) {
                        ivs.append(", ");
                    }
                    ivs.append("?");
                    args.add(v);
                }
                selection.append("(").append(ivs.toString()).append(")");
            } else {
                selection.append("?");
                args.add(value);
            }
        }
        return this;
    }

}
