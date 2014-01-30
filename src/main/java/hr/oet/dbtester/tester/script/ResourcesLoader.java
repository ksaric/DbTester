package hr.oet.dbtester.tester.script;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */

public class ResourcesLoader {

    public static String loadScriptFromJarLocation() {

        String path = ResourcesLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = null;

        try {
            decodedPath = URLDecoder.decode( path, Charsets.UTF_8.displayName() );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }

        return Optional.fromNullable( decodedPath ).or( "" );
    }

    public static String loadScriptFromJarLocation( final String additionalPath ) {
        final String loadScriptFromJarLocation = loadScriptFromJarLocation();
        return loadScriptFromJarLocation.substring( 0, loadScriptFromJarLocation.lastIndexOf( "/" ) + 1 ) + additionalPath.trim();
    }

}
