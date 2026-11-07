package edu.upc.dsa.domain.repository;

import edu.upc.dsa.domain.models.FlightPlan;
import edu.upc.dsa.domain.models.Pilot;
import edu.upc.dsa.domain.models.Dron;

import java.util.*;

import org.apache.log4j.Logger;

public class DronManagerImpl implements DronManager {

    // Instància única Singleton
    private static DronManager instance;
    // Logger de Log4j
    final static Logger logger = Logger.getLogger(DronManagerImpl.class);

    // Estructures de dades
    protected Map<String, Dron> drons; //HashMap amb clau ID dron
    protected Map<String, Pilot> pilots; //HashMap amb clau ID pilot
    protected Stack<Dron> magatzemManteniment; // Pila (LIFO) per al magatzem


    private DronManagerImpl() {
        try {
            this.drons = new HashMap<>();
            this.pilots = new HashMap<>();
            this.magatzemManteniment = new Stack<>();
            logger.info("DroneManagerImpl inicialitzat correctament");
        } catch (Exception e) {
            logger.fatal("Error crític durant la inicialització de les estructures de dades", e);
        }
    }

    public static DronManager getInstance() {
        if (instance==null) instance = new DronManagerImpl();
        return instance;
    }

    // AFEGIT DESPRÉS DEL 05/11/2026 (DIA MÍNIM CLASSE)
    // He comentat una mica tots els mètodes

    //Definim tots els mètodes que hem posat a la interfície:

    //1. Afegir un nou dron (o actualitzar-ne la informació)
    @Override
    public Dron addDron(Dron d) {

        if (d == null) {
            logger.error("addDron: El dron proporcionat és nul");
            return null;
        }
        logger.info("addDron: Afegint/Actualitzant dron " + d.getId());
        // Si el dron ja existeix, actualitzem dades. Si no, l'afegim al HashMap
        Dron dExist = this.getDronById(d.getId());
        if (dExist != null) {
            logger.info("El dron amb ID " + d.getId() + " ja existeix. Actualitzant dades...");
            dExist.setNom(d.getNom());
            dExist.setFabricant(d.getFabricant());
            dExist.setModel(d.getModel());
            return dExist;
        } else {
            this.drons.put(d.getId(), d);
            logger.info("Nou dron afegit correctament: " + d);
            return d;
        }
    }
    @Override
    public Dron addDron(String nom, String fabricant, String model) {
        return this.addDron(new Dron(nom, fabricant, model));
    }
    @Override
    public Dron addDron(String id, String nom, String fabricant, String model) {
        return this.addDron(new Dron(id, nom, fabricant, model));
    }

    // 2. Afegir un nou pilot (o actualitzar-ne la informació)
    @Override
    public Pilot addPilot(Pilot p) {
        if (p == null) {
            logger.error("addPilot: El pilot proporcionat és nul");
            return null;
        }
        logger.info("addPilot: Afegint/Actualitzant pilot " + p.getId());
        // Si el dron ja existeix, actualitzem dades. Si no, l'afegim al HashMap
        Pilot pExist = this.getPilotById(p.getId());
        if (pExist != null) {
            logger.info("El pilot amb ID " + p.getId() + " ja existeix. Actualitzant dades...");
            pExist.setNom(p.getNom());
            pExist.setApellidos(p.getCognom());
            return pExist;
        } else {
            this.pilots.put(p.getId(), p);
            logger.info("Nou pilot afegit correctament: " + p);
            return p;
        }
    }
    @Override
    public Pilot addPilot(String nom, String cognom) {
        return this.addPilot(new Pilot(nom, cognom));
    }
    @Override
    public Pilot addPilot(String id, String nom, String cognom) {
        return this.addPilot(new Pilot(id, nom, cognom));
    }

