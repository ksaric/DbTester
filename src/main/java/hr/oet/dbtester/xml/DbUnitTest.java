package hr.oet.dbtester.xml;

import com.google.common.base.Objects;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class DbUnitTest {

    @Attribute
    private String name;

    @Element(name = "cleanup_before")
    private String cleanupBefore;

    @Element
    private String upscript;

    @Element(name = "test_setup", required = false)
    private String testSetup;

    @Element
    private String procedure;

    @ElementList(name = "verification")
    private List<TestVerification> verifications;

    @Element(name = "cleanup_after")
    private String cleanupAfter;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getCleanupBefore() {
        return cleanupBefore;
    }

    public void setCleanupBefore( String cleanupBefore ) {
        this.cleanupBefore = cleanupBefore;
    }

    public String getUpscript() {
        return upscript;
    }

    public void setUpscript( String upscript ) {
        this.upscript = upscript;
    }

    public String getTestSetup() {
        return testSetup;
    }

    public void setTestSetup( String testSetup ) {
        this.testSetup = testSetup;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure( String procedure ) {
        this.procedure = procedure;
    }

    public List<TestVerification> getVerifications() {
        return verifications;
    }

    public void setVerifications( List<TestVerification> verifications ) {
        this.verifications = verifications;
    }

    public String getCleanupAfter() {
        return cleanupAfter;
    }

    public void setCleanupAfter( String cleanupAfter ) {
        this.cleanupAfter = cleanupAfter;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this )
                .add( "name", name )
                .add( "cleanupBefore", cleanupBefore )
                .add( "upscript", upscript )
                .add( "procedure", procedure )
                .add( "verifications", verifications )
                .add( "cleanupAfter", cleanupAfter )
                .toString();
    }
}
