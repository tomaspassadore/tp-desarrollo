package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// La entidad que maneja es Reserva, y su clave primaria (ID) es Long
@Repository 
public interface ReservaRepository extends CrudRepository<Reserva, Long> {

    // Aquí irían métodos específicos de consulta, por ejemplo:
    // Set<Reserva> findByFechaIngresoBetween(Date inicio, Date fin);

}
