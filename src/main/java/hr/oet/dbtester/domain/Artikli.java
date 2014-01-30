package hr.oet.dbtester.domain;

import com.google.common.base.Objects;

/**
 * User: ksaric
 */
public class Artikli extends AuditUser {

    private Long id;
    private String sifra;
    private String naziv;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra( String sifra ) {
        this.sifra = sifra;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv( String naziv ) {
        this.naziv = naziv;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Artikli artikli = (Artikli) o;

        if ( !sifra.equals( artikli.sifra ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sifra.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this )
                .add( "ID", id )
                .add( "SIFRA", sifra )
                .add( "NAZIV", naziv )
                .toString();
    }
}
