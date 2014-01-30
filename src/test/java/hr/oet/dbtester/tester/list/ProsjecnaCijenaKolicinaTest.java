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
 * User: ksaric, pfh (Kristijan Šarić)
 */
public class ProsjecnaCijenaKolicinaTest {

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
//                "DELETE FROM zalihe_proba",
                "DELETE FROM izdatnice_st",
                "DELETE FROM izdatnice_zg",
                "DELETE FROM primke_st",
                "DELETE FROM primke_zg",
                "DELETE FROM error_log",
                "DELETE FROM artikli",
                "DELETE FROM mt",
                "DELETE FROM brojevi",
                "DELETE FROM skladista",
                "DELETE FROM dobavljaci",


                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('1', '100101', 'Krastavci 100 g')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('2', '100102', 'Paprika 100 g')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('3', '100103', 'Jaja 12 kom')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('4', '100104', 'Pasteta 10 dag')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('5', '100105', 'Sol 1 kg')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('6', '100106', 'Secer 1 kg')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('7', '100107', 'Papar 10 dag')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('8', '100108', 'Maslac 100 g')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('9', '100109', 'Vrhnje 300 g')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('10', '100110', 'Ajvar 350 g')",
                "INSERT INTO ARTIKLI (ID, SIFRA, NAZIV) VALUES ('11', '100111', 'Cikla 380 g')",

                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('1', '200201', 'Podravka', 'A. Starcevica 32, Koprivnica')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('2', '200202', 'Agrokor', 'Palisina 43, Zagreb')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('3', '200203', 'Agrokoka', 'Valmade 13, Pula')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('4', '200204', 'Zvijezda', 'M. Cavica 1, Zagreb')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('5', '200205', 'Dukat', 'M. Cavica 9, Zagreb')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('6', '200206', 'Solana Pag', 'Svilno bb, Pag')",
                "INSERT INTO DOBAVLJACI (ID, SIFRA, NAZIV, ADRESA) VALUES ('7', '200207', 'Luxor', 'Put Majdana bb, Solin')",

                "INSERT INTO SKLADISTA (ID, SIFRA, NAZIV) VALUES ('1', '300301', 'Skladiste jedan')",
                "INSERT INTO SKLADISTA (ID, SIFRA, NAZIV) VALUES ('2', '300302', 'Skladiste dva')",
                "INSERT INTO SKLADISTA (ID, SIFRA, NAZIV) VALUES ('3', '300303', 'Skladiste tri')",

                "INSERT INTO MT (ID, SIFRA, NAZIV) VALUES ('1', '400401', 'Konoba Franko')",
                "INSERT INTO MT (ID, SIFRA, NAZIV) VALUES ('2', '400402', 'Hotel Sheraton')",
                "INSERT INTO MT (ID, SIFRA, NAZIV) VALUES ('3', '400403', 'Caffe Bar P14')",
                "INSERT INTO MT (ID, SIFRA, NAZIV) VALUES ('4', '400404', 'Caffe Bar Cocolo')",