    // 3. Llistat de drons ordenats descendentment per hores de vol
    @Override
    public List<Dron> getDronsByHours() {
        logger.info("getDronesByHoras: Inici");
        //Del HashMap li demanem que ens extregui tots els drons
        List<Dron> result = new ArrayList<>(this.drons.values());
        //Els ordenem
        result.sort(Comparator.comparingDouble(Dron::getHoresVol).reversed());
        logger.info("getDronesByHoras: Retornant " + result.size() + " drons ordenats descendentment per hores");
        return result;
    }

    // 4. Llistat de pilots ordenats descendentment per hores de vol
    @Override
    public List<Pilot> getPilotsByHours() {
        logger.info("getPilotsByHoras: Inici");
        //Del HashMap li demanem que ens extregui tots els pilots
        List<Pilot> result = new ArrayList<>(this.pilots.values());
        //Els ordenem
        result.sort(Comparator.comparingDouble(Pilot::getHoresVol).reversed());
        logger.info("getPilotsByHoras: Retornant " + result.size() + " pilots ordenats descendentment per hores");
        return result;
    }

    // 5. Guardar un dron al magatzem de manteniment (s'apila al fons, LIFO)
    @Override
    public Dron guardarDronEnMagatzem(String dronId) throws Exception {
        logger.info("guardarDronEnAlmacen: Inici amb dronId = " + dronId);
        if (dronId == null) {
            logger.error("guardarDronEnAlmacen: L'ID del dron és null");
            throw new Exception("ID de dron no vàlid");
        }
        Dron dron = this.getDronById(dronId);
        if (dron == null) {
            logger.error("guardarDronEnAlmacen: Dron " + dronId + " no trobat");
            throw new Exception("Dron no trobat");
        }
        if (dron.isEnManteniment()) {
            logger.error("guardarDronEnAlmacen: El dron ja està al magatzem");
            throw new Exception("El dron ja està en manteniment");
        }
        dron.setEnManteniment(true); // El marquem en manteniment
        this.magatzemManteniment.push(dron); // Entra a la Pila (LIFO)
        logger.info("guardarDronEnAlmacen: Dron " + dronId + " afegit a la pila del magatzem. Drons al magatzem: " + this.magatzemManteniment.size());
        return dron;
    }

    // 6. Reparar/manteniment del dron més proper a la porta (LIFO - Pop de la Pila)
    @Override
    public Dron repararDron() throws Exception {
        logger.info("repararDron: Intentant reparar el dron més proper a la porta (pila)");
        if (this.magatzemManteniment.isEmpty()) {
            logger.error("repararDron: El magatzem està buit, no hi ha drons per reparar");
            throw new Exception("No hi ha drons al magatzem");
        }
        Dron dron = this.magatzemManteniment.pop(); // Extreu l'últim que ha entrat
        dron.setEnManteniment(false); // El tornem a posar en servei
        logger.info("repararDron: Dron " + dron.getId() + " reparat i reactivat amb èxit. Drons restants al magatzem: " + this.magatzemManteniment.size());
        return dron;
    }

