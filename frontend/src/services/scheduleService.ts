import api from '@/lib/api'
import type { ScheduleType } from '@/types/scheduleType'

export interface ScheduleResponse {
  id: number
  routeId: number
  routeNumber?: string
  routeName?: string
  busId: number
  busNumber?: string
  departureTime: string
  arrivalTime: string
  daysOfWeek: string[]
  scheduleType: ScheduleType
  isActive: boolean
  validFrom?: string
  validUntil?: string
  frequency?: number
  notes?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateScheduleRequest {
  routeId: number
  busId: number
  departureTime: string
  arrivalTime: string
  daysOfWeek: string[]
  scheduleType: ScheduleType
  validFrom?: string
  validUntil?: string
  frequency?: number
  notes?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const scheduleService = {
  // Get all schedules
  getAllSchedules: async (page: number = 0, size: number = 10): Promise<PageResponse<ScheduleResponse>> => {
    const response = await api.get<PageResponse<ScheduleResponse>>('/api/schedules', {
      params: { page, size },
    })
    return response.data
  },

  // Get schedule by ID
  getScheduleById: async (id: number): Promise<ScheduleResponse> => {
    const response = await api.get<ScheduleResponse>(`/api/schedules/${id}`)
    return response.data
  },

  // Create schedule
  createSchedule: async (data: CreateScheduleRequest): Promise<ScheduleResponse> => {
    const response = await api.post<ScheduleResponse>('/api/schedules', data)
    return response.data
  },

  // Update schedule
  updateSchedule: async (id: number, data: Partial<CreateScheduleRequest>): Promise<ScheduleResponse> => {
    const response = await api.put<ScheduleResponse>(`/api/schedules/${id}`, data)
    return response.data
  },

  // Delete schedule
  deleteSchedule: async (id: number): Promise<void> => {
    await api.delete(`/api/schedules/${id}`)
  },

  // Get schedules by route
  getSchedulesByRoute: async (routeId: number, page: number = 0, size: number = 10): Promise<PageResponse<ScheduleResponse>> => {
    const response = await api.get<PageResponse<ScheduleResponse>>(`/api/schedules/route/${routeId}`, {
      params: { page, size },
    })
    return response.data
  },
}
