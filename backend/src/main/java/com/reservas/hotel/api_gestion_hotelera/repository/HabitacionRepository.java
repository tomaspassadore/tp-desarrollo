package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends CrudRepository<Habitacion, Long> {
}
