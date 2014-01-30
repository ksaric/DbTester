package hr.oet.dbtester.xml;

import com.google.common.base.Objects;
import org.simpleframework.xml.Element;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */

public class TestVerification {

    @Element
    private String sql;

    @Element
    private String result;

    public String getSql() {
        return sql;
    }

    public void setSql( String sql ) {
        this.sql = sql;
    }

    public String getResult() {
        return result;
    }

    public void setResult( String result ) {
        this.result = result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this )
                .add( "sql", sql )
                .add( "result", result )
                .toString();
    }
}
