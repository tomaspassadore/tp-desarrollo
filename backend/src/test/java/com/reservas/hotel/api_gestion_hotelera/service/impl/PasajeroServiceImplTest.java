package com.reservas.hotel.api_gestion_hotelera.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas.hotel.api_gestion_hotelera.entities.Direccion;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;
import com.reservas.hotel.api_gestion_hotelera.repository.DireccionRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;

@ExtendWith(MockitoExtension.class)
class PasajeroServiceImplTest {

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private PasajeroServiceImpl pasajeroService;

    // ==========================================================
    // TESTS PARA: registrarPasajero
    // ==========================================================

    @Test
    void registrarPasajero_EstadoNuloYConDireccion_AsignaActivoYGuardaDireccion() {
        // Arrange
        Pasajero nuevoPasajero = new Pasajero();
        nuevoPasajero.setEstado(null); // Caso: Estado null (debe entrar al primer if)
        
        Direccion direccion = new Direccion();
        direccion.setCalle("Av. Siempreviva");
        nuevoPasajero.setDireccion(direccion); // Caso: Con dirección (debe entrar al segundo if)

        // Simulamos que al guardar la dirección, devuelve la misma con ID
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);
        // Simulamos el guardado del pasajero
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(nuevoPasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(nuevoPasajero);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPasajero.ACTIVO, resultado.getEstado(), "Debe asignar estado ACTIVO por defecto");
        verify(direccionRepository).save(direccion); // Verificamos que se guardó la dirección
        verify(pasajeroRepository).save(nuevoPasajero);
    }

    @Test
    void registrarPasajero_EstadoYaAsignadoYSinDireccion_NoCambiaEstadoNiGuardaDireccion() {
        // Arrange
        Pasajero nuevoPasajero = new Pasajero();
        nuevoPasajero.setEstado(EstadoPasajero.INACTIVO); // Caso: Estado NO null (no entra al primer if)
        nuevoPasajero.setDireccion(null); // Caso: Sin dirección (no entra al segundo if)

        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(nuevoPasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(nuevoPasajero);

        // Assert
        assertEquals(EstadoPasajero.INACTIVO, resultado.getEstado(), "No debe cambiar el estado si ya existía");
        verify(direccionRepository, never()).save(any()); // Verificamos que NO se llamó al repo de direcciones
        verify(pasajeroRepository).save(nuevoPasajero);
    }

    // ==========================================================
    // TESTS PARA: buscarHuesped (Switch Case)
    // ==========================================================

    @Test
    void buscarHuesped_CriterioDni_BuscaPorDni() {
        when(pasajeroRepository.buscarPorDni("12345")).thenReturn(Collections.emptyList());

        pasajeroService.buscarHuesped("dni", "12345");

        verify(pasajeroRepository).buscarPorDni("12345");
    }

    @Test
    void buscarHuesped_CriterioNombre_BuscaPorNombre() {
        when(pasajeroRepository.buscarPorNombre("Juan")).thenReturn(Collections.emptyList());

        // Probamos con mayúsculas/minúsculas mezcladas para verificar el toLowerCase()
        pasajeroService.buscarHuesped("NoMbRe", "Juan");

        verify(pasajeroRepository).buscarPorNombre("Juan");
    }

    @Test
    void buscarHuesped_CriterioApellido_BuscaPorApellido() {
        when(pasajeroRepository.buscarPorApellido("Perez")).thenReturn(Collections.emptyList());

        pasajeroService.buscarHuesped("apellido", "Perez");

        verify(pasajeroRepository).buscarPorApellido("Perez");
    }

    @Test
    void buscarHuesped_CriterioInvalido_LanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pasajeroService.buscarHuesped("email", "juan@test.com");
        });

        assertTrue(exception.getMessage().contains("Criterio de búsqueda no válido"));
    }

    // ==========================================================
    // TESTS PARA: buscarPorId, buscarTodos, darDeBaja
    // ==========================================================

    @Test
    void buscarPorId_DelegacionCorrecta() {
        Pasajero p = new Pasajero();
        p.setId(1L);
        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Pasajero> resultado = pasajeroService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void buscarTodos_ConvierteIterableASet() {
        Pasajero p1 = new Pasajero();
        Pasajero p2 = new Pasajero();
        List<Pasajero> lista = Arrays.asList(p1, p2);

        when(pasajeroRepository.findAll()).thenReturn(lista);

        Set<Pasajero> resultado = pasajeroService.buscarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void darDeBajaPasajero_DelegacionCorrecta() {
        Long idEliminar = 5L;
        
        // Ejecutamos el método void
        pasajeroService.darDeBajaPasajero(idEliminar);

        // Verificamos que se haya llamado al deleteById con el ID correcto
        verify(pasajeroRepository).deleteById(idEliminar);
    }
}