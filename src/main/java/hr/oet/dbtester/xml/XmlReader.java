package hr.oet.dbtester.xml;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class XmlReader {

    public DbTest read( final File file ) {
        Preconditions.checkNotNull( file );

        DbTest dbTest = new DbTest();

        Serializer serializer = new Persister();

        try {
            dbTest = serializer.read( DbTest.class, file );
        } catch ( Exception e ) {
            throw new IllegalStateException( e.getMessage() );
        }

        return Optional.fromNullable( dbTest ).or( new DbTest() );
    }
}
