package com.reservas.hotel.api_gestion_hotelera.controller;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva; // <-- USADO
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;      // <-- USADO
import org.springframework.http.ResponseEntity;  // <-- USADO
import org.springframework.web.bind.annotation.*;
import java.util.Set;                          // <-- USADO
import java.util.Optional;                     // <-- USADO

// @RestController es una versión especializada de @Controller que incluye @ResponseBody [2, 3]
@RestController
@RequestMapping("/api/reservas") // Define la URL base del recurso
public class ReservaController {
    
    // Inyección de la dependencia de la Capa de Servicio [4]
    @Autowired 
    private ReservaService reservaService; // <-- USADO (La variable ya no tiene el warning)
    
    // ==========================================================
    // 1. POST: CREAR RECURSO (CU04: Reservar habitación) [5]
    // Mínimo 2 Endpoints de cada tipo requerido [6]
    // ==========================================================

    @PostMapping
    // @RequestBody mapea el JSON de la petición a un objeto Java [7]
    public ResponseEntity<Reserva> crearReserva(@RequestBody Reserva nuevaReserva) {
        // Llama a la lógica de negocio (Servicio)
        Reserva reservaGuardada = reservaService.crearReserva(nuevaReserva); 
        // Retorna el recurso creado con código HTTP 201 CREATED
        return new ResponseEntity<>(reservaGuardada, HttpStatus.CREATED); 
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<Reserva> realizarCheckIn(@RequestBody Reserva reserva) {
        // Endpoint secundario de POST
        Reserva reservaActualizada = reservaService.realizarCheckIn(reserva); 
        return new ResponseEntity<>(reservaActualizada, HttpStatus.OK);
    }
    
    // ==========================================================
    // 2. GET: CONSULTAR RECURSOS (CU05: Mostrar disponibilidad) [5]
    // ==========================================================

    // Endpoint 1 de GET: Obtener todas las reservas (Colección)
    @GetMapping 
    public ResponseEntity<Set<Reserva>> obtenerTodasReservas() {
        Set<Reserva> reservas = reservaService.buscarTodas(); // Llama al servicio
        return new ResponseEntity<>(reservas, HttpStatus.OK); 
    }

    // Endpoint 2 de GET: Buscar una reserva por ID (Recurso único)
    // @PathVariable mapea el ID de la URL (ej. /api/reservas/123) al parámetro del método [7]
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.buscarPorId(id);
        
        // Manejo de la respuesta: 200 OK si existe, 404 NOT FOUND si no [7]
        return reserva.map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // ==========================================================
    // 3. DELETE: ELIMINAR RECURSOS (CU06: Cancelar reserva) [5]
    // ==========================================================

    // Endpoint 1 de DELETE: Cancelar una reserva (CU06)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id); // Llama al servicio [8]
        // Se puede devolver 200 OK, o 204 No Content
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    // Endpoint 2 de DELETE: Dar de baja huésped (CU11) [5] (Suponiendo que se delega a un servicio)
    @DeleteMapping("/huesped/{idHuesped}")
    public ResponseEntity<Void> darBajaHuesped(@PathVariable Long idHuesped) {
        // En un escenario real, esto llamaría a HuéspedService.darBaja(idHuesped)
        // Usaremos el servicio de reserva solo para ejemplificar el endpoint DELETE.
        reservaService.darBajaHuesped(idHuesped); // Asumimos este método en ReservaService
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content es común para DELETE
    }
    
    // ==========================================================
    // 4. PUT: MODIFICAR RECURSOS (CU07: Facturar) [5]
    // ==========================================================
    
    // 1. PUT: Facturar una reserva (CU07)
// EL TIPO DE RETORNO CAMBIA A Factura
@PutMapping("/facturar/{id}")
public ResponseEntity<Factura> facturarReserva(@PathVariable Long id) {
    
    // EL TIPO DE LA VARIABLE DE CAPTURA CAMBIA A Factura
    Factura facturaGenerada = reservaService.facturar(id); 

    // Retornamos el objeto Factura con HTTP 200 OK
    return new ResponseEntity<>(facturaGenerada, HttpStatus.OK);
}
    
    // Endpoint 2 de PUT: Modificar reserva (general)
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> modificarReserva(@PathVariable Long id, @RequestBody Reserva datosActualizados) {
        // Este método actualiza completamente la entidad
        Reserva reservaModificada = reservaService.modificarReserva(id, datosActualizados);
        return new ResponseEntity<>(reservaModificada, HttpStatus.OK);
    }

}
