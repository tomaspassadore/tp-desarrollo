package com.reservas.hotel.api_gestion_hotelera.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Pasajero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String nroDocumento;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.util.Date fechaDeNacimiento;
    
    private String nacionalidad;
    private String telefono;
    private String ocupacion;

    @Column(nullable = true)
    private String cuit;
    
    @Column(nullable = true)
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;
    
    @Enumerated(EnumType.STRING)
    private EstadoPasajero estado;

}
