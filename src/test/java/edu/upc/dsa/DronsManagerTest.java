package edu.upc.dsa;

import edu.upc.dsa.domain.models.*;
import edu.upc.dsa.domain.repository.DronManager;
import edu.upc.dsa.domain.repository.DronManagerImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Date;

public class DronsManagerTest {
    DronManager manager;
    Dron d1;
    Dron d2;
    Dron d3;

    Pilot p1;
    Pilot p2;

    @Before
    public void setUp() {
        this.manager = DronManagerImpl.getInstance();

        // Afegim objectes per les proves:
        // 1. Afegim drons de prova
        d1 = this.manager.addDron("D001", "Phantom", "DJI", "Pro 4");
        d2 = this.manager.addDron("D002", "Mavic", "DJI", "Air 2");
        d3 = this.manager.addDron("Anafi", "Parrot", "Work");

        // 2. Afegim pilots de prova
        p1 = this.manager.addPilot("P001", "Joan", "Garcia");
        p2 = this.manager.addPilot("Laura", "Martínez");

    }

    @After
    public void tearDown() {
        // Nategem dades en acabar prova
        this.manager.clear();
    }
    @Test
    public void testEstatInicial() {
        // Comprovem que el @Before ha inserit correctament les dades de partida
        Assert.assertEquals(3, this.manager.numDrons());
        Assert.assertEquals(2, this.manager.numPilots());
        Assert.assertEquals(0, this.manager.numDronsMagatzem());

    }

    // 1. Test per a guardar un dron al magatzem de manteniment
    @Test
    public void testGuardarDronEnMagatzem() throws Exception {
        //No ha d'haver-hi cap dron al magatzem
        Assert.assertEquals(0, this.manager.numDronsMagatzem());
        //El dron 1 encara no està en manteniment
        Assert.assertFalse(d1.isEnManteniment());
        //Guardem dron al magatzem
        Dron dronGuardat = this.manager.guardarDronEnMagatzem("D001");
        //El dron no ha de ser null
        Assert.assertNotNull(dronGuardat);
        //Mirem que ara el dron 1 sí que el marqui en manteniment
        Assert.assertTrue(dronGuardat.isEnManteniment());
        //Mirem que ara tinguim 1 dron al magatzem
        Assert.assertEquals(1, this.manager.numDronsMagatzem());
    }

    // 2. Test per a reparar el dron més proper a la porta (LIFO - Pila)
    @Test
    public void testRepararDron() throws Exception {
        // Guardem dos drons al magatzem: primer D001 i després D002
        this.manager.guardarDronEnMagatzem("D001");
        this.manager.guardarDronEnMagatzem("D002");
        Assert.assertEquals(2, this.manager.numDronsMagatzem());

        // Com que l'últim en entrar ha estat D002, ha de ser el primer a sortir
        Dron reparat = this.manager.repararDron();

        //El dron reparat No ha de ser nul
        Assert.assertNotNull(reparat);
        //El dron reparat ha de ser el primer
        Assert.assertEquals("D002", reparat.getId());
        //El dron 2 s'ha d'haver marcat en manteniment
        Assert.assertFalse(reparat.isEnManteniment());
        //Al magatzem ha de quedar només 1 dron
        Assert.assertEquals(1, this.manager.numDronsMagatzem());
    }

    // 3. Test per a afegir un pla de vol correctament
    @Test
    public void testAddFlightPlan() throws Exception {
        Date avui = new Date(); //data d'avui
        // Creem flightplan amd idDron i idPilot, data d'avui, hores
        FlightPlan plan = new FlightPlan("D001", "P001", avui, 2.0);
        //afegim posicions per les quals passarà
        plan.addPosicio(41.2756, 1.9868);

        //afegim flightplan
        FlightPlan afegit = this.manager.addFlightPlan(plan);

        //Comprobem que FP afegit no és null
        Assert.assertNotNull(afegit);
        //Les hores de vol del pilo1 i del dron 1 han de ser 2
        Assert.assertEquals(2.0, d1.getHoresVol(), 0.01);
        Assert.assertEquals(2.0, p1.getHoresVol(), 0.01);

        //En l'historial de plans de vol del dron 1 i el pilot 1 ha d'haver-hi només 1 pla de vol
        List<FlightPlan> plansDron = this.manager.getFlightPlansByDron("D001");
        Assert.assertEquals(1, plansDron.size());
        List<FlightPlan> plansPilot = this.manager.getFlightPlansByPilot("P001");
        Assert.assertEquals(1, plansPilot.size());
    }

