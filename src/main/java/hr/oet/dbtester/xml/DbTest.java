package hr.oet.dbtester.xml;

import com.google.common.base.Objects;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */

@Root(strict = true)
public class DbTest {

    @Element(name = "connection")
    private DbTestConnection dbTestConnection;

    @ElementList
    private List<DbUnitTest> tests;

    public DbTestConnection getDbTestConnection() {
        return dbTestConnection;
    }

    public void setDbTestConnection( DbTestConnection dbTestConnection ) {
        this.dbTestConnection = dbTestConnection;
    }

    public List<DbUnitTest> getTests() {
        return tests;
    }

    public void setTests( List<DbUnitTest> tests ) {
        this.tests = tests;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this )
                .add( "dbTestConnection", dbTestConnection )
                .add( "tests", tests )
                .toString();
    }
}
