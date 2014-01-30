package hr.oet.dbtester.tester.list;

import hr.oet.dbtester.ParametersProvider;
import hr.oet.dbtester.ResultProvider;
import hr.oet.dbtester.datasource.DataSourceFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * User: ksaric
 */
public class OracleDbTester extends DbTester {

    protected OracleDbTester( DataSourceFactory dataSourceFactory ) {
        super( dataSourceFactory );
    }

    public void init() {
        // prepare the required settings
        prepareDataSourceQueryRunner();
    }

    @Override
    public <T extends List<String>> void input( ParametersProvider<T> upScripts ) {
        // get the test upScripts!
        final List<String> dmlStatements = upScripts.get();

        runDMLMultiple( queryRunner, dmlStatements );
    }

    @Override
    public Boolean function( final String statement, final Object... params ) {
        CallableStatement callableStatement = getCallableStatement( statement );

        try {
            queryRunner.fillStatement( callableStatement, params );
            callableStatement.executeUpdate();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public <T, L extends List<String>> ResultProvider<List<T>> output( final ParametersProvider<L> selectScripts, final Class<T> type, final Objects... params ) {
        // get the test upScripts!
        final List<String> dmlStatements = selectScripts.get();

        final List<T> artikli = runSQLMultiple( queryRunner, dmlStatements, type, params );

        return new ResultProvider<List<T>>() {
            @Override
            public List<T> get() {
                return artikli;
            }
        };
    }

    @Override
    public <K, L extends List<String>> ResultProvider<List<K>> listOutput( ParametersProvider<L> selectScripts, Class<K> type, Objects... params ) {
        final List<String> dmlStatements = selectScripts.get();

        final List<K> artikli = runSQLMultiple( queryRunner, dmlStatements, type, params );

        return new ResultProvider<List<K>>() {
            @Override
            public List<K> get() {
                return artikli;
            }
        };
    }

    @Override
    public Boolean cleanup( List<String> downScripts ) {
        final List<String> dmlStatements = downScripts;

        runDMLMultiple( queryRunner, dmlStatements );

        return true;
    }
}
