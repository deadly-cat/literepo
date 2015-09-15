package ingvar.android.literepo.builder;

/**
 * Created by Igor Zubenko on 2015.05.11.
 */
public enum JoinType {

    INNER("", ""),
    LEFT("left", "~lj"),
    CROSS("cross", "~cj");

    public final String sql;
    public final String uri;

    public static JoinType fromUri(String uri) {
        for(JoinType type : values()) {
            if(!INNER.equals(type) && uri.startsWith(type.uri)) {
                return type;
            }
        }
        return INNER;
    }

    JoinType(String sql, String uri) {
        this.sql = sql;
        this.uri = uri;
    }

}
