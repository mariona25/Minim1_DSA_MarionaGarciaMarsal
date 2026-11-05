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
        // Nategem dades en acabar proba
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
        //Les hores de vol del pilo1 i i del dron 1 han de ser 2
        Assert.assertEquals(2.0, d1.getHoresVol(), 0.01);
        Assert.assertEquals(2.0, p1.getHoresVol(), 0.01);

        //En l'historial de plans de vol del dron 1 i el pilot 1 ha d'haver-hi només i pla de vol
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


}
