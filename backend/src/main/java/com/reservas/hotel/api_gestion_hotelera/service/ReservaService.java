package com.reservas.hotel.api_gestion_hotelera.service;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;

import java.util.Set;
import java.util.Optional;

public interface ReservaService {

    // CU04: Reservar habitación
    Reserva crearReserva(Reserva nuevaReserva); 
    
    // Check-in
    Reserva realizarCheckIn(Reserva reserva); 

    // Modificar reserva
    Reserva modificarReserva(Long id, Reserva datosActualizados);

    // Consultas
    Set<Reserva> buscarTodas();
    
    Optional<Reserva> buscarPorId(Long id);

    // CU07: Facturar
    Factura facturar(Long id);

    // CU06: Cancelar reserva
    void cancelarReserva(Long id);

    // CU11: Dar de baja Huesped
    void darBajaPasajero(Long idPasajero);
}
