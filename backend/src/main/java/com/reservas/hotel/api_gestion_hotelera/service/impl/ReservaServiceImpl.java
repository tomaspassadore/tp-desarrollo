package com.reservas.hotel.api_gestion_hotelera.service.impl;

// Importaciones de Entidades
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.Factura; // Entidad Factura [4]

// Importaciones de Contratos y Repositorios
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;

// Importaciones de Spring y Utilidades
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Estereotipo de la Capa de Servicio
import org.springframework.transaction.annotation.Transactional; // Para lógica transaccional
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReservaServiceImpl implements ReservaService {

    // Inyección de Dependencias: Conexión con la persistencia
    @Autowired
    private ReservaRepository reservaRepository;

    // Inyección para colaboración con otros servicios (ej. para facturación CU07)
    @Autowired
    private ContabilidadService contabilidadService; 
    
    // ==========================================================
    // MÉTODOS DE MANIPULACIÓN (POST/PUT/DELETE)
    // ==========================================================

    /**
     * Implementa CU04: Crea y guarda una nueva reserva.
     */
    @Override
    @Transactional
    public Reserva crearReserva(Reserva nuevaReserva) {
        // Lógica de negocio 1: Implementación de reglas y restricciones [1] (ej. verificar disponibilidad)
        // ...
        
        // Delegación a la capa de persistencia [5]
        return reservaRepository.save(nuevaReserva); 
    }

    /**
     * Inicia el proceso de check-in para una reserva existente.
     */
    @Override
    @Transactional
    public Reserva realizarCheckIn(Reserva reserva) {
        // Lógica de negocio 2: Actualizar estado de la reserva y/o habitación a OCUPADA [4]
        // ...
        return reservaRepository.save(reserva);
    }
    
    /**
     * Modifica los datos de una reserva existente.
     */
    @Override
    @Transactional
    public Reserva modificarReserva(Long id, Reserva datosActualizados) {
        // 1. Buscar la reserva actual (asegurando que exista)
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // 2. Aplicar las modificaciones (ej. cambiar fechas, ocupación)
        reservaExistente.setFechaIngreso(datosActualizados.getFechaIngreso());
        reservaExistente.setFechaEgreso(datosActualizados.getFechaEgreso());
        // ... setear otros atributos según la lógica
        
        // 3. Guardar y delegar la persistencia
        return reservaRepository.save(reservaExistente);
    }

    /**
     * Implementa CU07: Procesa la facturación final de una reserva.
     */
    @Override
    @Transactional
    public Factura facturar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede facturar: Reserva no encontrada"));

        // Lógica de negocio 3: Generar la factura y actualizar el estado
        // Delega la creación de la Factura al ContabilidadService (CU07)
        Factura facturaGenerada = contabilidadService.generarFactura(reserva);
        
        // Actualizar el estado de la reserva/habitacion, etc.
        // ...
        
        return facturaGenerada;
    }

    /**
     * Implementa CU06: Cancela una reserva (lógica de negocio).
     */
    @Override
    @Transactional
    public void cancelarReserva(Long id) {
        // Lógica de negocio 4: Validar si se puede cancelar (ej. no debe estar ya facturada)
        // ...
        
        reservaRepository.deleteById(id);
        // Lógica 5: La habitación asociada debería volver a estado LIBRE o RESERVADA (si hay una reserva siguiente) [4]
    }
    
    /**
     * Implementa CU11: Da de baja a un huésped/pasajero asociado a la reserva.
     */
    @Override
    @Transactional
    public void darBajaHuesped(Long idHuesped) {
        // Lógica de negocio 6: Aquí iría la lógica específica para desvincular o eliminar al pasajero
        // Podría implicar buscar reservas activas y verificar si el huésped es el responsable.
        // ...
    }

    // ==========================================================
    // MÉTODOS DE CONSULTA (GET)
    // ==========================================================

    /**
     * Implementa CU05: Busca y devuelve todas las reservas.
     */
    @Override
    public Set<Reserva> buscarTodas() {
        // Convierte el Iterable devuelto por CrudRepository.findAll() en un Set [6]
        return StreamSupport.stream(reservaRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    /**
     * Busca una reserva específica por ID.
     */
    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }
}
