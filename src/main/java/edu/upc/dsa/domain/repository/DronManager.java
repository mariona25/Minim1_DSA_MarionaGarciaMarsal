package edu.upc.dsa.domain.repository;
import edu.upc.dsa.domain.models.Pilot;
import edu.upc.dsa.domain.models.Dron;
import edu.upc.dsa.domain.models.FlightPlan;

import java.util.List;

public interface DronManager {

    // 1. Afegir un nou dron
    public Dron addDron(Dron dron);
    public Dron addDron(String nom, String fabricant, String model);
    public Dron addDron(String id, String nom, String fabricant, String model);

    // 2. Afegir un nou pilot
    public Pilot addPilot(Pilot pilot);
    public Pilot addPilot(String nom, String cognom);
    public Pilot addPilot(String id, String nom, String cognom);


    // 3. Llistat de drons ordenats descendentment per hores de vol
    public List<Dron> getDronsByHours();

    // 4. Llistat de pilots ordenats descendentment per hores de vol
    public List<Pilot> getPilotsByHours();

    // 5. Guardar un dron al magatzem de manteniment (s'apila al fons, LIFO)
    public Dron guardarDronEnMagatzem(String dronId) throws Exception;

    // 6. Reparar/manteniment del dron més proper a la porta (LIFO - Pop de la Pila)
    public Dron repararDron() throws Exception;

    // 7. Crear un plan de vol (comprova solapaments i si el dron està en manteniment)
    public FlightPlan addFlightPlan(FlightPlan plan) throws Exception;

    // 8. Llistat de plans de vol d'un pilot
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) throws Exception;

    // 9. Llistat de plans de vol d'un dron
    public List<FlightPlan> getFlightPlansByDron(String dronId) throws Exception;


    // Mètodes auxiliars i de consulta (útils per a JUnit)
    public Dron getDronById(String id);
    public Pilot getPilotById(String id);
    public int numDrons();
    public int numPilots();
    public int numDronsMagatzem();
    public void clear();

}
