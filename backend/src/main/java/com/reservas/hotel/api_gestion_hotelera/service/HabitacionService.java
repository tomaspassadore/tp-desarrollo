package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.Date;
import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion; 
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import java.util.Optional;
import java.util.Set; 

public interface HabitacionService {
    
    // Contrato Básico (CRUD)
    Optional<Habitacion> buscarPorId(String id);
    Set<Habitacion> buscarTodas();
    Habitacion guardarHabitacion(Habitacion habitacion); // Para mantenimiento o registro inicial
    
    // Lógica de Negocio (Ejemplo de CU05)
    /**
     * CU05: Muestra todas las habitaciones con un estado específico.
     * @param estado EstadoHabitacion (LIBRE, OCUPADA, RESERVADA, EN MANTENIMIENTO)
     */
    Set<Habitacion> mostrarPorEstado(EstadoHabitacion estado); // El estado de la habitación es un <<Enum>> [5]
    
    // Lógica de Negocio (Ejemplo de CU04: Verificación de disponibilidad)
    void verificarDisponibilidad(String idHabitacion, Date fechaIngreso, Date fechaEgreso);

    // Operación de mantenimiento
    Habitacion actualizarEstado(String id, EstadoHabitacion nuevoEstado);
}
