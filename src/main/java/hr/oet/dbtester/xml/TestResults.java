package hr.oet.dbtester.xml;

import com.google.common.collect.*;

import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class TestResults {

    private List<String> passedTests = Lists.newArrayList();
    private List<String> failedTests = Lists.newArrayList();

    public void addPassedTest( final String testName, final String rezultat ) {
        passedTests.add( testName );
    }

    public void addFailedTest( final String testName, final String result, final String rezultat ) {
        failedTests.add( testName );
    }

    public List<String> getPassedTests() {
        return passedTests;
    }

    public List<String> getFailedTests() {
        return failedTests;
    }
}
