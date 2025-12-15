package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// La entidad que maneja es Reserva, y su clave primaria (ID) es Long
@Repository 
public interface ReservaRepository extends CrudRepository<Reserva, Long> {

    @Query("""
    SELECT r FROM Reserva r
    WHERE r.habitacion.id = :idHabitacion
    AND r.fechaIngreso < :fechaEgreso
    AND r.fechaEgreso > :fechaIngreso
    """)
    List<Reserva> buscarReservasSolapadas(
        @Param("idHabitacion") Long idHabitacion,
        @Param("fechaIngreso") Date fechaIngreso,
        @Param("fechaEgreso") Date fechaEgreso
    );


}
