package com.reservas.hotel.api_gestion_hotelera.controller;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    /**
     * CU05 - Obtener todas las habitaciones
     */
    @GetMapping
    public ResponseEntity<Set<Habitacion>> obtenerTodas() {
        Set<Habitacion> habitaciones = habitacionService.buscarTodas();
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    /**
     * CU05 - Obtener habitaciones por estado
     * Ejemplo: /api/habitaciones/estado/LIBRE
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Set<Habitacion>> obtenerPorEstado(@PathVariable String estado) {
        try {
            EstadoHabitacion estadoHabitacion = EstadoHabitacion.valueOf(estado.toUpperCase());
            Set<Habitacion> habitaciones = habitacionService.mostrarPorEstado(estadoHabitacion);
            return new ResponseEntity<>(habitaciones, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
