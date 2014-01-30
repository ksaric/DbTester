package hr.oet.dbtester.datasource;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public interface DataSourceFactory {
    Boolean checkForDriver();

    OracleDataSource getDataSourceUnchecked();

    OracleDataSource getDataSource() throws SQLException;
}
