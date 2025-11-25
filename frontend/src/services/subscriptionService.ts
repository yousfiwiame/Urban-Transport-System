import api from '@/lib/api'

export interface SubscriptionPlan {
  planId: string
  planCode: string
  name: string
  description?: string
  price: number
  duration: number
  durationUnit: string
  features?: string[]
  isActive: boolean
}

export interface Subscription {
  subscriptionId: string
  userId: string
  planId: string
  status: string
  startDate: string
  endDate: string
  nextBillingDate?: string
  autoRenewEnabled: boolean
  plan?: SubscriptionPlan
}

export interface Payment {
  paymentId: string
  subscriptionId: string
  amount: number
  status: string
  paymentDate: string
  paymentMethod: string
}

export const subscriptionService = {
  getPlans: async (): Promise<SubscriptionPlan[]> => {
    const response = await api.get<SubscriptionPlan[]>('/api/plans/active')
    return response.data
  },

  getMySubscriptions: async (userId: string): Promise<Subscription[]> => {
    const response = await api.get<Subscription[]>(`/api/subscriptions/user/${userId}`)
    return response.data
  },

  subscribe: async (userId: string, planId: string): Promise<Subscription> => {
    const response = await api.post<Subscription>('/api/subscriptions', {
      userId,
      planId,
    })
    return response.data
  },

  cancelSubscription: async (subscriptionId: string, reason?: string): Promise<Subscription> => {
    const response = await api.put<Subscription>(`/api/subscriptions/${subscriptionId}/cancel`, {
      reason: reason || 'User requested cancellation',
    })
    return response.data
  },

  getPayments: async (): Promise<Payment[]> => {
    const response = await api.get<Payment[]>('/api/payments')
    return response.data
  },
}

