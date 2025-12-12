package com.reservas.hotel.api_gestion_hotelera.service.impl;

// Importaciones para las Entidades
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.NotaDeCredito;
import com.reservas.hotel.api_gestion_hotelera.entities.Pago; 
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;

// Importaciones de Repositorios (Capa de Persistencia)
import com.reservas.hotel.api_gestion_hotelera.repository.FacturaRepository; 
import com.reservas.hotel.api_gestion_hotelera.repository.NotaDeCreditoRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PagoRepository; 

// Importaciones del Servicio y Utilidades
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; // Para manejar la lógica de negocio como una unidad atómica

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport; // Para manejar la conversión de Iterable de los repositorios

@Service
public class ContabilidadServiceImpl implements ContabilidadService {
    
    // Inyección de Repositorios (la Capa de Servicio agrupa su funcionalidad) [2, 4]
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private NotaDeCreditoRepository notaDeCreditoRepository;

    @Autowired
    private PagoRepository pagoRepository; 
    
    // ==========================================================
    // MÉTODOS DE FACTURA (CU07)
    // ==========================================================
    
    @Override
    @Transactional 
    public Factura generarFactura(Reserva reserva) {
        if (reserva == null) {
             throw new IllegalArgumentException("No se puede generar factura sin una reserva asociada.");
        }
        
        Factura factura = new Factura();
        factura.setReservaAsociada(reserva);
        
        return facturaRepository.save(factura); 
    }
    
    @Override // <--- Implementa ContabilidadService.buscarFacturaPorId(Long)
    public Optional<Factura> buscarFacturaPorId(Long id) {
        return facturaRepository.findById(id);
    }
    
    @Override // <--- Implementa ContabilidadService.buscarTodasFacturas()
    public Set<Factura> buscarTodasFacturas() {
        // Usa StreamSupport para convertir Iterable a Set
        return StreamSupport.stream(facturaRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }

    // ==========================================================
    // MÉTODOS DE NOTA DE CRÉDITO (CU19)
    // ==========================================================

    @Override
    public NotaDeCredito registrarNotaDeCredito(NotaDeCredito nota) {
        return notaDeCreditoRepository.save(nota);
    }
    
    @Override // <--- Implementa ContabilidadService.buscarNotaDeCreditoPorId(Long)
    public Optional<NotaDeCredito> buscarNotaDeCreditoPorId(Long id) {
        return notaDeCreditoRepository.findById(id);
    }

    @Override // <--- Implementa ContabilidadService.buscarTodasNotasDeCredito()
    public Set<NotaDeCredito> buscarTodasNotasDeCredito() {
        // Conversión segura de Iterable a Set
        return StreamSupport.stream(notaDeCreditoRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }

    // ==========================================================
    // MÉTODOS DE PAGO
    // ==========================================================
    
    @Override
    @Transactional 
    public Pago registrarPago(Pago pago) {
        return pagoRepository.save(pago); 
    }
    
    @Override
    public Optional<Pago> buscarPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }
    
    // NOTA: Si ContabilidadService incluye buscarTodosPagos(), también debería
    // implementarse aquí:
    /*
    @Override
    public Set<Pago> buscarTodosPagos() {
        return StreamSupport.stream(pagoRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }
    */
}