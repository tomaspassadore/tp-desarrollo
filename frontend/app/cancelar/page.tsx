"use client"

import { useState } from "react"
import { CalendarX, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { DashboardLayout } from "@/components/dashboard-layout"

const reservas = [
  {
    id: "R001",
    huesped: "María García",
    habitacion: "101",
    entrada: "2025-06-25",
    salida: "2025-06-28",
    estado: "confirmada",
  },
  {
    id: "R002",
    huesped: "Carlos López",
    habitacion: "205",
    entrada: "2025-06-26",
    salida: "2025-06-30",
    estado: "confirmada",
  },
  {
    id: "R003",
    huesped: "Ana Martínez",
    habitacion: "302",
    entrada: "2025-06-27",
    salida: "2025-06-29",
    estado: "pendiente",
  },
  {
    id: "R004",
    huesped: "Pedro Sánchez",
    habitacion: "108",
    entrada: "2025-06-28",
    salida: "2025-07-02",
    estado: "confirmada",
  },
]

export default function CancelarReserva() {
  const [busqueda, setBusqueda] = useState("")

  const reservasFiltradas = reservas.filter(
    (r) =>
      r.huesped.toLowerCase().includes(busqueda.toLowerCase()) || r.id.toLowerCase().includes(busqueda.toLowerCase()),
  )

  return (
    <DashboardLayout>
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Cancelar Reserva</h1>
          <p className="text-gray-600 mt-1">Busque y cancele reservas existentes</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <div className="flex gap-4">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  placeholder="Buscar por ID de reserva o nombre del huésped..."
                  className="pl-10"
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg flex items-center gap-2">
              <CalendarX className="w-5 h-5 text-red-600" />
              Reservas Activas
            </CardTitle>
            <CardDescription>{reservasFiltradas.length} reserva(s) encontrada(s)</CardDescription>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50">
                  <TableHead className="font-medium text-gray-700">ID</TableHead>
                  <TableHead className="font-medium text-gray-700">Huésped</TableHead>
                  <TableHead className="font-medium text-gray-700">Habitación</TableHead>
                  <TableHead className="font-medium text-gray-700">Entrada</TableHead>
                  <TableHead className="font-medium text-gray-700">Salida</TableHead>
                  <TableHead className="font-medium text-gray-700">Estado</TableHead>
                  <TableHead className="font-medium text-gray-700">Acción</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {reservasFiltradas.map((reserva) => (
                  <TableRow key={reserva.id} className="hover:bg-gray-50">
                    <TableCell className="font-mono">{reserva.id}</TableCell>
                    <TableCell className="font-medium">{reserva.huesped}</TableCell>
                    <TableCell>{reserva.habitacion}</TableCell>
                    <TableCell>{reserva.entrada}</TableCell>
                    <TableCell>{reserva.salida}</TableCell>
                    <TableCell>
                      <Badge
                        className={
                          reserva.estado === "confirmada"
                            ? "bg-green-100 text-green-700"
                            : "bg-yellow-100 text-yellow-700"
                        }
                      >
                        {reserva.estado}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Button variant="destructive" size="sm">
                        Cancelar
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
