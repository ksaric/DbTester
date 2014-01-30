package hr.oet.dbtester.tester.strategy;

import com.google.common.collect.*;
import hr.oet.dbtester.JdbcRunnerStrategy;
import hr.oet.dbtester.datasource.DataSourceFactory;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */

public class SingleJdbcRunnerStrategy implements JdbcRunnerStrategy {

    private DataSourceFactory dataSourceFactory;
    private QueryRunner queryRunner;

    public <T> List<T> runSQLMultiple( final QueryRunner run, final List<String> sql, Class<T> aClass, final Object... params ) {

        List<T> list = Lists.newArrayList();
        ResultSetHandler<List<T>> resultSetHandler = new BeanListHandler<>( aClass );

        try {
            for ( String currentDml : sql ) {
                List<T> query = run.query( currentDml, resultSetHandler, params );
                list.addAll( query );
            }

            return list;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new RuntimeException( "FATAL!" );
    }

    protected int runDMLMultiple( final QueryRunner run, final List<String> dml ) {
        try {
            int totalModified = 0;

            for ( String currentDml : dml ) {
                totalModified += run.update( currentDml );
            }

            return totalModified;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new RuntimeException( "FATAL!" );
    }

    protected int runDMLSingle( final QueryRunner run, final String dml ) {
        try {
            return run.update( dml );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new RuntimeException( "FATAL!" );
    }

    protected void prepareDataSourceQueryRunner() {
        dataSourceFactory.checkForDriver();

        final OracleDataSource dataSource = dataSourceFactory.getDataSourceUnchecked();

        queryRunner = new QueryRunner( dataSource );
    }

    protected PreparedStatement getPreparedStatement( final String statement ) {
        try {
            return queryRunner.getDataSource().getConnection().prepareStatement( statement );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new RuntimeException( "FATAL!" );
    }

    protected CallableStatement getCallableStatement( final String statement ) {
        try {
            return queryRunner.getDataSource().getConnection().prepareCall( statement );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new RuntimeException( "FATAL!" );
    }

}
