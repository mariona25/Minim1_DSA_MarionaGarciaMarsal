Mínim 1_MarionaGarciaMarsal

Estat Actual
El projecte és totalment funcional i compleix els requisits:
- Singleton (DronManagerImpl):
  - Entitats: Dron, Pilot, FlightPlan, Posició. 
  - Cerca eficient O(1) amb HashMap. 
  - Magatzem de manteniment LIFO (LastInFirstOut) amb Stack. 
  - Llistat de drons i pilot ordenat per hores de vol. 
- Proves (DronsManagerTest):
  - Coberta l'estat inicial i les 4 operacions principals. 
  - Validació d'excepcions (per exemple, evitar vols amb drons en manteniment).
- Servei REST (DronService / Main):
  - Servidor Grizzly a http://localhost:8080/dsaApp/
  - Endpoints REST actius amb codis HTTP (200, 201, 400, 404, 500). 
  - Documentat amb Swagger a http://localhost:8080/swagger/ i comprovat a Postman.

Errors Solucionats
- Deserialització de dates REST: Resolt passant les dates en format ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ) via JSON a Postman.




