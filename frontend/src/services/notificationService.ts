import api from '@/lib/api'

export enum NotificationStatus {
  PENDING = 'PENDING',
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  READ = 'READ'
}

export enum ChannelType {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
  PUSH = 'PUSH',
  IN_APP = 'IN_APP'
}

export interface Notification {
  id: number
  recipientId: number
  title: string
  message: string
  status: NotificationStatus
  channel: ChannelType
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  metadata?: Record<string, any>
  sentAt?: string
  readAt?: string
  createdAt: string
  updatedAt: string
}

export interface SendNotificationRequest {
  recipientId: number
  title: string
  message: string
  channel: ChannelType
  priority?: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  metadata?: Record<string, any>
}

export interface NotificationPage {
  content: Notification[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

const notificationService = {
  // Send a notification
  sendNotification: async (request: SendNotificationRequest): Promise<Notification> => {
    const response = await api.post<Notification>('/api/notifications', request)
    return response.data
  },

  // Get user's notifications with pagination
  getUserNotifications: async (
    userId: number,
    page: number = 0,
    size: number = 20
  ): Promise<NotificationPage> => {
    const response = await api.get<NotificationPage>(
      `/api/notifications/users/${userId}`,
      {
        params: { page, size, sort: 'createdAt,desc' }
      }
    )
    return response.data
  },

  // Get user's notifications by status
  getUserNotificationsByStatus: async (
    userId: number,
    status: NotificationStatus,
    page: number = 0,
    size: number = 20
  ): Promise<NotificationPage> => {
    const response = await api.get<NotificationPage>(
      `/api/notifications/users/${userId}/status/${status}`,
      {
        params: { page, size, sort: 'createdAt,desc' }
      }
    )
    return response.data
  },

  // Mark notification as read
  markAsRead: async (notificationId: number, userId: number): Promise<Notification> => {
    const response = await api.put<Notification>(
      `/api/notifications/${notificationId}/read`,
      null,
      {
        params: { userId }
      }
    )
    return response.data
  },

  // Get unread count
  getUnreadCount: async (userId: number): Promise<number> => {
    const response = await api.get<number>(
      `/api/notifications/users/${userId}/unread/count`
    )
    return response.data
  }
}

export default notificationService
