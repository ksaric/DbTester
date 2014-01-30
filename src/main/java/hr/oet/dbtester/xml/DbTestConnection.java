package hr.oet.dbtester.xml;

import com.google.common.base.Objects;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class DbTestConnection {

    @Attribute
    private String name;

    @Attribute
    private String type;

    @Element
    private String driver;

    @Element
    private String url;

    @Element
    private String username;

    @Element
    private String password;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver( String driver ) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this )
                .add( "name", name )
                .add( "type", type )
                .add( "driver", driver )
                .add( "url", url )
                .add( "username", username )
                .add( "password", password )
                .toString();
    }
}
