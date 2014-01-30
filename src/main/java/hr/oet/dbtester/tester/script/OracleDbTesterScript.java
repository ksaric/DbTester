package hr.oet.dbtester.tester.script;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Resources;
import hr.oet.dbtester.JdbcRunnerStrategy;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class OracleDbTesterScript extends DbTesterScript {

    public OracleDbTesterScript(
            final DataSource dataSourceFactory,
            final JdbcRunnerStrategy jdbcRunnerStrategy
    ) {
        super( dataSourceFactory, jdbcRunnerStrategy );
    }

    // todo: there is just one !?!
    public <T> T runSqlQueryResultType( final Class<T> klazz, final String querySql ) {
        checkNotNull( klazz );
        checkNotNull( querySql );

        Connection connectionFromQueryRunner = null;

        try {
            connectionFromQueryRunner = queryRunner.getDataSource().getConnection();

            final ResultSet resultSet = connectionFromQueryRunner.createStatement().executeQuery( querySql );

            // single row!
            if ( resultSet.next() ) {
                return (T) resultSet.getObject( 1 );
                //                return (T) resultSet.getObject( 1, klazz ); // todo : fucking idiot
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            // todo I guess!
            /*try {
                connectionFromQueryRunner.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }*/
        }

        throw new IllegalStateException( "Query failed. Raise exception!" );
    }

    public Boolean callProcedure( final String statement, final Object... params ) {

        try {
            // fetch the connection
            final Connection connectionFromQueryRunner = queryRunner.getDataSource().getConnection();
            // reate the callable statement
            CallableStatement callableStatement = connectionFromQueryRunner.prepareCall( statement );
            // run the statement
            queryRunner.fillStatement( callableStatement, params );
            // execute it
            callableStatement.executeUpdate();
            // return true, executed
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return false;
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

    private List<String> convertLocationToDmlStatements( String scriptName ) {
        List<String> dmlList = Lists.newArrayList();

        // todo IoC ?!?
        final String scriptFromJarLocation = ResourcesLoader.loadScriptFromJarLocation( scriptName );

        final File file = new File( scriptFromJarLocation );
        if ( file.exists() ) {
            // convert to URL
            URL url = null;

            try {
                url = file.toURI().toURL();
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            }

            // read from URL
            String fileContent = "";

            try {
                fileContent = Resources.toString( url, Charsets.UTF_8 );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            // todo: ANTLR : PL/SQL parser, this is error-prone!
            Iterable<String> scripts = Splitter.on( ";" )
                    .omitEmptyStrings()
                    .trimResults()
                    .split( fileContent );

            for ( final String script : scripts ) {
                // add the script without the semicolon, since the semicolon is forbiedden in JDBC statements
                dmlList.add( script );
            }
        }

        return dmlList;
    }

    @Override
    public Boolean inputDML( final String dmlCommand ) {
        int runDMLMultiple = runDMLMultiple( queryRunner, Lists.newArrayList( dmlCommand ) );

        // the cleanup is run if there are updated rows?
        return runDMLMultiple > 0;
    }

    @Override
    public Boolean input( final String scriptName ) {
        final List<String> dmlList = convertLocationToDmlStatements( scriptName );

        int runDMLMultiple = runDMLMultiple( queryRunner, dmlList );

        // the cleanup is run if there are updated rows?
        return runDMLMultiple > 0;
    }

    @Override
    public Boolean function( final String statement, final Object... params ) {
        return callProcedure( statement, params );
    }

    @Override
    public <T> T output( final String sqlSelect, final Class<T> type ) {
        return runSqlQueryResultType( type, sqlSelect );
    }

    @Override
    public String output( final String sqlSelect ) {
        checkNotNull( sqlSelect );

        Connection connectionFromQueryRunner = null;

        try {
            connectionFromQueryRunner = queryRunner.getDataSource().getConnection();

            final ResultSet resultSet = connectionFromQueryRunner.createStatement().executeQuery( sqlSelect );

            // single row!
            if ( resultSet.next() ) {
                return resultSet.getString( 1 );
            } else {
                return "";
            }

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        throw new IllegalStateException( "Query failed. Raise exception!" );
    }

    @Override
    public Boolean cleanup( final String scriptName ) {
        final List<String> dmlList = convertLocationToDmlStatements( scriptName );

        int runDMLMultiple = runDMLMultiple( queryRunner, dmlList );

        // the cleanup is run if there are updated rows?
        return runDMLMultiple > 0;
    }
}
