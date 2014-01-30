package hr.oet.dbtester.xml;

import com.google.common.io.Resources;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class XmlReaderTest {

    @Test
    public void testSimple() throws Exception {
        //Before
        XmlReader xmlReader = new XmlReader();

        //When
        DbTest read = xmlReader.read( new File( Resources.getResource( "test_configuration.xml" ).getFile() ) );

        //Then
        DbTestConnection dbTestConnection = read.getDbTestConnection();
        List<DbUnitTest> tests = read.getTests();
        final DbUnitTest dbUnitTest = tests.get( 0 );

        Assert.assertThat( dbTestConnection.getName(), Matchers.equalTo( "simple_connection" ) );
        Assert.assertThat( dbTestConnection.getType(), Matchers.equalTo( "ORACLE" ) );

        Assert.assertThat( dbTestConnection.getUsername(), Matchers.equalTo( "projekt" ) );
        Assert.assertThat( dbTestConnection.getPassword(), Matchers.equalTo( "lozinka" ) );

        Assert.assertThat( dbUnitTest.getName(), Matchers.equalTo( "test_simple1" ) );

        Assert.assertThat( read, Matchers.notNullValue() );

    }
}
