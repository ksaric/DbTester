package hr.oet.dbtester.tester.script;

import hr.oet.dbtester.tester.strategy.SingleJdbcRunnerStrategy;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class OracleDbTesterScriptTest {

    public static final String PROCEDURE_IZRACUN = "SKLADISTE_BATCH.izracun";
    public static final String PROCEDURE_CALL = String.format( "{call %s()}", PROCEDURE_IZRACUN );

    private DbTesterScript dbTesterScript;

    @Before
    public void setUp() throws Exception {
//        OracleDataSourceFactory dataSourceFactory = OracleDataSourceFactory.createDefaultConnection();
        final BasicDataSource dataSource = new BasicDataSource();
        dbTesterScript = new OracleDbTesterScript( dataSource, new SingleJdbcRunnerStrategy() );
    }

    @Test
    public void testProcedure() throws Exception {
        //Before
        dbTesterScript.cleanup( "down_skripta.sql" );
        dbTesterScript.input( "up_skripta.sql" );

        //When
        dbTesterScript.function( PROCEDURE_CALL );
        String kolicina = dbTesterScript.output( "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1" );

        dbTesterScript.cleanup( "down_skripta.sql" );

        //Then
        System.out.println( "---" + kolicina );
        Assert.assertNotNull( kolicina );
    }
}
