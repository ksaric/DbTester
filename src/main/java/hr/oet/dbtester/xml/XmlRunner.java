package hr.oet.dbtester.xml;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import hr.oet.dbtester.tester.script.OracleDbTesterScript;
import hr.oet.dbtester.tester.strategy.SingleJdbcRunnerStrategy;
import org.apache.commons.dbcp.BasicDataSource;

import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class XmlRunner {

    private DbTest dbTest;
    private TestResults testResults;

    public XmlRunner() {
    }

    public void setDbTest( DbTest dbTest ) {
        this.dbTest = dbTest;
    }

    public void setTestResults( TestResults testResults ) {
        this.testResults = testResults;
    }

    public void runTests() {
        // read the config
        final DbTestConnection dbTestConnection = this.dbTest.getDbTestConnection();
        final List<DbUnitTest> tests = this.dbTest.getTests();

        final String dbTestConnectionType = dbTestConnection.getType();

        /*if ( dbTestConnection.equals( DbType.ORACLE.name() ) ) {
            // oracle
        } else if ( dbTestConnection.equals( DbType.MYSQL.name() ) ) {
            // todo: mysql
        }*/

        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName( dbTestConnection.getDriver() );
        dataSource.setUrl( dbTestConnection.getUrl() );
        dataSource.setUsername( dbTestConnection.getUsername() );
        dataSource.setPassword( dbTestConnection.getPassword() );
        // settings
        dataSource.setMaxActive( 100 );
        dataSource.setMaxWait( 10000 );
        dataSource.setMaxIdle( 10 );

        OracleDbTesterScript dbTesterScript = new OracleDbTesterScript( dataSource, new SingleJdbcRunnerStrategy() );

        for ( final DbUnitTest test : tests ) {
            final String testName = test.getName();

            try {
                dbTesterScript.cleanup( test.getCleanupBefore() );
                dbTesterScript.input( test.getUpscript() );

                // run the scripts
                if ( !Strings.isNullOrEmpty( test.getTestSetup() ) ) {
                    final Iterable<String> scripts = Splitter.on( ";" )
                            .trimResults()
                            .omitEmptyStrings()
                            .split( test.getTestSetup() );

                    for ( final String script : scripts ) {
                        dbTesterScript.inputDML( script );
                    }
                }
                //When
                final String procedureCall = String.format( "{call %s()}", test.getProcedure() );
                dbTesterScript.function( procedureCall );

                // verify results
                boolean isPassed = true;

                for ( final TestVerification testVerification : test.getVerifications() ) {
                    final String rezultat = dbTesterScript.output( testVerification.getSql() );

                    if ( !rezultat.equals( testVerification.getResult() ) )
                        isPassed = false;

                }

                if ( isPassed ) {
                    testResults.addPassedTest( testName, null );
                } else {
                    testResults.addFailedTest( testName, null, null );
                }

                dbTesterScript.cleanup( test.getCleanupAfter() );
            } catch ( Exception e ) {
                e.printStackTrace();
                testResults.addFailedTest( testName, null, null );
            }
        }
    }

    public TestResults getTestResults() {
        return testResults;
    }
}
