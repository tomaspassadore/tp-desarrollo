package com.reservas.hotel.api_gestion_hotelera.service;

// Importaciones necesarias
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.Factura; // Necesaria para el método facturar

import java.util.Set;
import java.util.Optional;

// Esta es la interfaz que define el contrato de la capa de servicio para Reservas
public interface ReservaService {

    // Métodos de POST (Creación/Inicio)
    // 1. CU04: Reservar habitación
    Reserva crearReserva(Reserva nuevaReserva); 
    
    // 2. Realizar Check-In (asumiendo que se recibe la Reserva a actualizar)
    Reserva realizarCheckIn(Reserva reserva); 

    // 3. Modificar una reserva existente (Necesario para el endpoint PUT)
    Reserva modificarReserva(Long id, Reserva datosActualizados);

    // Métodos de GET (Consulta)
    // 4. Buscar todas las reservas (necesario para la colección)
    Set<Reserva> buscarTodas();
    
    // 5. Buscar reserva por ID
    Optional<Reserva> buscarPorId(Long id);

    // Métodos de Operaciones Financieras / Mantenimiento
    // 6. CU07: Facturar
    Factura facturar(Long id); // Retorna la Factura generada.

    // 7. CU06/CU11: Cancelar o dar de baja (Asumiendo que el controlador lo delega aquí)
    void cancelarReserva(Long id);

    // 8. CU11: Dar de baja huésped (asumiendo que se procesa a través de la reserva)
    void darBajaHuesped(Long idHuesped); 
}