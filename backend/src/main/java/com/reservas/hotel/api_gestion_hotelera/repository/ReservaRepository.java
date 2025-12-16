package com.reservas.hotel.api_gestion_hotelera.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;

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

    @Query("""
    SELECT DISTINCT r FROM Reserva r
    LEFT JOIN r.pasajeros p
    LEFT JOIN r.responsableReserva rr
    WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
       OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))
       OR LOWER(rr.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
       OR LOWER(rr.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Reserva> buscarPorNombreHuesped(@Param("nombre") String nombre);

}