    // 7. Crear un pla de vol (comprova solapaments i si el dron està en manteniment)
    @Override
    public FlightPlan addFlightPlan(FlightPlan plan) throws Exception {
        if (plan == null || plan.getDronId() == null || plan.getPilotId() == null || plan.getDataHora() == null) {
            logger.error("addFlightPlan: Dades incompletes del pla de vol");
            throw new Exception("Pla de vol no vàlid");
        }
        logger.info("addFlightPlan: Processant nou pla de vol per dron " + plan.getDronId() + " i pilot " + plan.getPilotId());
        // Comprovem que el dron existeix
        Dron dron = this.getDronById(plan.getDronId());
        if (dron == null) {
            logger.error("addFlightPlan: Dron " + plan.getDronId() + " no trobat");
            throw new Exception("Dron no trobat");
        }
        // Comprovem que el dron no estigui al magatzem
        if (dron.isEnManteniment()) {
            logger.error("addFlightPlan: El dron " + plan.getDronId() + " està en manteniment i no pot volar");
            throw new Exception("El dron està en manteniment");
        }
        // Comprovem que el pilot existeix
        Pilot pilot = this.getPilotById(plan.getPilotId());
        if (pilot == null) {
            logger.error("addFlightPlan: Pilot " + plan.getPilotId() + " no trobat");
            throw new Exception("Pilot no trobat");
        }
        // Comprovació de solapament horari per al dron i pilot
        // Calculem l'interval de temps del nou vol (en mil·lisegons)
        long nouInici = plan.getDataHora().getTime();
        long nouFi = nouInici + (long) (plan.getDuracioHores() * 3600 * 1000);
        // Comprovem que el dron no tingui un altre vol solapat en aquest horari
        for (FlightPlan fp : dron.getHistorialPlaVol()) {
            long existInici = fp.getDataHora().getTime();
            long existFi = existInici + (long) (fp.getDuracioHores() * 3600 * 1000);
            if (nouInici < existFi && nouFi > existInici) {
                logger.error("addFlightPlan: Solapament d'horari detectat per al dron " + dron.getId());
                throw new Exception("El dron ja té un pla de vol assignat en aquest interval");
            }
        }
        // Comprovem que el pilot no tingui un altre vol solapat en aquest horari
        for (FlightPlan fp : pilot.getHistorialPlaVol()) {
            long existInici = fp.getDataHora().getTime();
            long existFi = existInici + (long) (fp.getDuracioHores() * 3600 * 1000);
            if (nouInici < existFi && nouFi > existInici) {
                logger.error("addFlightPlan: Solapament d'horari detectat per al pilot " + pilot.getId());
                throw new Exception("El pilot ja té un pla de vol assignat en aquest interval");
            }
        }
        // Si tot és correcte, guardem el vol i sumem les hores volades al dron i al pilot
        dron.addHistorialPlaVol(plan);
        dron.addHoresVol(plan.getDuracioHores());
        pilot.addFlightPlan(plan);
        pilot.addHoresVol(plan.getDuracioHores());
        logger.info("addFlightPlan: Pla de vol afegit amb èxit: " + plan.getId());
        return plan;
    }

    // 8. Llistat de plans de vol d'un pilot
    @Override
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) throws Exception {
        logger.info("getFlightPlansByPilot: Cerca per pilotId = " + pilotId);
        Pilot pilot = this.getPilotById(pilotId);
        if (pilot == null) {
            logger.error("getFlightPlansByPilot: Pilot " + pilotId + " no trobat");
            throw new Exception("Pilot no trobat");
        }
        return pilot.getHistorialPlaVol();
    }

    // 9. Llistat de plans de vol d'un dron
    @Override
    public List<FlightPlan> getFlightPlansByDron(String dronId) throws Exception {
        logger.info("getFlightPlansByDron: Cerca per dronId = " + dronId);
        Dron dron = this.getDronById(dronId);
        if (dron == null) {
            logger.error("getFlightPlansByDron: Dron " + dronId + " no trobat");
            throw new Exception("Dron no trobat");
        }
        return dron.getHistorialPlaVol();
    }

    // Cerca de dron per ID en HashMap
    @Override
    public Dron getDronById(String id) {
        return this.drons.get(id);
    }

    // Cerca de pilot per ID en HashMap
    @Override
    public Pilot getPilotById(String id) {
        return this.pilots.get(id);
    }

    // Retorna nombre de drons registrats
    @Override
    public int numDrons() {
        return this.drons.size();
    }

    // Retorna nombre de pilots registrats
    @Override
    public int numPilots() {
        return this.pilots.size();
    }

    // Retorna nombre de drons en magatzem
    @Override
    public int numDronsMagatzem() {
        return this.magatzemManteniment.size();
    }

    @Override
    public void clear() {
        this.drons.clear();
        this.pilots.clear();
        this.magatzemManteniment.clear();
        logger.info("DronManager reiniciat.");
    }
}