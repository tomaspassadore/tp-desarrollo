package com.reservas.hotel.api_gestion_hotelera.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set; // <-- USADO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;      // <-- USADO
import org.springframework.web.bind.annotation.DeleteMapping;  // <-- USADO
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;                          // <-- USADO
import org.springframework.web.bind.annotation.PutMapping;                     // <-- USADO
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;

// @RestController es una versión especializada de @Controller que incluye @ResponseBody [2, 3]
@RestController
@RequestMapping("/api/reservas") // Define la URL base del recurso
public class ReservaController {
    
    // Inyección de la dependencia de la Capa de Servicio [4]
    @Autowired 
    private ReservaService reservaService; // <-- USADO (La variable ya no tiene el warning)
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // ==========================================================
    // 1. POST: CREAR RECURSO (CU04: Reservar habitación) [5]
    // Mínimo 2 Endpoints de cada tipo requerido [6]
    // ==========================================================

    @PostMapping
    // @RequestBody mapea el JSON de la petición a un objeto Java [7]
    public ResponseEntity<Reserva> crearReserva(@RequestBody Map<String, Object> requestMap) {
        try {
            // Extraer el DNI del pasajero si viene en el request
            String dniPasajero = requestMap.containsKey("dniPasajero") 
                ? (String) requestMap.get("dniPasajero") 
                : null;
            
            // Extraer y validar datos del request
            if (!requestMap.containsKey("fechaIngreso") || !requestMap.containsKey("fechaEgreso") 
                || !requestMap.containsKey("habitacion")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Extraer número de habitación
            @SuppressWarnings("unchecked")
            Map<String, Object> habitacionMap = (Map<String, Object>) requestMap.get("habitacion");
            if (habitacionMap == null || !habitacionMap.containsKey("numero")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Object numeroObj = habitacionMap.get("numero");
            Integer numeroHabitacion;
            if (numeroObj instanceof String) {
                try {
                    numeroHabitacion = Integer.parseInt((String) numeroObj);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else if (numeroObj instanceof Integer) {
                numeroHabitacion = (Integer) numeroObj;
            } else if (numeroObj instanceof Number) {
                numeroHabitacion = ((Number) numeroObj).intValue();
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Crear Habitacion temporal solo con el número (el servicio la buscará completa)
            Habitacion habitacionTemporal = new Habitacion();
            habitacionTemporal.setNumero(numeroHabitacion);
            
            // Convertir fechas de String a Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            
            Date fechaIngreso;
            Date fechaEgreso;
            
            try {
                Object fechaIngresoObj = requestMap.get("fechaIngreso");
                if (fechaIngresoObj instanceof String) {
                    fechaIngreso = dateFormat.parse((String) fechaIngresoObj);
                } else if (fechaIngresoObj instanceof Date) {
                    fechaIngreso = (Date) fechaIngresoObj;
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                
                Object fechaEgresoObj = requestMap.get("fechaEgreso");
                if (fechaEgresoObj instanceof String) {
                    fechaEgreso = dateFormat.parse((String) fechaEgresoObj);
                } else if (fechaEgresoObj instanceof Date) {
                    fechaEgreso = (Date) fechaEgresoObj;
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Crear Reserva manualmente
            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setFechaIngreso(fechaIngreso);
            nuevaReserva.setFechaEgreso(fechaEgreso);
            nuevaReserva.setHabitacion(habitacionTemporal);
            
            // Llama a la lógica de negocio (Servicio) con el DNI
            Reserva reservaGuardada = reservaService.crearReserva(nuevaReserva, dniPasajero); 
            // Retorna el recurso creado con código HTTP 201 CREATED
            return new ResponseEntity<>(reservaGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<Reserva> realizarCheckIn(@RequestBody Reserva reserva) {
        // Endpoint secundario de POST
        Reserva reservaActualizada = reservaService.realizarCheckIn(reserva); 
        return new ResponseEntity<>(reservaActualizada, HttpStatus.OK);
    }
    
    // ==========================================================
    // 2. GET: CONSULTAR RECURSOS (CU05: Mostrar disponibilidad)
    // ==========================================================

    // Endpoint 1 de GET: Obtener todas las reservas (Colección)
    @GetMapping 
    public ResponseEntity<Set<Reserva>> obtenerTodasReservas() {
        Set<Reserva> reservas = reservaService.buscarTodas(); // Llama al servicio
        return new ResponseEntity<>(reservas, HttpStatus.OK); 
    }

    // Endpoint 2 de GET: Buscar reservas por nombre de huésped/responsable
    @GetMapping("/buscar")
    public ResponseEntity<List<Reserva>> buscarReservasPorNombre(@RequestParam("nombre") String nombre) {
        List<Reserva> reservas = reservaService.buscarPorNombreHuesped(nombre);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Endpoint 3 de GET: Buscar una reserva por ID (Recurso único)
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
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
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
