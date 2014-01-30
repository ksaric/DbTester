package hr.oet.dbtester.domain;

/**
 * User: ksaric
 */
public class AuditUser {

    public String user_dodavanja;
    public String datum_dodavanja;
    public String user_izmjene;
    public String datum_izmjene;

    public String getUser_dodavanja() {
        return user_dodavanja;
    }

    public void setUser_dodavanja( String user_dodavanja ) {
        this.user_dodavanja = user_dodavanja;
    }

    public String getDatum_dodavanja() {
        return datum_dodavanja;
    }

    public void setDatum_dodavanja( String datum_dodavanja ) {
        this.datum_dodavanja = datum_dodavanja;
    }

    public String getUser_izmjene() {
        return user_izmjene;
    }

    public void setUser_izmjene( String user_izmjene ) {
        this.user_izmjene = user_izmjene;
    }

    public String getDatum_izmjene() {
        return datum_izmjene;
    }

    public void setDatum_izmjene( String datum_izmjene ) {
        this.datum_izmjene = datum_izmjene;
    }
}
