package hr.oet.dbtester.tester.list;

import com.google.common.collect.*;
import hr.oet.dbtester.ParametersProvider;
import hr.oet.dbtester.ResultProvider;
import hr.oet.dbtester.datasource.OracleDataSourceFactory;
import hr.oet.dbtester.wrapper.CustomString;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * User: ksaric
 */
public class ProsjecnaCijenaTesterTest {

    private OracleDbTester oracleDbTester;

    public static final String PROCEDURE_IZRACUN = "SKLADISTE_BATCH.izracun";
    public static final String PROCEDURE_OLD_IZRACUN = "IZRACUN_SKLADISTA.izracun_prosjecne_cijene";

    public static final String PROCEDURE_CALL = String.format( "{call %s()}", PROCEDURE_IZRACUN );
    public static final String PROCEDURE_OLD_CALL = String.format( "{call %s()}", PROCEDURE_OLD_IZRACUN );

    private String getMethodName() {
        return PROCEDURE_CALL;
//        return PROCEDURE_OLD_CALL;
    }

    @Before
    public void setUp() throws Exception {
        oracleDbTester = new OracleDbTester( OracleDataSourceFactory.createDefaultConnection() );
        oracleDbTester.init();

        oracleDbTester.cleanup( Lists.<String>newArrayList(
                "DELETE FROM zalihe",
                "DELETE FROM zalihe_proba",
                "DELETE FROM izdatnice_st",
                "DELETE FROM izdatnice_zg",
                "DELETE FROM primke_st",
                "DELETE FROM primke_zg",
                "DELETE FROM artikli",
                "DELETE FROM mt",
                "DELETE FROM skladista",
                "DELETE FROM dobavljaci",
                "DELETE FROM brojevi",
                "COMMIT"
        ) );
    }

    @Test
    public void testIzracunZalihe() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into skladista values (1, 200201, 'Skladiste 1', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into zalihe values (1, 1, 1, 50, 4.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( getMethodName() );

        //Then
        final ResultProvider<List<CustomString>> prosjecnaCijena = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicina = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe"
                );
            }
        }, CustomString.class );

        Assert.assertThat( prosjecnaCijena.get().get( 0 ).getValue(), Matchers.equalTo( "4" ) );
        Assert.assertThat( ukupnaKolicina.get().get( 0 ).getValue(), Matchers.equalTo( "50" ) );
    }

    @Test
    public void testIzracunPrimke() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into dobavljaci values (1, 100101, 'Jamnica', 'Getaldiceva 3, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into dobavljaci values (2, 100102, 'Coca-Cola', 'Milana Sachsa 1, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into skladista values (1, 200201, 'Skladiste 1', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_zg values (1, 1, 1, 'PR001', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Jamnica', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_st values (1, 1, 1, 50, 4.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (2, 1, 2, 30, 6.50, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( getMethodName() );

        //Then
        final ResultProvider<List<CustomString>> prosjecnaCijena = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicina = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( prosjecnaCijena.get().get( 0 ).getValue(), Matchers.equalTo( "4" ) );
        Assert.assertThat( ukupnaKolicina.get().get( 0 ).getValue(), Matchers.equalTo( "50" ) );
    }

    @Test
    public void testIzracunDvijePrimke() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into dobavljaci values (1, 100101, 'Jamnica', 'Getaldiceva 3, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into dobavljaci values (2, 100102, 'Coca-Cola', 'Milana Sachsa 1, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into skladista values (1, 200201, 'Skladiste 1', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_zg values (1, 1, 1, 'PR001', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Jamnica', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_zg values (2, 1, 1, 'PR002', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Cola', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_st values (1, 1, 1, 50, 4.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (2, 1, 2, 10, 7.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (3, 2, 1, 50, 2.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( getMethodName() );

        //Then
        final ResultProvider<List<CustomString>> prosjecnaCijenaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> prosjecnaCijenaS1Art2 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art2 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA AS value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().size(), Matchers.equalTo( 1 ) );
        Assert.assertThat( prosjecnaCijenaS1Art2.get().size(), Matchers.equalTo( 1 ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().size(), Matchers.equalTo( 1 ) );
        Assert.assertThat( ukupnaKolicinaS1Art2.get().size(), Matchers.equalTo( 1 ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "3" ) );
        Assert.assertThat( prosjecnaCijenaS1Art2.get().get( 0 ).getValue(), Matchers.equalTo( "7" ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "100" ) );
        Assert.assertThat( ukupnaKolicinaS1Art2.get().get( 0 ).getValue(), Matchers.equalTo( "10" ) );
    }

    @Test
    public void testIzracunDvijePrimkeZaliha() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "insert into artikli values (1, 400401, 'Jamnica 0,5l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into artikli values (2, 400402, 'Coca-cola 2l', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into dobavljaci values (1, 100101, 'Jamnica', 'Getaldiceva 3, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into dobavljaci values (2, 100102, 'Coca-Cola', 'Milana Sachsa 1, Zagreb', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into skladista values (1, 200201, 'Skladiste 1', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into zalihe values (1, 1, 1, 50, 6.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_zg values (1, 1, 1, 'PR001', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Jamnica', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_zg values (2, 1, 1, 'PR002', to_date('11/1/2014', 'dd/mm/yyyy'), 'Primka dobavljaca Cola', 'N', 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",

                        "insert into primke_st values (1, 1, 1, 50, 4.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (2, 1, 2, 10, 7.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))",
                        "insert into primke_st values (3, 2, 1, 50, 2.00, 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'), 'PROJEKT', to_date('10/1/2014', 'dd/mm/yyyy'))"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( getMethodName() );

        //Then
        final ResultProvider<List<CustomString>> prosjecnaCijenaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> prosjecnaCijenaS1Art2 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art2 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA AS value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().size(), Matchers.equalTo( 1 ) );
        Assert.assertThat( prosjecnaCijenaS1Art2.get().size(), Matchers.equalTo( 1 ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().size(), Matchers.equalTo( 1 ) );
        Assert.assertThat( ukupnaKolicinaS1Art2.get().size(), Matchers.equalTo( 1 ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "4" ) );
        Assert.assertThat( prosjecnaCijenaS1Art2.get().get( 0 ).getValue(), Matchers.equalTo( "7" ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "150" ) );
        Assert.assertThat( ukupnaKolicinaS1Art2.get().get( 0 ).getValue(), Matchers.equalTo( "10" ) );
    }

    @After
    public void tearDown() throws Exception {
        // todo: FULL cleanup!
        oracleDbTester.cleanup( Lists.<String>newArrayList(
                "DELETE FROM zalihe",
                "DELETE FROM zalihe_proba",
                "DELETE FROM izdatnice_st",
                "DELETE FROM izdatnice_zg",
                "DELETE FROM primke_st",
                "DELETE FROM primke_zg",
                "DELETE FROM artikli",
                "DELETE FROM mt",
                "DELETE FROM skladista",
                "DELETE FROM dobavljaci",
                "DELETE FROM brojevi",
                "COMMIT"
        ) );

        Thread.sleep( 1000 );
    }
}
