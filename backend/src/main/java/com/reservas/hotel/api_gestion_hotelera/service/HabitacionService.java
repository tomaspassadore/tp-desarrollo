package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

public interface HabitacionService {

    Optional<Habitacion> buscarPorId(Long id);

    Set<Habitacion> buscarTodas();

    Habitacion guardarHabitacion(Habitacion habitacion);

    Set<Habitacion> mostrarPorEstado(EstadoHabitacion estado);

    void verificarDisponibilidad(Long idHabitacion, Date fechaIngreso, Date fechaEgreso);

    Habitacion actualizarEstado(Long id, EstadoHabitacion nuevoEstado);
}
