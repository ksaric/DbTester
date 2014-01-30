package hr.oet.dbtester.tester.list;

import com.google.common.collect.*;
import hr.oet.dbtester.ParametersProvider;
import hr.oet.dbtester.ResultProvider;
import hr.oet.dbtester.datasource.OracleDataSourceFactory;
import hr.oet.dbtester.domain.Artikli;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: ksaric
 */
public class OracleDbTesterTest {

    @Test
    public void testFunction() throws Exception {
        //Before


        OracleDbTester oracleDbTester = new OracleDbTester( OracleDataSourceFactory.createDefaultConnection() );
        oracleDbTester.init();

        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'), 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'), 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( "{call SKLADISTE_BATCH.izracun}" );

        //Then
        final ResultProvider<List<Artikli>> listOutput = oracleDbTester.listOutput( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT * FROM artikli WHERE id = 1",
                        "SELECT * FROM artikli WHERE id = 2"
                );
            }
        }, Artikli.class );

        Assert.assertTrue( function );

        List<Artikli> artikliList = listOutput.get();

        Assert.assertThat( artikliList, Matchers.hasItem( createArtikalSifra( "400401" ) ) );
        Assert.assertThat( artikliList, Matchers.hasItem( createArtikalSifra( "400402" ) ) );
        Assert.assertThat( artikliList, Matchers.not( Matchers.hasItem( createArtikalSifra( "400410" ) ) ) );

        oracleDbTester.cleanup( Lists.<String>newArrayList(
                "DELETE FROM artikli WHERE id = 1",
                "DELETE FROM artikli WHERE id = 2"
        ) );
    }

   /* @Test
    public void testPrimkaFunction() throws Exception {
        //Before
        OracleDbTester oracleDbTester = OracleDbTester.create();

        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'), 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'), 'Projekt', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_zg values (1, 1, 2, 'PR001', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Jamnica', 'D', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_zg values (2, 2, 1, 'PR002', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Coca-Cola', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_st values (1, 1, 1, 50, 4.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (2, 1, 5, 30, 6.50, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( "{call IZRACUN_SKLADISTA.izracun_prosjecne_cijene}" );

        //Then
        final ResultProvider<List<Artikli>> listOutput = oracleDbTester.listOutput( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT * FROM artikli WHERE id = 1",
                        "SELECT * FROM artikli WHERE id = 2"
                );
            }
        }, Artikli.class );

        Assert.assertTrue( function );

        List<Artikli> artikliList = listOutput.get();

        Assert.assertThat( artikliList, Matchers.hasItem( createArtikalSifra( "400401" ) ) );
        Assert.assertThat( artikliList, Matchers.hasItem( createArtikalSifra( "400402" ) ) );
        Assert.assertThat( artikliList, Matchers.not( Matchers.hasItem( createArtikalSifra( "400410" ) ) ) );

        // constraint
        *//*oracleDbTester.cleanup( Lists.<String>newArrayList(
                "DELETE FROM artikli WHERE id = 1",
                "DELETE FROM artikli WHERE id = 2"
        ) );*//*
    }*/

    private Artikli createArtikalSifra( String sifra ) {
        Artikli artikli = new Artikli();
        artikli.setSifra( sifra );
        return artikli;
    }
}