                "COMMIT"
        ) );
    }

    /*

    before: zaliha 0 u skladistu jedan
    and: doslo je 100 kom krastavaca od dobavljaca Podravka
    where: povecanje zaliha 100 kom
    after: stanje zaliha 100 kom krastavaca u skladistu jedan

    */

    @Test
    public void testIzracun1() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (1, 1, 1, 1, TO_DATE('28.12.13', 'DD.MM.RR'), 'Primka dobavljaca Podravka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (1, 1, 1, 100, 10)"
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

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "10" ) );
        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "100" ) );
    }

    /*

    before: zaliha 50 u skladistu jedan
    and: doslo je 200 kom paprika od dobavljaca Podravka
    where: povecanje zaliha 200 kom
    after: stanje zaliha 250 kom paprika u skladistu jedan, prosjecna cijena 11,9

    ((50*11.5)+(200*12))/250 = 11.9

     */

    @Test
    public void testIzracun2() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 1, 2, 50, 11.50)",
                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (1, 1, 1, 1, TO_DATE('28.12.13', 'DD.MM.RR'), 'Primka dobavljaca Podravka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (1, 1, 2, 200, 12)"
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
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=2"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "11.9" ) );
        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "250" ) );
    }

    /*

    before: zaliha 25 u skladistu dva
    and: doslo je 240 kom jaja od dobavljaca Agrokor
    and: doslo je 300 kom jaja od odbavljaca Agrokoka
    where: povecanje zaliha za 540 kom
    after: stanje zaliha 565 kom jaja u skladistu dva
    prosjecna cijena ??? 13.56, izbacilo je 26,51

    */

    // ((240*14) + (13*300) + (16*25)) / 565 // N
    // 13.5575 => 13.56

    @Test
    public void testIzracun3() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 2, 3, 25, 16)",

                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (1, 2, 2, 1, TO_DATE('28.12.13', 'DD.MM.RR'), 'Primka dobavljaca Agrokor')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (1, 1, 3, 240, 14)",

                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (2, 3, 2, 2, TO_DATE('28.12.13', 'DD.MM.RR'), 'Primka dobavljaca Agrokoka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (2, 2, 3, 300, 13)"
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
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "565" ) );
        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "13.56" ) );
    }

    /*

    before: stanje zaliha jaja u skladistu dva je 240
    and: doslo je 480 kom jaja od dobavljaca Agrokoka
    where: povecanje zaliha 480 kom
    after: stanje zaliha 720 kom jaja u skladistu dva
    17,33 prosjecna cijena ???

    */

    // ((240*16) + (480*18)) / 720 // N
    // 17.3333

    @Test
    public void testIzracun4() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 2, 3, 240, '16')",

                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (1, 3, 2, 1, TO_DATE('30.12.13', 'DD.MM.RR'), 'Primka dobavljaca Agrokoka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (1, 1, 3, 480, '18')"
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
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "720" ) );
        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "17.33" ) );
    }

    /*

    before: stanje zaliha 240 krastavaca u skladistu dva i 480 pasteta u skladistu dva i 40 kom krastavaca u skladistu jedan
    and: doslo je 100 kom krastavaca od dobavljaca Zvijezda
    and: doslo je 120 kom pasteta od dobavljaca Podravka
    and: doslo je 80 kom pasteta od dobavljaca Podravka
    where: povecanje zaliha 220 kom
    after: stanje zaliha krastavaca u skladistu dva je 340 kom i pasteta 600 kom i u skladistu jedan 160 kom pasteta
    prosjecna cijena

    */

    // 1,1 => ((40*16) + (80*15))/120 // N                              => 15.3333
    // 2,1 => ((240*16) + (100*18) + (120*15.5))/460 // N               => 16.3043 => 16.31 ??
    // 2,4 => (480*16)/480 //N                                          => 16

    @Test
    public void testIzracun5() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 2, 1, 240, '16')",
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (2, 2, 4, 480, '16')",
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (3, 1, 1, 40, '16')",

                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (1, 4, 2, 1, TO_DATE('30.12.13', 'DD.MM.RR'), 'Primka dobavljaca Zvijezda')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (1, 1, 1, 100, '18')",
                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (2, 1, 2, 2, TO_DATE('30.12.13', 'DD.MM.RR'), 'Primka dobavljaca Podravka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (2, 2, 1, 120, 15.5)",
                        "INSERT INTO PRIMKE_ZG (ID, ID_DOBAVLJACA, ID_SKLADISTA, BROJ, DATUM, OPIS) VALUES (3, 1, 1, 3, TO_DATE('30.12.13', 'DD.MM.RR'), 'Primka dobavljaca Podravka')",
                        "INSERT INTO PRIMKE_ST (ID, ID_PRIMKE, ID_ARTIKLA, KOLICINA, NABAVNA_CIJENA) VALUES (3, 3, 1, 80, '15')"
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
        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> prosjecnaCijenaS2Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );
        final ResultProvider<List<CustomString>> ukupnaKolicinaS2Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );

        final ResultProvider<List<CustomString>> prosjecnaCijenaS2Art4 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=4"
                );
            }
        }, CustomString.class );
        final ResultProvider<List<CustomString>> ukupnaKolicinaS2Art4 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=4"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "120" ) );
        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "15.33" ) );

        Assert.assertThat( ukupnaKolicinaS2Art1.get().get( 0 ).getValue(), Matchers.equalTo( "460" ) );
        Assert.assertThat( prosjecnaCijenaS2Art1.get().get( 0 ).getValue(), Matchers.equalTo( "16.31" ) );

        Assert.assertThat( ukupnaKolicinaS2Art4.get().get( 0 ).getValue(), Matchers.equalTo( "480" ) );
        Assert.assertThat( prosjecnaCijenaS2Art4.get().get( 0 ).getValue(), Matchers.equalTo( "16" ) );
    }

    /*

    before: stanje zaliha 400 kom krastavaca u skladistu jedan
    and: izdali smo 50 kom
    where: smanjenje zaliha 50 kom
    after: novo stanje je 350 kom krastavaca u skladistu jedan

    PROSJEČNA CIJENA izdatnice ne utječe na cijenu!

    */

    // 1,1      => ((400*13) + (50*10.75))/450 // N                          => 12.75
    // 1,1 KOL  => 400 - 50                                                 => 350

    @Test
    public void testIzracun6() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 1, 1, 400, 13)",

                        "INSERT INTO IZDATNICE_ZG (ID, ID_MT, ID_SKLADISTA, DATUM, OPIS) VALUES (1, 1, 1, TO_DATE('21.01.14', 'DD.MM.RR'), 'Izdatnica Konoba Franko')",
                        "INSERT INTO IZDATNICE_ST (ID, ID_IZDATNICE, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 1, 1, 50, 10.75)"
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
        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=1 AND ID_ARTIKLA=1"
                );
            }
        }, CustomString.class );


        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "350" ) );
        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "13" ) );
    }

    /*

    before: u zalihama skladista dva stanje jaja 720 kom, a u skladistu jedan 800 kom jaja
    and: skinuli smo 320 jaja
    where: smanjenje zaliha za 320 kom
    after: novo stanje 400 kom jaja u skladistu dva, skladiste jedan ostaje isto, 800 kom jaja

    PROSJEČNA CIJENA izdatnice ne utječe na cijenu!

    */

    // 2,3 KOL  => 720 - 320                                                => 400

    @Test
    public void testIzracun7() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 2, 3, 720, 18)",
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (2, 1, 3, 800, 18)",

                        "INSERT INTO IZDATNICE_ZG (ID, ID_MT, ID_SKLADISTA, DATUM, OPIS) VALUES (1, 1, 2, TO_DATE('21.01.14', 'DD.MM.RR'), 'Izdatnica Konoba Franko')",
                        "INSERT INTO IZDATNICE_ST (ID, ID_IZDATNICE, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 1, 3, 320, 17.75)"
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
                        "SELECT PROSJECNA_CIJENA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );
        final ResultProvider<List<CustomString>> ukupnaKolicinaS1Art1 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM zalihe WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );


        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( ukupnaKolicinaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "400" ) );
        Assert.assertThat( prosjecnaCijenaS1Art1.get().get( 0 ).getValue(), Matchers.equalTo( "18" ) );
    }

    /*

    before: stanje zaliha jaja u skladistu dva je 400 kom
    and: skidanje sa zaliha 450 kom
    where: smanjenje zaliha za 450
    after: minus 50 kom jaja u error_log tablici

    PROSJEČNA CIJENA izdatnice ne utječe na cijenu!

    */

    // 2,3 KOL  => 720 - 320                                                => 400

    @Test
    public void testIzracun8() throws Exception {
        //Before
        oracleDbTester.input( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "INSERT INTO ZALIHE (ID, ID_SKLADISTA, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 2, 3, 400, 18)",

                        "INSERT INTO IZDATNICE_ZG (ID, ID_MT, ID_SKLADISTA, DATUM, OPIS) VALUES (1, 2, 2, TO_DATE('22.04.14', 'DD.MM.RR'), 'Izdatnica Hotel Sheraton')",
                        "INSERT INTO IZDATNICE_ST (ID, ID_IZDATNICE, ID_ARTIKLA, KOLICINA, PROSJECNA_CIJENA) VALUES (1, 1, 3, 450, 17.75)"
                );
            }
        } );

        //When
        Boolean function = oracleDbTester.function( getMethodName() );

        //Then
        final ResultProvider<List<CustomString>> kolicinaErrorLog = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM ERROR_LOG WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );
        final ResultProvider<List<CustomString>> zalihaS2A3 = oracleDbTester.output( new ParametersProvider<List<String>>() {
            @Override
            public List<String> get() {
                return Lists.newArrayList(
                        "SELECT KOLICINA as value FROM ZALIHE WHERE ID_SKLADISTA=2 AND ID_ARTIKLA=3"
                );
            }
        }, CustomString.class );

        Assert.assertThat( function, Matchers.is( true ) );

        Assert.assertThat( kolicinaErrorLog.get().get( 0 ).getValue(), Matchers.equalTo( "-50" ) );
        Assert.assertThat( zalihaS2A3.get().get( 0 ).getValue(), Matchers.equalTo( "400" ) );
    }

    @After
    public void tearDown() throws Exception {
        // todo: FULL cleanup!
        oracleDbTester.cleanup( Lists.<String>newArrayList(
                "DELETE FROM zalihe",
                "DELETE FROM izdatnice_st",
                "DELETE FROM izdatnice_zg",
                "DELETE FROM primke_st",
                "DELETE FROM primke_zg",
                "DELETE FROM error_log",
                "DELETE FROM artikli",
                "DELETE FROM mt",
                "DELETE FROM brojevi",
                "DELETE FROM skladista",
                "COMMIT"
        ) );

        Thread.sleep( 1000 );
    }
}
