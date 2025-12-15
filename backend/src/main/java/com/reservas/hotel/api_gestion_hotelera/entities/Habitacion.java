package com.reservas.hotel.api_gestion_hotelera.entities;

import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;
    
    @Column(nullable = false, unique = true)
    private String idHabitacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;
    
    public Habitacion(String numero, String idHabitacion, EstadoHabitacion estado, TipoHabitacion tipoHabitacion) {
        this.numero = numero;
        this.idHabitacion = idHabitacion;
        this.estado = estado;
        this.tipoHabitacion = tipoHabitacion;
    }
}