package hr.oet.dbtester.datasource;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class OracleDataSourceFactory implements DataSourceFactory {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public void setDriverClassName( String driverClassName ) {
        this.driverClassName = driverClassName;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public OracleDataSourceFactory() {
    }

    @Override
    public Boolean checkForDriver() {
        try {
            Class.forName( driverClassName );
            return true;
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public OracleDataSource getDataSourceUnchecked() {
        OracleDataSource dataSource = null;

        try {
            dataSource = getDataSource();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return dataSource;
    }

    // todo IoC !?!
    @Override
    public OracleDataSource getDataSource() throws SQLException {

        OracleConnectionPoolDataSource oracleDataSource = new OracleConnectionPoolDataSource();
//        url = "jdbc:oracle:thin:@localhost:1521:orcl";
//        username = "projekt";
//        password = "lozinka";

//        oracleDataSource.setDriverType( "thin" );
//        oracleDataSource.setServerName( "localhost" );
//        oracleDataSource.setPortNumber( 1521 );
//        oracleDataSource.setDatabaseName( "orcl" ); // sid

        oracleDataSource.setURL( url );
        oracleDataSource.setUser( username );
        oracleDataSource.setPassword( password );

        // first check
        checkForDriver();

        return oracleDataSource;
    }

    public static OracleDataSourceFactory createDefaultConnection() {
        final OracleDataSourceFactory oracleDataSourceFactory = new OracleDataSourceFactory();
        oracleDataSourceFactory.setDriverClassName( "oracle.jdbc.OracleDriver" );
        oracleDataSourceFactory.setUsername( "faksprojekt" );
        oracleDataSourceFactory.setPassword( "faks" );
        oracleDataSourceFactory.setUrl( "jdbc:oracle:thin:@localhost:1521:orcl" );

        return oracleDataSourceFactory;
    }
}
