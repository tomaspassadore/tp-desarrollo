package com.reservas.hotel.api_gestion_hotelera.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PruebaController {

    @GetMapping("/api/")
    public String prueba(){
        return "prueba";
    }
}
