import api from '@/lib/api'

export interface UserStatistics {
  totalUsers: number
  passengers: number
  drivers: number
  admins: number
  activeUsers: number
  inactiveUsers: number
  emailVerifiedUsers: number
  phoneVerifiedUsers: number
}

export interface ScheduleStatistics {
  totalBuses: number
  activeBuses: number
  totalRoutes: number
  activeRoutes: number
  totalSchedules: number
  activeSchedules: number
}

export interface SubscriptionStatistics {
  totalSubscriptions: number
  activeSubscriptions: number
  totalPlans: number
  activePlans: number
}

export interface TicketStatistics {
  totalTickets: number
  activeTickets: number
  usedTickets: number
  expiredTickets: number
  cancelledTickets: number
  totalRevenue: number
  activeRevenue: number
  usedRevenue: number
}

export interface DashboardStatistics {
  users: UserStatistics
  schedules: ScheduleStatistics
  subscriptions: SubscriptionStatistics
  tickets: TicketStatistics
}

export const statisticsService = {
  // Get user statistics
  getUserStatistics: async (): Promise<UserStatistics> => {
    const response = await api.get<UserStatistics>('/api/statistics/users')
    return response.data
  },

  // Get schedule statistics (buses, routes, schedules)
  getScheduleStatistics: async (): Promise<ScheduleStatistics> => {
    const response = await api.get<ScheduleStatistics>('/api/statistics/schedules')
    return response.data
  },

  // Get subscription statistics
  getSubscriptionStatistics: async (): Promise<SubscriptionStatistics> => {
    const response = await api.get<SubscriptionStatistics>('/api/statistics/subscriptions')
    return response.data
  },

  // Get ticket statistics
  getTicketStatistics: async (): Promise<TicketStatistics> => {
    const response = await api.get<TicketStatistics>('/api/tickets/statistics')
    return response.data
  },

  // Get all statistics for dashboard
  getAllStatistics: async (): Promise<DashboardStatistics> => {
    const [users, schedules, subscriptions, tickets] = await Promise.all([
      statisticsService.getUserStatistics(),
      statisticsService.getScheduleStatistics(),
      statisticsService.getSubscriptionStatistics(),
      statisticsService.getTicketStatistics(),
    ])

    return { users, schedules, subscriptions, tickets }
  },
}

