package com.reservas.hotel.api_gestion_hotelera.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.hotel.api_gestion_hotelera.entities.Direccion;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.repository.DireccionRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import com.reservas.hotel.api_gestion_hotelera.service.PasajeroService;

@Service
public class PasajeroServiceImpl implements PasajeroService {

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Override
    public Optional<Pasajero> buscarPorId(Long id) {
        return pasajeroRepository.findById(id);
    }

    @Override
    public Set<Pasajero> buscarTodos() {
        return StreamSupport.stream(pasajeroRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void darDeBajaPasajero(Long id) {
        pasajeroRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Pasajero registrarPasajero(Pasajero pasajero) {
        // Si el pasajero tiene una dirección, guardarla primero
        if (pasajero.getDireccion() != null) {
            Direccion direccionGuardada = direccionRepository.save(pasajero.getDireccion());
            pasajero.setDireccion(direccionGuardada);
        }
        // Guardar el pasajero con la dirección asociada
        return pasajeroRepository.save(pasajero);
    }

    /**
     * Busca huéspedes por un criterio específico (dni, nombre o apellido)
     * @param criterio El tipo de búsqueda: "dni", "nombre" o "apellido"
     * @param valor El valor a buscar
     * @return Lista de pasajeros encontrados
     */
    @Override
    public List<Pasajero> buscarHuesped(String criterio, String valor) {
        return switch (criterio.toLowerCase()) {
            case "dni" -> pasajeroRepository.buscarPorDni(valor);
            case "nombre" -> pasajeroRepository.buscarPorNombre(valor);
            case "apellido" -> pasajeroRepository.buscarPorApellido(valor);
            default -> throw new IllegalArgumentException("Criterio de búsqueda no válido. Debe ser 'dni', 'nombre' o 'apellido'");
        };
    }
}

