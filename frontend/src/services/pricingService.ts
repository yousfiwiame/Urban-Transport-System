import api from '@/lib/api'

export interface RoutePricing {
  id: number
  routeId: number
  basePrice: number
  peakHourPrice?: number
  weekendPrice?: number
  currency: string
  active: boolean
  description?: string
}

export interface CreatePricingRequest {
  routeId: number
  basePrice: number
  peakHourPrice?: number
  weekendPrice?: number
  description?: string
}

export const pricingService = {
  // Get pricing for a specific route
  getPricingByRoute: async (routeId: number): Promise<RoutePricing> => {
    const response = await api.get<RoutePricing>(`/api/routes/pricing/route/${routeId}`)
    return response.data
  },

  // Calculate price for a route at a specific date/time
  calculatePrice: async (routeId: number, dateTime?: string): Promise<number> => {
    const params = dateTime ? { dateTime } : {}
    const response = await api.get<number>(`/api/routes/pricing/route/${routeId}/calculate`, { params })
    return response.data
  },

  // Create or update pricing (admin only)
  createOrUpdatePricing: async (request: CreatePricingRequest): Promise<RoutePricing> => {
    const response = await api.post<RoutePricing>('/api/routes/pricing', request)
    return response.data
  },

  // Delete pricing (admin only)
  deletePricing: async (id: number): Promise<void> => {
    await api.delete(`/api/routes/pricing/${id}`)
  },
}

