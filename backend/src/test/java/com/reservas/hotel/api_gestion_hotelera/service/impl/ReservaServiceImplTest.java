package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.*;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;
import com.reservas.hotel.api_gestion_hotelera.exception.ConflictoReservaException;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HabitacionService habitacionService;

    @Mock
    private ContabilidadService contabilidadService;

    @Mock
    private PasajeroRepository pasajeroRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    // ==========================================================
    // TESTS PARA: crearReserva
    // ==========================================================

    @Test
    void crearReserva_CaminoFeliz_CreaReserva() {
        // Arrange
        Date fechaIn = new Date();
        Date fechaOut = new Date(fechaIn.getTime() + 86400000); // +1 día

        Habitacion habitacionMock = new Habitacion();
        habitacionMock.setId(1L);
        habitacionMock.setEstado(EstadoHabitacion.LIBRE);

        Reserva reservaInput = new Reserva();
        reservaInput.setFechaIngreso(fechaIn);
        reservaInput.setFechaEgreso(fechaOut);
        reservaInput.setHabitacion(habitacionMock);

        when(habitacionService.buscarPorId(1L)).thenReturn(Optional.of(habitacionMock));
        // Simulamos que NO hay solapamiento (lista vacía)
        when(reservaRepository.buscarReservasSolapadas(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaInput);

        // Act
        Reserva resultado = reservaService.crearReserva(reservaInput);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.RESERVADA, habitacionMock.getEstado()); // Verifica cambio de estado
        verify(habitacionService).guardarHabitacion(habitacionMock);
        verify(reservaRepository).save(reservaInput);
    }

    @Test
    void crearReserva_FechasNulas_LanzaExcepcion() {
        Reserva reserva = new Reserva();
        // Fechas nulas por defecto

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("Las fechas de ingreso y egreso son obligatorias", ex.getMessage());
    }

    @Test
    void crearReserva_FechasInvertidas_LanzaExcepcion() {
        Reserva reserva = new Reserva();
        Date fechaIn = new Date();
        Date fechaOut = new Date(fechaIn.getTime() - 86400000); // Salida antes que entrada
        reserva.setFechaIngreso(fechaIn);
        reserva.setFechaEgreso(fechaOut);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("La fecha de ingreso debe ser anterior a la fecha de egreso", ex.getMessage());
    }

    @Test
    void crearReserva_HabitacionNoExiste_LanzaExcepcion() {
        Reserva reserva = new Reserva();
        reserva.setFechaIngreso(new Date());
        reserva.setFechaEgreso(new Date(System.currentTimeMillis() + 10000));
        Habitacion h = new Habitacion();
        h.setId(99L);
        reserva.setHabitacion(h);

        when(habitacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
    }

    @Test
    void crearReserva_HabitacionOcupada_LanzaConflicto() {
        Habitacion h = new Habitacion();
        h.setId(1L);
        h.setEstado(EstadoHabitacion.OCUPADA); // No está LIBRE

        Reserva reserva = new Reserva();
        reserva.setFechaIngreso(new Date());
        reserva.setFechaEgreso(new Date(System.currentTimeMillis() + 10000));
        reserva.setHabitacion(h);

        when(habitacionService.buscarPorId(1L)).thenReturn(Optional.of(h));

        ConflictoReservaException ex = assertThrows(ConflictoReservaException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("La habitación no está disponible", ex.getMessage());
    }

    @Test
    void crearReserva_HaySolapamiento_LanzaConflicto() {
        Habitacion h = new Habitacion();
        h.setId(1L);
        h.setEstado(EstadoHabitacion.LIBRE);

        Reserva reserva = new Reserva();
        reserva.setFechaIngreso(new Date());
        reserva.setFechaEgreso(new Date(System.currentTimeMillis() + 10000));
        reserva.setHabitacion(h);

        when(habitacionService.buscarPorId(1L)).thenReturn(Optional.of(h));
        // Simulamos que YA EXISTE una reserva en esas fechas (lista no vacía)
        when(reservaRepository.buscarReservasSolapadas(any(), any(), any())).thenReturn(List.of(new Reserva()));

        ConflictoReservaException ex = assertThrows(ConflictoReservaException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("La habitación ya está reservada en ese rango de fechas", ex.getMessage());
    }

    // ==========================================================
    // TESTS PARA: darBajaPasajero
    // ==========================================================

    @Test
    void darBajaPasajero_Exito() {
        Pasajero p = new Pasajero();
        p.setId(1L);
        p.setEstado(EstadoPasajero.ACTIVO);

        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(p));

        reservaService.darBajaPasajero(1L);

        assertEquals(EstadoPasajero.INACTIVO, p.getEstado());
        verify(pasajeroRepository).save(p);
    }

    @Test
    void darBajaPasajero_YaInactivo_LanzaExcepcion() {
        Pasajero p = new Pasajero();
        p.setId(1L);
        p.setEstado(EstadoPasajero.INACTIVO);

        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(RuntimeException.class, () -> reservaService.darBajaPasajero(1L));
        verify(pasajeroRepository, never()).save(any());
    }

    // ==========================================================
    // TESTS PARA: realizarCheckIn
    // ==========================================================

    @Test
    void realizarCheckIn_Exito() {
        Habitacion h = new Habitacion();
        h.setId(10L);
        h.setEstado(EstadoHabitacion.RESERVADA);

        Reserva r = new Reserva();
        r.setId(1L);
        r.setHabitacion(h);

        Reserva request = new Reserva();
        request.setId(1L);
        request.setHabitacion(h); // necesario para evitar null pointer en request.getHabitacion().getId()

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(habitacionService.buscarPorId(10L)).thenReturn(Optional.of(h));

        reservaService.realizarCheckIn(request);

        assertEquals(EstadoHabitacion.OCUPADA, h.getEstado());
        verify(habitacionService).guardarHabitacion(h);
    }

    @Test
    void realizarCheckIn_HabitacionNoReservada_Falla() {
        Habitacion h = new Habitacion();
        h.setId(10L);
        h.setEstado(EstadoHabitacion.LIBRE); // Debería estar RESERVADA

        Reserva r = new Reserva();
        r.setId(1L);
        r.setHabitacion(h);
        
        // Simulamos request
        Reserva req = new Reserva(); 
        req.setId(1L);
        req.setHabitacion(h);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(habitacionService.buscarPorId(10L)).thenReturn(Optional.of(h));

        assertThrows(ConflictoReservaException.class, () -> reservaService.realizarCheckIn(req));
    }

    // ==========================================================
    // TESTS PARA: modificarReserva
    // ==========================================================

    @Test
    void modificarReserva_Exito() {
        Reserva existente = new Reserva();
        existente.setId(1L);
        
        Reserva nuevosDatos = new Reserva();
        nuevosDatos.setFechaIngreso(new Date());
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(reservaRepository.save(existente)).thenReturn(existente);

        reservaService.modificarReserva(1L, nuevosDatos);

        verify(reservaRepository).save(existente);
    }

    // ==========================================================
    // TESTS PARA: facturar
    // ==========================================================

    @Test
    void facturar_Exito() {
        Habitacion h = new Habitacion();
        h.setEstado(EstadoHabitacion.OCUPADA);
        
        Reserva r = new Reserva();
        r.setId(1L);
        r.setHabitacion(h);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(contabilidadService.generarFactura(r)).thenReturn(new Factura());

        Factura resultado = reservaService.facturar(1L);
        assertNotNull(resultado);
    }

    @Test
    void facturar_ReservaInactiva_Falla() {
        Habitacion h = new Habitacion();
        h.setEstado(EstadoHabitacion.LIBRE); // Estado inválido para facturar
        
        Reserva r = new Reserva();
        r.setId(1L);
        r.setHabitacion(h);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reservaService.facturar(1L));
        assertEquals("No se puede facturar una reserva inactiva", ex.getMessage());
    }

    // ==========================================================
    // TESTS PARA: cancelarReserva
    // ==========================================================

    @Test
    void cancelarReserva_Exito() {
        Habitacion h = new Habitacion();
        h.setEstado(EstadoHabitacion.RESERVADA);
        
        Reserva r = new Reserva();
        r.setId(1L);
        r.setHabitacion(h);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        reservaService.cancelarReserva(1L);

        assertEquals(EstadoHabitacion.LIBRE, h.getEstado()); // Verifica que liberó la habitación
        verify(habitacionService).guardarHabitacion(h);
        verify(reservaRepository).delete(r);
    }

    // ==========================================================
    // TESTS PARA: Búsquedas (Simples delegaciones)
    // ==========================================================

    @Test
    void buscarTodas_Exito() {
        when(reservaRepository.findAll()).thenReturn(Arrays.asList(new Reserva(), new Reserva()));
        Set<Reserva> resultado = reservaService.buscarTodas();
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorId_Exito() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(new Reserva()));
        assertTrue(reservaService.buscarPorId(1L).isPresent());
    }
}