    // 4. Test per a llistat de drons ordenats descendentment per hores de vol
    @Test
    public void testGetDronesByHoras() throws Exception {
        Date avui = new Date();

        // D001 fa un vol de 1 hora
        FlightPlan fp1 = new FlightPlan(d1.getId(), p1.getId(), avui, 1.0);
        this.manager.addFlightPlan(fp1);

        // D002 fa un vol de 4 hores (data més tard per evitar solapaments)
        Date despres = new Date(avui.getTime() + 10000000);
        FlightPlan fp2 = new FlightPlan(d2.getId(), p2.getId(), despres, 4.0);
        this.manager.addFlightPlan(fp2);

        //Cridem el mètode que ordena els drons
        List<Dron> dronsOrdenats = this.manager.getDronsByHours();

        //Ens assegurem que hi ha 3 drons a la llista
        Assert.assertEquals(3, dronsOrdenats.size());

        // El primer ha de ser D002 (4h), seguit de D001 (1h) i finalment D003 (0h)
        Assert.assertEquals("D002", dronsOrdenats.get(0).getId());
        Assert.assertEquals("D001", dronsOrdenats.get(1).getId());
        Assert.assertEquals(d3.getId(), dronsOrdenats.get(2).getId());
    }

    // 5. Test d'error: Intentar afegir un pla de vol a un dron en manteniment
    @Test(expected = Exception.class)
    public void testAddFlightPlanDronEnManteniment() throws Exception {
        this.manager.guardarDronEnMagatzem("D001");

        // Això ha de llençar una Exception perquè el dron està en manteniment
        FlightPlan plan = new FlightPlan("D001", "P001", new Date(), 2.0);
        this.manager.addFlightPlan(plan);
    }

    // AFEGIT DESPRÉS DEL 05/11/2026 (DIA MÍNIM CLASSE)
    // He afegit els tests següents per acabar de validar totes les funcionalitats que ens demana l'enunciat

    // 6. Test per afegir Dron i Pilot
    @Test
    public void testAddDronAndPilot() {
        // Comprobem que podem afegir dron i pilot posant nosaltres ID
        Dron dNou = this.manager.addDron("D003", "Spark", "DJI", "Mini");
        Pilot pNou = this.manager.addPilot("P003", "Marc", "Pérez");

        //Ni el pilot ni el dron afegits poden ser nulls
        Assert.assertNotNull(dNou);
        Assert.assertNotNull(pNou);

        //Núm drons ha de ser 4 (3 afegits abans i 1 ara)
        //Núm pilots ha de ser 3 (2 afegits abans i 1 ara)
        Assert.assertEquals(4, this.manager.numDrons());
        Assert.assertEquals(3, this.manager.numPilots());

        // Comprobem que podem afegir dron i pilot sense posar nosaltres ID
        Dron dNou2 = this.manager.addDron("dron2", "DJI2", "Mini2");
        Pilot pNou2 = this.manager.addPilot("Mariona", "Garcia");

        //Ni el pilot ni el dron afegits poden ser nulls
        Assert.assertNotNull(dNou2);
        Assert.assertNotNull(pNou2);

        //Núm drons ha de ser 5 (3 afegits abans i 2 ara)
        //Núm pilots ha de ser 4 (2 afegits abans i 2 ara)
        Assert.assertEquals(5, this.manager.numDrons());
        Assert.assertEquals(4, this.manager.numPilots());
    }

