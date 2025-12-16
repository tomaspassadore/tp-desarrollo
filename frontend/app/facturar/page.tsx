"use client"

import { useMemo, useState } from "react"
import { Receipt, Search, Printer } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { DashboardLayout } from "@/components/dashboard-layout"
import { apiFetch } from "@/lib/api/fetch"
import { toast } from "@/components/ui/use-toast"

type ApiPasajero = {
  id?: number
  nombre?: string
  apellido?: string
  nroDocumento?: string
}

type ApiHabitacion = {
  id?: number
  numero?: string
  tipoHabitacion?: {
    nombre?: string
    costoPorNoche?: number | string
  }
}

type ApiResponsableReserva = {
  id?: number
  nombre?: string
  apellido?: string
  nroDocumento?: string
}

type ApiReserva = {
  id?: number
  fechaIngreso?: string | number
  fechaEgreso?: string | number
  habitacion?: ApiHabitacion
  pasajeros?: ApiPasajero[]
  responsableReserva?: ApiResponsableReserva
}

type Concepto = {
  descripcion: string
  cantidad: number
  precio: number
  total: number
}

function formatDate(value: unknown) {
  if (!value) return "-"
  const d = new Date(value as any)
  if (Number.isNaN(d.getTime())) return "-"
  return d.toISOString().slice(0, 10)
}

function diffNoches(desde?: string | number, hasta?: string | number) {
  if (!desde || !hasta) return 0
  const d1 = new Date(desde as any)
  const d2 = new Date(hasta as any)
  if (Number.isNaN(d1.getTime()) || Number.isNaN(d2.getTime())) return 0
  const msPorDia = 1000 * 60 * 60 * 24
  const diff = Math.round((d2.getTime() - d1.getTime()) / msPorDia)
  return Math.max(diff, 0)
}

function parsePrecio(value?: number | string) {
  if (typeof value === "number") return value
  if (typeof value === "string") {
    const parsed = parseFloat(value)
    return Number.isFinite(parsed) ? parsed : 0
  }
  return 0
}

export default function Facturar() {
  const [dni, setDni] = useState("")
  const [reservas, setReservas] = useState<ApiReserva[]>([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const reservaSeleccionada = reservas[0]

  const conceptos: Concepto[] = useMemo(() => {
    if (!reservaSeleccionada) return []
    const noches = diffNoches(reservaSeleccionada.fechaIngreso, reservaSeleccionada.fechaEgreso)
    const precioNoche = parsePrecio(reservaSeleccionada.habitacion?.tipoHabitacion?.costoPorNoche)
    const desc = `Habitación ${reservaSeleccionada.habitacion?.numero ?? "-"} - ${reservaSeleccionada.habitacion?.tipoHabitacion?.nombre ?? "-"
      } (${noches} noche${noches === 1 ? "" : "s"})`
    const totalLinea = noches * precioNoche
    return [
      {
        descripcion: desc,
        cantidad: noches || 1,
        precio: precioNoche,
        total: totalLinea || precioNoche,
      },
    ]
  }, [reservaSeleccionada])

  const subtotal = useMemo(() => conceptos.reduce((acc, c) => acc + c.total, 0), [conceptos])
  const iva = useMemo(() => subtotal * 0.21, [subtotal])
  const total = useMemo(() => subtotal + iva, [subtotal, iva])

  const handleBuscar = async () => {
    const dniTrim = dni.trim()
    if (!dniTrim) {
      toast({
        title: "DNI requerido",
        description: "Ingresá el DNI del huésped para buscar su reserva.",
        variant: "destructive",
      })
      return
    }

    try {
      setCargando(true)
      setError(null)
      const data = await apiFetch<ApiReserva[]>(`/reservas/buscar-por-dni?dni=${encodeURIComponent(dniTrim)}`, {
        method: "GET",
      })
      if (!data || data.length === 0) {
        setReservas([])
        toast({
          title: "Sin resultados",
          description: "No se encontraron reservas para ese DNI.",
          variant: "destructive",
        })
        return
      }
      setReservas(data)
    } catch (err) {
      console.error("Error al buscar reservas por DNI:", err)
      const msg = err instanceof Error ? err.message : "Error al buscar reservas"
      setError(msg)
      toast({
        title: "Error",
        description: msg,
        variant: "destructive",
      })
    } finally {
      setCargando(false)
    }
  }

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
              <Button className="bg-blue-600 hover:bg-blue-700" onClick={handleBuscar} disabled={cargando}>
                {cargando ? "Buscando..." : "Buscar"}
              </Button>
            </div>
          </CardContent>
        </Card>

        {error && <div className="text-red-600 mb-4">{error}</div>}

        {reservaSeleccionada && (
          <Card className="border-gray-200">
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <Receipt className="w-5 h-5 text-blue-600" />
                    Factura
                  </CardTitle>
                  <CardDescription>
                    Huésped:{" "}
                    {reservaSeleccionada.responsableReserva?.nombre ??
                      reservaSeleccionada.pasajeros?.[0]?.nombre ??
                      "-"}{" "}
                    {reservaSeleccionada.responsableReserva?.apellido ??
                      reservaSeleccionada.pasajeros?.[0]?.apellido ??
                      "-"}{" "}
                    - DNI:{" "}
                    {reservaSeleccionada.responsableReserva?.nroDocumento ??
                      reservaSeleccionada.pasajeros?.[0]?.nroDocumento ??
                      "-"}
                  </CardDescription>
                  <p className="text-sm text-gray-600">
                    Estadia: {formatDate(reservaSeleccionada.fechaIngreso)} a {formatDate(reservaSeleccionada.fechaEgreso)}
                  </p>
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
                      <TableCell className="text-right">${concepto.precio.toFixed(2)}</TableCell>
                      <TableCell className="text-right">${concepto.total.toFixed(2)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <div className="mt-6 border-t pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Subtotal</span>
                  <span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">IVA (21%)</span>
                  <span>${iva.toFixed(2)}</span>
                </div>
                <div className="flex justify-between font-bold text-lg pt-2 border-t">
                  <span>Total</span>
                  <span>${total.toFixed(2)}</span>
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
