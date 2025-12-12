package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion; 
import com.reservas.hotel.api_gestion_hotelera.repository.HabitacionRepository;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport; // IMPORTACIÓN CLAVE

@Service
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Override
    // El argumento es String, lo que ahora coincide con CrudRepository<Habitacion, String>
    public Optional<Habitacion> buscarPorId(String id) {
        return habitacionRepository.findById(id); 
    }
    
    @Override
    public Set<Habitacion> buscarTodas() {
        // Corrección 1: Usamos StreamSupport para convertir Iterable a Stream
        return StreamSupport.stream(habitacionRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }

    @Override
    public Habitacion guardarHabitacion(Habitacion habitacion) {
        // Delegamos al repositorio. Spring Data se encarga de saber si es insert o update.
        return habitacionRepository.save(habitacion); 
    }
    
    // Lógica de Negocio: Mostrar estado/disponibilidad (CU05)
    @Override
    public Set<Habitacion> mostrarPorEstado(EstadoHabitacion estado) {
        // Corrección 2: Usamos StreamSupport para permitir el método stream() y filtrar
        return StreamSupport.stream(habitacionRepository.findAll().spliterator(), false)
                .filter(h -> h.getEstado().equals(estado))
                .collect(Collectors.toSet());
    }

    // Se mantiene la firma pero la lógica requiere acceso a las reservas
    @Override
    public void verificarDisponibilidad(String idHabitacion, Date fechaIngreso, Date fechaEgreso) {
        // Lógica: Necesita acceso al Repositorio de Reservas para verificar solapamiento de fechas.
        // ... (Implementación de la lógica de negocio pendiente)
    }
    
    @Override
    public Habitacion actualizarEstado(String id, EstadoHabitacion nuevoEstado) {
        // Uso del ID de tipo String
        Habitacion habitacion = habitacionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Habitación con ID " + id + " no encontrada.")); 
        
        // Asumiendo que setEstado(EstadoHabitacion) fue corregido en la entidad Habitacion
        habitacion.setEstado(nuevoEstado); 
        
        return habitacionRepository.save(habitacion);
    }
}
