package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*; // Paquete estándar de JPA (o javax.persistence)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // Clave primaria e identificador (requerido por JPA) [2]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // Atributos base de la reserva [5]
    private Date fechaIngreso;
    private Date fechaEgreso; 

    // Relaciones (Mapeos básicos asumidos basados en el diagrama) [5]
    
    // Una reserva pertenece a una habitación (Habitacion)
    @ManyToOne 
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion; 

    // Una reserva tiene uno o más pasajeros (Pasajero)
    @ManyToMany
    // Definimos cómo se mapea la tabla intermedia
    @JoinTable(
        name = "reserva_pasajero",
        joinColumns = @JoinColumn(name = "reserva_id"),
        inverseJoinColumns = @JoinColumn(name = "pasajero_id")
    )
    private Set<Pasajero> pasajeros;

    // Relación con la persona que paga/hace la reserva (ResponsableReserva)
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private ResponsableReserva responsableReserva; 

    // ... aquí irían las relaciones con Pago, Factura (si no es 1-1 bidireccional), etc.
}
