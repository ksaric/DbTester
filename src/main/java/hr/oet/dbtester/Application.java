package hr.oet.dbtester;

import hr.oet.dbtester.tester.script.ResourcesLoader;
import hr.oet.dbtester.xml.DbTest;
import hr.oet.dbtester.xml.TestResults;
import hr.oet.dbtester.xml.XmlReader;
import hr.oet.dbtester.xml.XmlRunner;

import java.io.File;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class Application {
    public static void main( String[] args ) {
        // config name
        if ( args.length != 1 ) {
            System.out.println( "Only 1 parameter required, configuration name..." );
            return;
        }

        final String configurationName = args[ 0 ];
        final String xmlConfig = ResourcesLoader.loadScriptFromJarLocation( configurationName );
        final File fileConfig = new File( xmlConfig );

        XmlReader xmlReader = new XmlReader();
        final DbTest dbTest = xmlReader.read( fileConfig );

        XmlRunner xmlRunner = new XmlRunner();
        xmlRunner.setDbTest( dbTest );
        xmlRunner.setTestResults( new TestResults() );

        // finally, run them
        xmlRunner.runTests();

        // print results
        final TestResults testResults = xmlRunner.getTestResults();

        System.out.println( "*****************************************************" );
        System.out.println( "FAILED TESTS" );
        System.out.println( "*****************************************************" );

        for ( final String currentFailedTest : testResults.getFailedTests() ) {
            System.out.println( "   ->      " + currentFailedTest );
        }

        System.out.println( "*****************************************************" );
        System.out.println( "*****************************************************" );
        System.out.println( "PASSED TESTS" );
        System.out.println( "*****************************************************" );

        for ( final String currentPassedTest : testResults.getPassedTests() ) {
            System.out.println( "   ->      " + currentPassedTest );
        }

        System.out.println( "*****************************************************" );
    }
}
