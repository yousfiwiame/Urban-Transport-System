import api from '@/lib/api'

export interface SubscriptionPlanResponse {
  planId: string
  planCode: string
  planName: string
  description: string
  features: string[]
  durationDays: number
  price: number
  currency: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface CreatePlanRequest {
  planCode: string
  planName: string
  description?: string
  features?: string[]
  durationDays: number
  price: number
  currency: string
  isActive?: boolean
}

export const planService = {
  /**
   * Get all subscription plans
   */
  getAllPlans: async (): Promise<SubscriptionPlanResponse[]> => {
    const response = await api.get<SubscriptionPlanResponse[]>('/api/plans')
    return response.data
  },

  /**
   * Get only active subscription plans (for passengers)
   */
  getActivePlans: async (): Promise<SubscriptionPlanResponse[]> => {
    const response = await api.get<SubscriptionPlanResponse[]>('/api/plans/active')
    return response.data
  },

  /**
   * Get subscription plan by ID
   */
  getPlanById: async (id: string): Promise<SubscriptionPlanResponse> => {
    const response = await api.get<SubscriptionPlanResponse>(`/api/plans/${id}`)
    return response.data
  },

  /**
   * Get subscription plan by code
   */
  getPlanByCode: async (code: string): Promise<SubscriptionPlanResponse> => {
    const response = await api.get<SubscriptionPlanResponse>(`/api/plans/code/${code}`)
    return response.data
  },

  /**
   * Create a new subscription plan (ADMIN only)
   */
  createPlan: async (data: CreatePlanRequest): Promise<SubscriptionPlanResponse> => {
    const response = await api.post<SubscriptionPlanResponse>('/api/plans', data)
    return response.data
  },

  /**
   * Update an existing subscription plan (ADMIN only)
   */
  updatePlan: async (id: string, data: CreatePlanRequest): Promise<SubscriptionPlanResponse> => {
    const response = await api.put<SubscriptionPlanResponse>(`/api/plans/${id}`, data)
    return response.data
  },

  /**
   * Delete (deactivate) a subscription plan (ADMIN only)
   */
  deletePlan: async (id: string): Promise<void> => {
    await api.delete(`/api/plans/${id}`)
  },
}
