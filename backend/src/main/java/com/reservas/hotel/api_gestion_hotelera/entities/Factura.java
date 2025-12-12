package com.reservas.hotel.api_gestion_hotelera.entities;

import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Usamos EnumType.STRING para guardar el nombre del Enum en la BD
    @Enumerated(EnumType.STRING)
    private TipoFactura tipo; // <<Enum>> TipoFactura [7]

    private Double importeTotal; // Usamos Double para Real, aunque BigDecimal es común para dinero [5]
    private Date fechaDeEmision; // [5]

    // Relación con la Reserva (0..1) [5]
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reservaAsociada;
    
    // Relación con los items/servicios facturados (ItemFactura) [7]
    @OneToMany(mappedBy = "factura")
    private Set<ItemFactura> items; 

    // ... otros atributos y relaciones

}
