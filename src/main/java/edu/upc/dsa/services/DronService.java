package edu.upc.dsa.services;

import edu.upc.dsa.domain.models.*;
import edu.upc.dsa.domain.repository.DronManager;
import edu.upc.dsa.domain.repository.DronManagerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/drons", description = "Endpoint per al Servei de Drons i Plans de Vol")
@Path("/drons")
public class DronService {

    private DronManager dm;

    public DronService() {
        this.dm = DronManagerImpl.getInstance();

        // Si el manager està buit, afegim dades inicials de prova per a Swagger
        if (this.dm.numDrons() == 0) {
            // 1. Afegim drons de prova
            this.dm.addDron("D001", "Phantom", "DJI", "Pro 4");
            this.dm.addDron("D002", "Mavic", "DJI", "Air 2");

            // 2. Afegim pilots de prova
            this.dm.addPilot("P001", "Joan", "Garcia");
            this.dm.addPilot("P002", "Laura", "Martínez");
        }
    }

    // 1. POST /drons/magatzem/{dronId} - Guardar un dron al magatzem de manteniment
    @POST
    @ApiOperation(value = "Guardar dron al magatzem", notes = "Guarda un dron al magatzem per a manteniment (s'apila al fons)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dron afegit al magatzem correctament", response = Dron.class),
            @ApiResponse(code = 404, message = "Dron no trobat"),
            @ApiResponse(code = 400, message = "El dron ja està en manteniment")
    })
    @Path("/magatzem/{dronId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardarDronEnMagatzem(@PathParam("dronId") String dronId) {
        try {
            Dron dron = this.dm.guardarDronEnMagatzem(dronId);
            return Response.status(200).entity(dron).build();
        } catch (Exception e) {
            if (e.getMessage().contains("no trobat")) {
                return Response.status(404).entity(e.getMessage()).build();
            } else {
                return Response.status(400).entity(e.getMessage()).build();
            }
        }
    }

    // 2. POST /drons/magatzem/reparar - Reparar el dron més proper a la porta (LIFO)
    @POST
    @ApiOperation(value = "Reparar dron", notes = "Repara el dron més proper a la porta del magatzem (LIFO) i el torna a posar operatiu")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dron reparat correctament", response = Dron.class),
            @ApiResponse(code = 404, message = "No hi ha drons al magatzem")
    })
    @Path("/magatzem/reparar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response repararDron() {
        try {
            Dron reparat = this.dm.repararDron();
            return Response.status(200).entity(reparat).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    // 3. POST /drons/plans - Crear un nou pla de vol
    @POST
    @ApiOperation(value = "Crear un pla de vol", notes = "Crea un nou pla de vol assignat a un dron i un pilot")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Pla de vol creat amb èxit", response = FlightPlan.class),
            @ApiResponse(code = 404, message = "Dron o Pilot no trobat"),
            @ApiResponse(code = 400, message = "Error de solapament d'horari o dron en manteniment"),
            @ApiResponse(code = 500, message = "Dades d'entrada no vàlides")
    })
    @Path("/plans")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlightPlan(FlightPlan plan) {
        if (plan == null || plan.getDronId() == null || plan.getPilotId() == null) {
            return Response.status(500).build();
        }
        try {
            FlightPlan nouPla = this.dm.addFlightPlan(plan);
            return Response.status(201).entity(nouPla).build();
        } catch (Exception e) {
            if (e.getMessage().contains("no trobat")) {
                return Response.status(404).entity(e.getMessage()).build();
            } else {
                return Response.status(400).entity(e.getMessage()).build();
            }
        }
    }

    // 4. GET /drons/plans/pilots/{pilotId} - Llistat de plans de vol d'un pilot
    @GET
    @ApiOperation(value = "Obtenir plans de vol d'un pilot", notes = "Retorna la llista de plans de vol assignats a un pilot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Èxit", response = FlightPlan.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Pilot no trobat")
    })
    @Path("/plans/pilots/{pilotId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightPlansByPilot(@PathParam("pilotId") String pilotId) {
        try {
            List<FlightPlan> plans = this.dm.getFlightPlansByPilot(pilotId);
            GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(plans) {};
            return Response.status(200).entity(entity).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    // AFEGIT DESPRÉS DEL 05/11/2026 (DIA MÍNIM CLASSE)
    // He acabat l'API REST perquè tingui totes les funcionalitats descrites a l'enunciat.

    // 5. POST /drons - Afegir un nou dron
    @POST
    @ApiOperation(value = "Afegir un nou dron", notes = "Crea o actualitza un dron a la flota")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Dron creat amb èxit", response = Dron.class),
            @ApiResponse(code = 400, message = "Dades de dron no vàlides")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDron(Dron dron) {
        if (dron == null) return Response.status(400).build();
        Dron d = this.dm.addDron(dron);
        return Response.status(201).entity(d).build();
    }

    // 6. POST /drons/pilots - Afegir un nou pilot
    @POST
    @ApiOperation(value = "Afegir un nou pilot", notes = "Crea o actualitza un pilot al sistema")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Pilot creat amb èxit", response = Pilot.class),
            @ApiResponse(code = 400, message = "Dades de pilot no vàlides")
    })
    @Path("/pilots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPilot(Pilot pilot) {
        if (pilot == null) return Response.status(400).build();
        Pilot p = this.dm.addPilot(pilot);
        return Response.status(201).entity(p).build();
    }

    // 7. GET /drons/plans/drons/{dronId} - Obtenir plans de vol d'un dron
    @GET
    @ApiOperation(value = "Obtenir plans de vol d'un dron", notes = "Retorna la llista de plans de vol assignats a un dron")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Èxit", response = FlightPlan.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Dron no trobat")
    })
    @Path("/plans/drons/{dronId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightPlansByDron(@PathParam("dronId") String dronId) {
        try {
            List<FlightPlan> plans = this.dm.getFlightPlansByDron(dronId);
            GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(plans) {};
            return Response.status(200).entity(entity).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    // 8. GET /drons/hores - Llistat de drons ordenats per hores de vol
    @GET
    @ApiOperation(value = "Obtenir drons ordenats per hores de vol", notes = "Retorna el llistat de drons ordenats descendentment per hores")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Èxit", response = Dron.class, responseContainer = "List")
    })
    @Path("/hores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDronsByHours() {
        List<Dron> drons = this.dm.getDronsByHours();
        GenericEntity<List<Dron>> entity = new GenericEntity<List<Dron>>(drons) {};
        return Response.status(200).entity(entity).build();
    }

    // 9. GET /drons/pilots/hores - Llistat de pilots ordenats per hores de vol
    @GET
    @ApiOperation(value = "Obtenir pilots ordenats per hores de vol", notes = "Retorna el llistat de pilots ordenats descendentment per hores")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Èxit", response = Pilot.class, responseContainer = "List")
    })
    @Path("/pilots/hores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilotsByHours() {
        List<Pilot> pilots = this.dm.getPilotsByHours();
        GenericEntity<List<Pilot>> entity = new GenericEntity<List<Pilot>>(pilots) {};
        return Response.status(200).entity(entity).build();
    }
}