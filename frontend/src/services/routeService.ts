import api from '@/lib/api'

export interface RouteResponse {
  id: number
  routeNumber: string
  routeName: string
  description?: string
  origin: string
  destination: string
  distance: number
  estimatedDuration: number
  isActive: boolean
  isCircular: boolean
  color?: string
  numberOfStops?: number
  createdAt?: string
  updatedAt?: string
}

export interface RouteStopResponse {
  id: number
  stopId: number
  stopName: string
  stopCode: string
  sequenceNumber: number
  distanceFromStart?: number
  estimatedTime?: number
}

export interface RouteDetailsResponse {
  id: number
  routeNumber: string
  routeName: string
  description?: string
  origin: string
  destination: string
  distance: number
  estimatedDuration: number
  isActive: boolean
  isCircular: boolean
  color?: string
  numberOfStops: number
  stops: RouteStopResponse[]
  createdAt?: string
  updatedAt?: string
}

export interface CreateRouteRequest {
  routeNumber: string
  routeName: string
  origin: string
  destination: string
  distance: number
  estimatedDuration: number
  isCircular?: boolean
  description?: string
  color?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const routeService = {
  // Get all routes
  getAllRoutes: async (page: number = 0, size: number = 10): Promise<PageResponse<RouteResponse>> => {
    const response = await api.get<PageResponse<RouteResponse>>('/api/routes', {
      params: { page, size },
    })
    return response.data
  },

  // Get route by ID
  getRouteById: async (id: number): Promise<RouteResponse> => {
    const response = await api.get<RouteResponse>(`/api/routes/${id}`)
    return response.data
  },

  // Create route
  createRoute: async (data: CreateRouteRequest): Promise<RouteResponse> => {
    const response = await api.post<RouteResponse>('/api/routes', data)
    return response.data
  },

  // Update route
  updateRoute: async (id: number, data: Partial<CreateRouteRequest>): Promise<RouteResponse> => {
    const response = await api.put<RouteResponse>(`/api/routes/${id}`, data)
    return response.data
  },

  // Delete route
  deleteRoute: async (id: number): Promise<void> => {
    await api.delete(`/api/routes/${id}`)
  },

  // Search routes (backend uses Pageable)
  searchRoutes: async (keyword: string, page: number = 0, size: number = 10, sort: string = 'routeNumber,asc'): Promise<PageResponse<RouteResponse>> => {
    const response = await api.get<PageResponse<RouteResponse>>('/api/routes/search', {
      params: { 
        keyword, 
        page, 
        size,
        sort
      },
    })
    return response.data
  },

  // Get route by route number
  getRouteByNumber: async (routeNumber: string): Promise<RouteResponse> => {
    const response = await api.get<RouteResponse>(`/api/routes/number/${routeNumber}`)
    return response.data
  },

  // Get route details with all stops
  getRouteDetails: async (id: number): Promise<RouteDetailsResponse> => {
    const response = await api.get<RouteDetailsResponse>(`/api/routes/${id}/details`)
    return response.data
  },

  // Get active routes
  getActiveRoutes: async (page: number = 0, size: number = 10): Promise<PageResponse<RouteResponse>> => {
    const response = await api.get<PageResponse<RouteResponse>>('/api/routes/active', {
      params: { page, size },
    })
    return response.data
  },

  // Add stop to route
  addStopToRoute: async (routeId: number, stopId: number, sequenceNumber: number): Promise<void> => {
    await api.post(`/api/routes/${routeId}/stops`, {
      stopId,
      sequenceNumber,
    })
  },

  // Remove stop from route
  removeStopFromRoute: async (routeId: number, stopId: number): Promise<void> => {
    await api.delete(`/api/routes/${routeId}/stops/${stopId}`)
  },

  // Activate route
  activateRoute: async (id: number): Promise<void> => {
    await api.patch(`/api/routes/${id}/activate`)
  },

  // Deactivate route
  deactivateRoute: async (id: number): Promise<void> => {
    await api.patch(`/api/routes/${id}/deactivate`)
  },
}

