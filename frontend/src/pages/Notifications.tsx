import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Bell, Check, Filter } from 'lucide-react'
import notificationService, { Notification, NotificationStatus, ChannelType } from '@/services/notificationService'
import { format } from 'date-fns'
import { fr } from 'date-fns/locale'
import { useAuthStore } from '@/store/authStore'
import { toast } from 'react-hot-toast'

export default function Notifications() {
  const { user } = useAuthStore()
  const [page, setPage] = useState(0)
  const [statusFilter, setStatusFilter] = useState<NotificationStatus | 'ALL'>('ALL')
  const queryClient = useQueryClient()

  // Fetch notifications based on filter
  const { data: notificationsPage, isLoading } = useQuery({
    queryKey: ['notifications', user?.id, page, statusFilter],
    queryFn: async () => {
      if (!user?.id) return null

      if (statusFilter === 'ALL') {
        return notificationService.getUserNotifications(user.id, page, 20)
      } else {
        return notificationService.getUserNotificationsByStatus(user.id, statusFilter, page, 20)
      }
    },
    enabled: !!user?.id,
  })

  // Mark as read mutation
  const markAsReadMutation = useMutation({
    mutationFn: ({ notificationId, userId }: { notificationId: number; userId: number }) =>
      notificationService.markAsRead(notificationId, userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
      queryClient.invalidateQueries({ queryKey: ['notifications-unread-count'] })
      toast.success('Notification marquée comme lue')
    },
    onError: () => {
      toast.error('Erreur lors de la mise à jour')
    },
  })

  const handleMarkAsRead = (notificationId: number) => {
    if (!user?.id) return
    markAsReadMutation.mutate({ notificationId, userId: user.id })
  }

  const getPriorityBadge = (priority: string) => {
    const colors = {
      URGENT: 'bg-red-100 text-red-800 border-red-300',
      HIGH: 'bg-orange-100 text-orange-800 border-orange-300',
      MEDIUM: 'bg-yellow-100 text-yellow-800 border-yellow-300',
      LOW: 'bg-blue-100 text-blue-800 border-blue-300',
    }
    return colors[priority as keyof typeof colors] || colors.LOW
  }

  const getStatusBadge = (status: NotificationStatus) => {
    const colors = {
      [NotificationStatus.PENDING]: 'bg-gray-100 text-gray-800',
      [NotificationStatus.SENT]: 'bg-blue-100 text-blue-800',
      [NotificationStatus.DELIVERED]: 'bg-green-100 text-green-800',
      [NotificationStatus.FAILED]: 'bg-red-100 text-red-800',
      [NotificationStatus.READ]: 'bg-purple-100 text-purple-800',
    }
    return colors[status] || colors[NotificationStatus.PENDING]
  }

  const getChannelIcon = (channel: ChannelType) => {
    switch (channel) {
      case ChannelType.EMAIL:
        return '📧'
      case ChannelType.SMS:
        return '💬'
      case ChannelType.PUSH:
        return '🔔'
      case ChannelType.IN_APP:
        return '📱'
      default:
        return '📨'
    }
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <p className="text-gray-600">Veuillez vous connecter pour voir vos notifications</p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
            <Bell className="h-8 w-8 text-blue-600" />
            Notifications
          </h1>
          <p className="mt-2 text-gray-600">
            Gérez toutes vos notifications de transport
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 mb-6">
          <div className="flex items-center gap-2 mb-3">
            <Filter className="h-5 w-5 text-gray-600" />
            <span className="font-semibold text-gray-900">Filtres</span>
          </div>
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => {
                setStatusFilter('ALL')
                setPage(0)
              }}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                statusFilter === 'ALL'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Toutes
            </button>
            {Object.values(NotificationStatus).map((status) => (
              <button
                key={status}
                onClick={() => {
                  setStatusFilter(status)
                  setPage(0)
                }}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                  statusFilter === status
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {status === NotificationStatus.PENDING && 'En attente'}
                {status === NotificationStatus.SENT && 'Envoyées'}
                {status === NotificationStatus.DELIVERED && 'Délivrées'}
                {status === NotificationStatus.FAILED && 'Échouées'}
                {status === NotificationStatus.READ && 'Lues'}
              </button>
            ))}
          </div>
        </div>

        {/* Notifications List */}
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="mt-4 text-gray-600">Chargement des notifications...</p>
          </div>
        ) : notificationsPage && notificationsPage.content.length > 0 ? (
          <div className="space-y-4">
            {notificationsPage.content.map((notification: Notification) => (
              <div
                key={notification.id}
                className={`bg-white rounded-lg shadow-sm border border-gray-200 p-5 hover:shadow-md transition-shadow ${
                  notification.status === NotificationStatus.READ ? 'opacity-75' : ''
                }`}
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    {/* Title and badges */}
                    <div className="flex items-start gap-3 mb-2">
                      <span className="text-2xl">{getChannelIcon(notification.channel)}</span>
                      <div className="flex-1">
                        <h3 className="text-lg font-semibold text-gray-900 mb-1">
                          {notification.title}
                        </h3>
                        <div className="flex flex-wrap items-center gap-2">
                          <span
                            className={`px-2 py-1 rounded-full text-xs font-medium border ${getPriorityBadge(
                              notification.priority
                            )}`}
                          >
                            {notification.priority}
                          </span>
                          <span
                            className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(
                              notification.status
                            )}`}
                          >
                            {notification.status === NotificationStatus.PENDING && 'En attente'}
                            {notification.status === NotificationStatus.SENT && 'Envoyée'}
                            {notification.status === NotificationStatus.DELIVERED && 'Délivrée'}
                            {notification.status === NotificationStatus.FAILED && 'Échouée'}
                            {notification.status === NotificationStatus.READ && 'Lue'}
                          </span>
                          <span className="text-xs text-gray-500">
                            {notification.channel}
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Message */}
                    <p className="text-gray-700 mb-3 ml-11">{notification.message}</p>

                    {/* Metadata */}
                    <div className="flex items-center gap-4 text-sm text-gray-500 ml-11">
                      <span>
                        Créée: {format(new Date(notification.createdAt), 'dd MMM yyyy HH:mm', { locale: fr })}
                      </span>
                      {notification.sentAt && (
                        <span>
                          Envoyée: {format(new Date(notification.sentAt), 'dd MMM yyyy HH:mm', { locale: fr })}
                        </span>
                      )}
                      {notification.readAt && (
                        <span>
                          Lue: {format(new Date(notification.readAt), 'dd MMM yyyy HH:mm', { locale: fr })}
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Actions */}
                  {notification.status !== NotificationStatus.READ && (
                    <button
                      onClick={() => handleMarkAsRead(notification.id)}
                      disabled={markAsReadMutation.isPending}
                      className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <Check className="h-4 w-4" />
                      Marquer comme lu
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <Bell className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Aucune notification</h3>
            <p className="text-gray-600">
              {statusFilter === 'ALL'
                ? 'Vous n\'avez aucune notification pour le moment'
                : 'Aucune notification avec ce statut'}
            </p>
          </div>
        )}

        {/* Pagination */}
        {notificationsPage && notificationsPage.totalPages > 1 && (
          <div className="mt-8 flex items-center justify-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Précédent
            </button>
            <span className="px-4 py-2 text-gray-700">
              Page {page + 1} sur {notificationsPage.totalPages}
            </span>
            <button
              onClick={() => setPage((p) => Math.min(notificationsPage.totalPages - 1, p + 1))}
              disabled={page >= notificationsPage.totalPages - 1}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Suivant
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
