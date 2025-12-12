package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria interna

    // Atributos base
    @Column(unique = true)
    private String IDItemFactura; // ID del ítem [3]
    
    private Integer cantidad; // Cantidad del servicio/producto [3]
    private String descripcion;
    private Double precioUnitario;
    private Integer cantidadPagada; 
    
    // Relación: Muchos ítems de factura pertenecen a una sola Factura
    // Mapeo inverso de la relación OneToMany definida en Factura
    @ManyToOne 
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura; 
}