    // 7. Test per consultar plans de vol per Dron un
    @Test
    public void testGetFlightPlansByDron() throws Exception {

        //Afegim pla de bol per un dron
        Date avui = new Date();
        FlightPlan plan = new FlightPlan("D001", "P001", avui, 2.0);
        this.manager.addFlightPlan(plan);

        //Obtenim els FP assignats al dron D001 i al pilot
        List<FlightPlan> plansDron = this.manager.getFlightPlansByDron("D001");
        List<FlightPlan> plansPilot = this.manager.getFlightPlansByPilot("P001");


        //Comprobem que el nombre de FP assignats al dron i al pilot és 1.
        Assert.assertNotNull(plansDron);
        Assert.assertNotNull(plansPilot);
        Assert.assertEquals(1, plansDron.size());
        Assert.assertEquals("D001", plansDron.get(0).getDronId());
        Assert.assertEquals(1, plansPilot.size());
        Assert.assertEquals("P001", plansPilot.get(0).getPilotId());
    }

    // 8. Test per consultar pilots ordenats per hores de vol descendentment
    @Test
    public void testGetPilotsByHoras() throws Exception {
        //Creem un 3r pilot
        Pilot pNou = this.manager.addPilot("P003", "Marc", "Pérez");

        //Creem FP pel pilot p1
        Date avui = new Date();

        // Pilot1 fa un vol de 5 hores
        FlightPlan fp1 = new FlightPlan(d1.getId(), p1.getId(), avui, 5.0);
        this.manager.addFlightPlan(fp1);

        // Pilot2 fa un vol de 4 hores (data més tard per evitar solapaments)
        Date despres = new Date(avui.getTime() + 10000000);
        FlightPlan fp2 = new FlightPlan(d2.getId(), p2.getId(), despres, 4.0);
        this.manager.addFlightPlan(fp2);

        // Pilot3 fa un vol de 1h (data més tard per evitar solapaments)
        Date despres2 = new Date(avui.getTime() + 50000000);
        FlightPlan fp3 = new FlightPlan(d2.getId(), pNou.getId(), despres2, 1.0);
        this.manager.addFlightPlan(fp3);

        //Cridem el mètode que ordena els drons
        List<Pilot> pilotsOrdenats = this.manager.getPilotsByHours();

        //Ens assegurem que hi ha 3 pilots a la llista
        Assert.assertEquals(3, pilotsOrdenats.size());

        // El primer ha de ser P1 (5h), seguit de P2 (4h) i finalment P3 (1h)
        Assert.assertEquals("P001", pilotsOrdenats.get(0).getId());
        Assert.assertEquals(p2.getId(), pilotsOrdenats.get(1).getId());
        Assert.assertEquals(pNou.getId(), pilotsOrdenats.get(2).getId());
    }

    // 9. Test d'error: Intentar afegir un pla de vol que solapa l'horari del DRON
    @Test(expected = Exception.class)
    public void testAddFlightPlanSolapamentDron() throws Exception {
        Date ara = new Date();
        // Primer vol: D001 amb P001 durant 2 hores (ex. 10:00 a 12:00)
        FlightPlan fp1 = new FlightPlan("D001", "P001", ara, 2.0);
        this.manager.addFlightPlan(fp1);

        // Segon vol: MATEIX DRON (D001) amb un altre pilot (p2), començant 1h més tard -> Solapament!
        Date solapat = new Date(ara.getTime() + 3600000); // +1 hora (11:00 a 13:00)
        FlightPlan fp2 = new FlightPlan("D001", p2.getId(), solapat, 2.0);

        // Ha de llençar una Exception i no deixar-lo crear
        this.manager.addFlightPlan(fp2);
    }

    // 10. Test d'error: Intentar afegir un pla de vol que solapa l'horari del PILOT
    @Test(expected = Exception.class)
    public void testAddFlightPlanSolapamentPilot() throws Exception {
        Date ara = new Date();
        // Primer vol: P001 amb D001 durant 2 hores
        FlightPlan fp1 = new FlightPlan("D001", "P001", ara, 2.0);
        this.manager.addFlightPlan(fp1);

        // Segon vol: MATEIX PILOT (P001) amb un altre dron (D002), començant 30 minuts més tard -> Solapament!
        Date solapat = new Date(ara.getTime() + 1800000); // +30 minuts
        FlightPlan fp2 = new FlightPlan("D002", "P001", solapat, 2.0);

        // Ha de llençar una Exception i no deixar-lo crear
        this.manager.addFlightPlan(fp2);
    }
}
