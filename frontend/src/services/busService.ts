import api from '@/lib/api'
import type { BusStatus } from '@/types/busStatus'

export interface BusResponse {
  id: number
  busNumber: string
  licensePlate: string
  model: string
  manufacturer: string
  year: number
  capacity: number
  seatingCapacity?: number
  standingCapacity?: number
  status: BusStatus
  hasWifi?: boolean
  hasAirConditioning?: boolean
  isAccessible: boolean
  hasGPS?: boolean
  lastMaintenanceDate?: string
  nextMaintenanceDate?: string
  notes?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateBusRequest {
  busNumber: string
  licensePlate: string
  model: string
  manufacturer: string
  year: number
  capacity: number
  seatingCapacity?: number
  standingCapacity?: number
  status: BusStatus
  hasWifi?: boolean
  hasAirConditioning?: boolean
  isAccessible?: boolean
  hasGPS?: boolean
  notes?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const busService = {
  // Get all buses
  getAllBuses: async (page: number = 0, size: number = 10): Promise<PageResponse<BusResponse>> => {
    const response = await api.get<PageResponse<BusResponse>>('/api/buses', {
      params: { page, size },
    })
    return response.data
  },

  // Get bus by ID
  getBusById: async (id: number): Promise<BusResponse> => {
    const response = await api.get<BusResponse>(`/api/buses/${id}`)
    return response.data
  },

  // Create bus
  createBus: async (data: CreateBusRequest): Promise<BusResponse> => {
    const response = await api.post<BusResponse>('/api/buses', data)
    return response.data
  },

  // Update bus
  updateBus: async (id: number, data: Partial<CreateBusRequest>): Promise<BusResponse> => {
    const response = await api.put<BusResponse>(`/api/buses/${id}`, data)
    return response.data
  },

  // Delete bus
  deleteBus: async (id: number): Promise<void> => {
    await api.delete(`/api/buses/${id}`)
  },

  // Search buses (backend uses Pageable, not individual page/size params in search)
  searchBuses: async (keyword: string, page: number = 0, size: number = 10, sort: string = 'busNumber,asc'): Promise<PageResponse<BusResponse>> => {
    const response = await api.get<PageResponse<BusResponse>>('/api/buses/search', {
      params: { 
        keyword, 
        page, 
        size,
        sort
      },
    })
    return response.data
  },

  // Get buses by status
  getBusesByStatus: async (status: string, page: number = 0, size: number = 10): Promise<PageResponse<BusResponse>> => {
    const response = await api.get<PageResponse<BusResponse>>(`/api/buses/status/${status}`, {
      params: { page, size },
    })
    return response.data
  },

  // Get available buses (non-paginated list)
  getAvailableBuses: async (): Promise<BusResponse[]> => {
    const response = await api.get<BusResponse[]>('/api/buses/available')
    return response.data
  },

  // Update bus status
  updateBusStatus: async (id: number, status: string): Promise<void> => {
    await api.patch(`/api/buses/${id}/status`, null, {
      params: { status },
    })
  },

  // Get bus by bus number
  getBusByNumber: async (busNumber: string): Promise<BusResponse> => {
    const response = await api.get<BusResponse>(`/api/buses/number/${busNumber}`)
    return response.data
  },
}

