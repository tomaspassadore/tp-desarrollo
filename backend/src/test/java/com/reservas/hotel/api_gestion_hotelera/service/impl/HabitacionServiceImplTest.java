package com.reservas.hotel.api_gestion_hotelera.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.repository.HabitacionRepository;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceImplTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionServiceImpl habitacionService;

    // ==========================================================
    // TESTS PARA: actualizarEstado
    // ==========================================================

    @Test
    void actualizarEstado_IdExiste_ActualizaYGuarda() {
        // Arrange
        Long id = 1L;
        EstadoHabitacion nuevoEstado = EstadoHabitacion.EN_MANTENIMIENTO;
        
        Habitacion habitacionExistente = new Habitacion();
        habitacionExistente.setId(id);
        habitacionExistente.setEstado(EstadoHabitacion.LIBRE); // Estado original

        when(habitacionRepository.findById(id)).thenReturn(Optional.of(habitacionExistente));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacionExistente);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(id, nuevoEstado);

        // Assert
        assertNotNull(resultado);
        assertEquals(nuevoEstado, resultado.getEstado()); // Verificamos que cambió el estado
        verify(habitacionRepository).save(habitacionExistente); // Verificamos que se guardó
    }

    @Test
    void actualizarEstado_IdNoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 99L;
        when(habitacionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            habitacionService.actualizarEstado(id, EstadoHabitacion.OCUPADA);
        });

        assertEquals("Habitación no encontrada", ex.getMessage());
        verify(habitacionRepository, never()).save(any());
    }

    // ==========================================================
    // TESTS PARA: mostrarPorEstado (Lógica de Stream/Filter)
    // ==========================================================

    @Test
    void mostrarPorEstado_FiltraCorrectamenteLaLista() {
        // Arrange
        Habitacion h1 = new Habitacion();
        h1.setId(1L);
        h1.setEstado(EstadoHabitacion.LIBRE);

        Habitacion h2 = new Habitacion();
        h2.setId(2L);
        h2.setEstado(EstadoHabitacion.OCUPADA);

        Habitacion h3 = new Habitacion();
        h3.setId(3L);
        h3.setEstado(EstadoHabitacion.LIBRE);

        // Simulamos que el repositorio devuelve TODAS (mezcladas)
        List<Habitacion> todasLasHabitaciones = Arrays.asList(h1, h2, h3);
        when(habitacionRepository.findAll()).thenReturn(todasLasHabitaciones);

        // Act: Pedimos solo las LIBRES
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.LIBRE);

        // Assert
        assertEquals(2, resultado.size(), "Debería encontrar 2 habitaciones libres");
        assertTrue(resultado.contains(h1));
        assertTrue(resultado.contains(h3));
        assertFalse(resultado.contains(h2), "No debería incluir la habitación ocupada");
    }

    // ==========================================================
    // TESTS PARA: Métodos simples (Delegación)
    // ==========================================================

    @Test
    void buscarPorId_DelegacionCorrecta() {
        Habitacion h = new Habitacion();
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(h));

        Optional<Habitacion> res = habitacionService.buscarPorId(1L);
        assertTrue(res.isPresent());
    }

    @Test
    void buscarDisponibles_LlamaRepoConEstadoLibre() {
        when(habitacionRepository.findByEstado(EstadoHabitacion.LIBRE))
            .thenReturn(Arrays.asList(new Habitacion()));

        List<Habitacion> res = habitacionService.buscarDisponibles();

        assertFalse(res.isEmpty());
        verify(habitacionRepository).findByEstado(EstadoHabitacion.LIBRE);
    }

    @Test
    void buscarTodas_ConvierteASet() {
        when(habitacionRepository.findAll()).thenReturn(Arrays.asList(new Habitacion(), new Habitacion()));

        Set<Habitacion> res = habitacionService.buscarTodas();
        
        assertEquals(2, res.size());
    }

    @Test
    void guardarHabitacion_DelegacionCorrecta() {
        Habitacion h = new Habitacion();
        when(habitacionRepository.save(h)).thenReturn(h);

        Habitacion res = habitacionService.guardarHabitacion(h);
        
        assertNotNull(res);
        verify(habitacionRepository).save(h);
    }
}