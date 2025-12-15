package com.reservas.hotel.api_gestion_hotelera.entities;

import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity; // Para @Entity, @Id, @GeneratedValue
import jakarta.persistence.GeneratedValue; // Para @Data
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Habitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria autogenerada

    private String numero;
    private String idHabitacion;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    // (Relaciones con TipoHabitacion y CostoPorNoche se añadirán luego)
}
