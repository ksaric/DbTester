package hr.oet.dbtester.tester.script;

import hr.oet.dbtester.JdbcRunnerStrategy;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;

/**
 * User: ksaric
 */

abstract class DbTesterScript {

    private final DataSource dataSource;
    private final JdbcRunnerStrategy jdbcRunnerStrategy;

    protected final QueryRunner queryRunner;

    protected DbTesterScript(
            final DataSource dataSource,
            final JdbcRunnerStrategy jdbcRunnerStrategy
    ) {
        this.dataSource = dataSource;
        this.jdbcRunnerStrategy = jdbcRunnerStrategy;

        // query runner
        this.queryRunner = new QueryRunner( dataSource );
    }

    // check if script is done
    public abstract Boolean inputDML( final String dmlCommand );

    public abstract Boolean input( final String scriptName );

    public abstract Boolean function( final String statement, final Object... params );

    public abstract <T> T output( final String sqlSelect, final Class<T> type );

    public abstract String output( final String sqlSelect );

    public abstract Boolean cleanup( final String scriptName );
}
