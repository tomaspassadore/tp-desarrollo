package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero; // Si usas Set para colecciones, como en el ejemplo de las fuentes [6]

public interface PasajeroService {
    
    Optional<Pasajero> buscarPorId(Long id); // Usamos Optional para el manejo de posibles ausencias [6]
    Set<Pasajero> buscarTodos();
    
    void darDeBajaPasajero(Long id); 
    
    Pasajero registrarPasajero(Pasajero pasajero);

    Pasajero actualizarPasajero(Long id, Pasajero pasajero);
    
    // Buscar huéspedes por criterio (dni, nombre o apellido)
    List<Pasajero> buscarHuesped(String criterio, String valor);
}
