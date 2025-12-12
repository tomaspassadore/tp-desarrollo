package com.reservas.hotel.api_gestion_hotelera.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;

// El segundo tipo debe ser String, si el ID de Habitacion es String
@Repository 
public interface HabitacionRepository extends CrudRepository<Habitacion, String> { 
    // ...
}