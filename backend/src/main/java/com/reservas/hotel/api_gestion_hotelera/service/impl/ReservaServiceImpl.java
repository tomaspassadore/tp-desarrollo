package com.reservas.hotel.api_gestion_hotelera.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;
import com.reservas.hotel.api_gestion_hotelera.exception.ConflictoReservaException;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;

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
            throw new ConflictoReservaException("La habitación no está disponible");
        }

        //VALIDACIÓN DE SOLAPAMIENTO
        List<Reserva> reservasSolapadas = reservaRepository.buscarReservasSolapadas(
                idHabitacion,
                nuevaReserva.getFechaIngreso(),
                nuevaReserva.getFechaEgreso()
        );

        if (!reservasSolapadas.isEmpty()) {
            throw new ConflictoReservaException("La habitación ya está reservada en ese rango de fechas");
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

        if (pasajero.getEstado() == EstadoPasajero.INACTIVO) {
            throw new RuntimeException("El pasajero ya se encuentra dado de baja");
        }

        pasajero.setEstado(EstadoPasajero.INACTIVO);
        pasajeroRepository.save(pasajero);
    }


    // ==========================================================
    // Otros métodos
    // ==========================================================

    @Override
    @Transactional
    public Reserva realizarCheckIn(Reserva reservaRequest) {

        Reserva reserva = reservaRepository.findById(reservaRequest.getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Habitacion habitacion = habitacionService.buscarPorId(
                reserva.getHabitacion().getId()
        ).orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        if (habitacion.getEstado() != EstadoHabitacion.RESERVADA) {
            throw new ConflictoReservaException(
            "No se puede hacer check-in de una habitación que no está reservada"
            );
        }
        
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionService.guardarHabitacion(habitacion);

        reserva.setHabitacion(habitacion);

        return reserva;
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

        EstadoHabitacion estadoHabitacion = reserva.getHabitacion().getEstado();

        if (estadoHabitacion == EstadoHabitacion.LIBRE ||
            estadoHabitacion == EstadoHabitacion.EN_MANTENIMIENTO) {

            throw new RuntimeException("No se puede facturar una reserva inactiva");
        }

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
