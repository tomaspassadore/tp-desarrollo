// Peticiones relacionadas con huéspedes
import { apiFetch } from "./fetch"

export type Criterio = "dni" | "nombre" | "apellido"

export type BuscarHuespedRequest = {
  criterio: Criterio
  valor: string
}

export type Direccion = {
  id?: number
  calle: string
  numero: string
  departamento: string
  piso: string
  codigoPostal: string
  localidad: string
  provincia: string
  pais: string
}

export type Huesped = {
  id?: number
  nombre: string
  apellido: string
  dni?: string // Puede venir como dni o nroDocumento del backend
  nroDocumento?: string // Campo del backend
  telefono?: string
  email?: string // Opcional
  cuit?: string // Opcional
  fechaNacimiento?: string // Puede venir como fechaNacimiento o fechaDeNacimiento
  fechaDeNacimiento?: string // Campo del backend
  nacionalidad?: string
  ocupacion?: string
  direccion?: Direccion
}

export type BuscarHuespedResponse = Huesped[]

export function buscarHuesped(payload: BuscarHuespedRequest) {
  return apiFetch<BuscarHuespedResponse>("/pasajeros/buscar", {
    method: "POST",
    json: payload,
  })
}

export function darAltaHuesped(payload: Huesped) {
  return apiFetch("/pasajeros/dar-alta", {
    method: "POST",
    json: payload,
  })
}