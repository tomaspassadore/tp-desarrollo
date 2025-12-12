"use client"

import { useState } from "react"
import { Receipt, Search, Printer } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { DashboardLayout } from "@/components/dashboard-layout"

export default function Facturar() {
  const [dni, setDni] = useState("")
  const [mostrarFactura, setMostrarFactura] = useState(false)

  const conceptos = [
    { descripcion: "Habitación 201 - Doble (3 noches)", cantidad: 3, precio: 85, total: 255 },
    { descripcion: "Servicio de habitaciones", cantidad: 2, precio: 15, total: 30 },
    { descripcion: "Minibar", cantidad: 1, precio: 25, total: 25 },
    { descripcion: "Parking", cantidad: 3, precio: 10, total: 30 },
  ]

  const subtotal = conceptos.reduce((acc, c) => acc + c.total, 0)
  const iva = subtotal * 0.21
  const total = subtotal + iva

  return (
    <DashboardLayout>
      <div className="max-w-3xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Facturar</h1>
          <p className="text-gray-600 mt-1">Genere facturas para los huéspedes</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <div className="flex gap-4 items-end">
              <div className="flex-1 space-y-2">
                <Label htmlFor="dni">DNI del Huésped</Label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    id="dni"
                    placeholder="Ingrese el DNI del huésped..."
                    className="pl-10"
                    value={dni}
                    onChange={(e) => setDni(e.target.value)}
                  />
                </div>
              </div>
              <Button className="bg-blue-600 hover:bg-blue-700" onClick={() => setMostrarFactura(true)}>
                Buscar
              </Button>
            </div>
          </CardContent>
        </Card>

        {mostrarFactura && (
          <Card className="border-gray-200">
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <Receipt className="w-5 h-5 text-blue-600" />
                    Factura
                  </CardTitle>
                  <CardDescription>Huésped: Carlos López - DNI: 87654321B</CardDescription>
                </div>
                <Button variant="outline" size="sm">
                  <Printer className="w-4 h-4 mr-2" />
                  Imprimir
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow className="bg-gray-50">
                    <TableHead className="font-medium text-gray-700">Descripción</TableHead>
                    <TableHead className="font-medium text-gray-700 text-center">Cantidad</TableHead>
                    <TableHead className="font-medium text-gray-700 text-right">Precio</TableHead>
                    <TableHead className="font-medium text-gray-700 text-right">Total</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {conceptos.map((concepto, index) => (
                    <TableRow key={index}>
                      <TableCell>{concepto.descripcion}</TableCell>
                      <TableCell className="text-center">{concepto.cantidad}</TableCell>
                      <TableCell className="text-right">{concepto.precio.toFixed(2)} €</TableCell>
                      <TableCell className="text-right">{concepto.total.toFixed(2)} €</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <div className="mt-6 border-t pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Subtotal</span>
                  <span>{subtotal.toFixed(2)} €</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">IVA (21%)</span>
                  <span>{iva.toFixed(2)} €</span>
                </div>
                <div className="flex justify-between font-bold text-lg pt-2 border-t">
                  <span>Total</span>
                  <span>{total.toFixed(2)} €</span>
                </div>
              </div>

              <Button className="w-full mt-6 bg-green-600 hover:bg-green-700">Generar Factura</Button>
            </CardContent>
          </Card>
        )}
      </div>
    </DashboardLayout>
  )
}
