Mínim 1_MarionaGarciaMarsal

DESCRIPCIÓ DEL PROJECTE: GESTIÓ DE FLOTA DE DRONS

Aquest projecte consisteix en el desenvolupament d'un sistema de gestió per a una flota de drons, pilots i plans de vol, utilitzant Java i l'arquitectura RESTful sobre Jersey/Grizzly. 
El sistema està dissenyat seguint el patró Singleton.

1. Funcionalitats Principals
- Guardar dron al magatzem: Envia un dron a la zona de manteniment i el marca com a no operatiu.
- Reparar dron (LIFO): Repara el dron més proper a la porta del magatzem utilitzant una estructura de pila (Stack), retornant-lo a l'estat operatiu.
- Crear pla de vol: Assigna una ruta geogràfica i una durada a un dron i un pilot específics, validant que el dron no estigui en manteniment i actualitzant les hores acumulades de vol.
- Consultar plans per pilot: Retorna la llista completa de plans de vol assignats a un pilot concret.
- Consultar plans per dron: Retorna la llista completa de plans de vol assignats a un dron concret.
- Llistat per hores de vol: Ordena els drons i pilots de forma descendent segons les hores totals volades.

2. Arquitectura i Components
- Classes: Format per Dron, Pilot, FlightPlan i Posició.
- Capa de Logica (DronManagerImpl): Gestor centralitzat implementat com a Singleton. Utilitza HashMap per a cerques per ID i Stack per a la gestió LIFO del magatzem.
- Capa REST (DronService / Main): Exposa les operacions mitjançant endpoints HTTP/JSON documentats amb Swagger i verificats amb Postman.
- Capa de Proves (DronsManagerTest): Proves automàtiques amb JUnit per validar 4 de les funcionalitats del codi.

Estat Actual
El projecte és totalment funcional i compleix els requisits:
- Singleton (DronManagerImpl):
  - Entitats: Dron, Pilot, FlightPlan, Posició. 
  - Cerca eficient O(1) amb HashMap. 
  - Magatzem de manteniment LIFO (LastInFirstOut) amb Stack. 
  - Llistat de drons i pilot ordenat per hores de vol. 
- Proves (DronsManagerTest):
  - Valida l'estat inicial i 4 funcionalitats del codi. 
  - Validació d'excepcions (per exemple, evitar vols amb drons en manteniment).
- Servei REST (DronService / Main):
  - Servidor Grizzly a http://localhost:8080/dsaApp/
  - Endpoints REST actius amb codis HTTP (200, 201, 400, 404, 500). 
  - Documentat amb Swagger a http://localhost:8080/swagger/ i comprovat a Postman.

Errors Solucionats
- Deserialització de dates REST (quan es vol afegir pla de vol via postman): Resolt passant les dates en format ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ) via JSON a Postman.




