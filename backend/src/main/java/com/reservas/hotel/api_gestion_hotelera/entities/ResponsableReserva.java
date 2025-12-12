package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*; // Paquete estándar de JPA
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Provee getters, setters, toString, etc. [3]
@NoArgsConstructor
@AllArgsConstructor
public class ResponsableReserva {

    // 1. Clave primaria (requerida por JPA/Hibernate)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Atributos según el diseño de clases [1]
    private String nombre;
    private String apellido;
    private String telefono;

    // Nota: Aunque el diagrama muestra solo estos tres atributos [1],
    // si el responsable de la reserva es el mismo que uno de los pasajeros 
    // y quieres mantener consistencia de datos (ej. CUIT, email), 
    // podrías considerar usar una relación @OneToOne con Pasajero. 
    // Por ahora, usamos solo los atributos definidos.
}
