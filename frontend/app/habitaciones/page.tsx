"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { DashboardLayout } from "@/components/dashboard-layout"

const habitaciones = [
  { numero: "101", tipo: "Individual", estado: "ocupada", huesped: "María García" },
  { numero: "102", tipo: "Individual", estado: "libre", huesped: null },
  { numero: "103", tipo: "Individual", estado: "mantenimiento", huesped: null },
  { numero: "201", tipo: "Doble", estado: "ocupada", huesped: "Carlos López" },
  { numero: "202", tipo: "Doble", estado: "libre", huesped: null },
  { numero: "203", tipo: "Doble", estado: "ocupada", huesped: "Ana Martínez" },
  { numero: "301", tipo: "Suite", estado: "libre", huesped: null },
  { numero: "302", tipo: "Suite", estado: "ocupada", huesped: "Pedro Sánchez" },
  { numero: "401", tipo: "Suite Premium", estado: "libre", huesped: null },
  { numero: "402", tipo: "Suite Premium", estado: "reservada", huesped: null },
]

const getEstadoColor = (estado: string) => {
  switch (estado) {
    case "libre":
      return "bg-green-100 text-green-700"
    case "ocupada":
      return "bg-red-100 text-red-700"
    case "reservada":
      return "bg-yellow-100 text-yellow-700"
    case "mantenimiento":
      return "bg-gray-100 text-gray-700"
    default:
      return "bg-gray-100 text-gray-700"
  }
}

export default function EstadoHabitaciones() {
  const libres = habitaciones.filter((h) => h.estado === "libre").length
  const ocupadas = habitaciones.filter((h) => h.estado === "ocupada").length
  const reservadas = habitaciones.filter((h) => h.estado === "reservada").length
  const mantenimiento = habitaciones.filter((h) => h.estado === "mantenimiento").length

  return (
    <DashboardLayout>
      <div className="max-w-5xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Estado de Habitaciones</h1>
          <p className="text-gray-600 mt-1">Vista general del estado de todas las habitaciones</p>
        </div>

        {/* Resumen */}
        <div className="grid grid-cols-4 gap-4 mb-8">
          <Card className="border-gray-200">
            <CardContent className="p-4 text-center">
              <div className="text-2xl font-bold text-green-600">{libres}</div>
              <div className="text-sm text-gray-600">Libres</div>
            </CardContent>
          </Card>
          <Card className="border-gray-200">
            <CardContent className="p-4 text-center">
              <div className="text-2xl font-bold text-red-600">{ocupadas}</div>
              <div className="text-sm text-gray-600">Ocupadas</div>
            </CardContent>
          </Card>
          <Card className="border-gray-200">
            <CardContent className="p-4 text-center">
              <div className="text-2xl font-bold text-yellow-600">{reservadas}</div>
              <div className="text-sm text-gray-600">Reservadas</div>
            </CardContent>
          </Card>
          <Card className="border-gray-200">
            <CardContent className="p-4 text-center">
              <div className="text-2xl font-bold text-gray-600">{mantenimiento}</div>
              <div className="text-sm text-gray-600">Mantenimiento</div>
            </CardContent>
          </Card>
        </div>

        {/* Grid de habitaciones */}
        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg">Todas las Habitaciones</CardTitle>
            <CardDescription>Haga clic en una habitación para ver más detalles</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-5 gap-4">
              {habitaciones.map((habitacion) => (
                <Card
                  key={habitacion.numero}
                  className="border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
                >
                  <CardContent className="p-4">
                    <div className="text-lg font-bold text-gray-900 mb-1">{habitacion.numero}</div>
                    <div className="text-xs text-gray-500 mb-2">{habitacion.tipo}</div>
                    <Badge className={getEstadoColor(habitacion.estado)}>{habitacion.estado}</Badge>
                    {habitacion.huesped && (
                      <div className="text-xs text-gray-600 mt-2 truncate">{habitacion.huesped}</div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
