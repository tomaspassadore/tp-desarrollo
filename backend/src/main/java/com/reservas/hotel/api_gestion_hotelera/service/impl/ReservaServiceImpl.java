package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;

import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private ContabilidadService contabilidadService;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    // ==========================================================
    // CU04 - Reservar habitación
    // ==========================================================
    @Override
    @Transactional
    public Reserva crearReserva(Reserva nuevaReserva) {

        if (nuevaReserva.getFechaIngreso() == null || nuevaReserva.getFechaEgreso() == null) {
            throw new RuntimeException("Las fechas de ingreso y egreso son obligatorias");
        }

        if (!nuevaReserva.getFechaIngreso().before(nuevaReserva.getFechaEgreso())) {
            throw new RuntimeException("La fecha de ingreso debe ser anterior a la fecha de egreso");
        }

        Long idHabitacion = nuevaReserva.getHabitacion().getId();

        Habitacion habitacion = habitacionService.buscarPorId(idHabitacion)
                .orElseThrow(() -> new RuntimeException("La habitación no existe"));

        if (habitacion.getEstado() != EstadoHabitacion.LIBRE) {
            throw new RuntimeException(
                    "La habitación no está disponible. Estado actual: " + habitacion.getEstado()
            );
        }

        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        habitacionService.guardarHabitacion(habitacion);

        nuevaReserva.setHabitacion(habitacion);

        return reservaRepository.save(nuevaReserva);
    }

    // ==========================================================
    // CU11 - Baja lógica de PASAJERO
    // ==========================================================
    @Override
    @Transactional
    public void darBajaPasajero(Long idPasajero) {

        Pasajero pasajero = pasajeroRepository.findById(idPasajero)
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado"));

        pasajero.setEstado(EstadoPasajero.INACTIVO);

        pasajeroRepository.save(pasajero);
    }

    // ==========================================================
    // Otros métodos
    // ==========================================================

    @Override
    @Transactional
    public Reserva realizarCheckIn(Reserva reserva) {
        Habitacion habitacion = reserva.getHabitacion();
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionService.guardarHabitacion(habitacion);
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva modificarReserva(Long id, Reserva datosActualizados) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setFechaIngreso(datosActualizados.getFechaIngreso());
        reserva.setFechaEgreso(datosActualizados.getFechaEgreso());

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Factura facturar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return contabilidadService.generarFactura(reserva);
    }

    @Override
    @Transactional
    public void cancelarReserva(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Habitacion habitacion = reserva.getHabitacion();

        habitacion.setEstado(EstadoHabitacion.LIBRE);
        habitacionService.guardarHabitacion(habitacion);

        reservaRepository.delete(reserva);
    }

    @Override
    public Set<Reserva> buscarTodas() {
        return StreamSupport.stream(reservaRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }
}
