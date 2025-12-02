import { useQuery } from '@tanstack/react-query'
import { ticketService } from '@/services/ticketService'
import { scheduleService } from '@/services/scheduleService'
import { Ticket, Calendar, MapPin, CreditCard, Bus, ArrowRight, Clock, TrendingUp, Users } from 'lucide-react'
import { Link, Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { isDriver, isAdmin } from '@/utils/roles'
import { translateDay, formatDateFR } from '@/utils/dateHelpers'

export default function Dashboard() {
  const { user } = useAuthStore()
  
  // Rediriger les admins vers leur dashboard spécifique
  if (isAdmin(user?.roles)) {
    return <Navigate to="/admin" replace />
  }
  
  // Rediriger les conducteurs vers leur dashboard spécifique
  if (isDriver(user?.roles)) {
    return <Navigate to="/driver" replace />
  }
  const { data: tickets = [], isLoading: ticketsLoading } = useQuery({
    queryKey: ['myTickets', user?.id],
    queryFn: () => user?.id ? ticketService.getMyTickets(user.id) : Promise.resolve([]),
    enabled: !!user?.id,
  })

  const { data: schedules, isLoading: schedulesLoading } = useQuery({
    queryKey: ['schedules'],
    queryFn: () => scheduleService.getAllSchedules(0, 10),
  })

  const stats = [
    {
      name: 'Mes Billets',
      value: tickets.length || 0,
      icon: Ticket,
      color: 'from-primary-500 to-primary-600',
      bgColor: 'bg-primary-50',
      iconColor: 'text-primary-600',
      link: '/tickets',
      description: 'Billets actifs',
    },
    {
      name: 'Horaires',
      value: schedules?.content?.length || 0,
      icon: Calendar,
      color: 'from-accent-500 to-accent-600',
      bgColor: 'bg-accent-50',
      iconColor: 'text-accent-600',
      link: '/schedules',
      description: 'Itinéraires disponibles',
    },
    {
      name: 'Suivi des Bus',
      value: 'En Direct',
      icon: MapPin,
      color: 'from-accent-500 to-accent-600',
      bgColor: 'bg-accent-50',
      iconColor: 'text-accent-600',
      link: '/tracking',
      description: 'Suivi en temps réel',
    },
    {
      name: 'Abonnements',
      value: 'Gérer',
      icon: CreditCard,
      color: 'from-accent-500 to-accent-600',
      bgColor: 'bg-accent-50',
      iconColor: 'text-accent-600',
      link: '/subscriptions',
      description: 'Vos plans',
    },
  ]

  const quickActions = [
    { icon: Bus, label: 'Réserver un Billet', path: '/tickets', color: 'from-primary-600 to-primary-700' },
    { icon: MapPin, label: 'Suivre un Bus', path: '/tracking', color: 'from-accent-500 to-accent-600' },
    { icon: Calendar, label: 'Voir les Horaires', path: '/schedules', color: 'from-primary-500 to-accent-500' },
    { icon: CreditCard, label: 'S\'abonner', path: '/subscriptions', color: 'from-accent-500 to-accent-600' },
  ]

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Hero Section */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary-600 via-primary-700 to-accent-500 p-8 md:p-12 text-white shadow-2xl">
        <div className="absolute inset-0 opacity-20" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")` }}></div>
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-4">
            <div className="p-3 bg-white/20 backdrop-blur-sm rounded-2xl">
              <Bus size={32} />
            </div>
            <div>
              <p className="text-primary-100 text-sm font-medium">Bon retour,</p>
              <h1 className="text-4xl md:text-5xl font-bold">
                {user?.firstName || 'Utilisateur'} ! 👋
              </h1>
            </div>
          </div>
          <p className="text-primary-100 text-lg md:text-xl max-w-2xl mb-6">
            Votre compagnon de transport urbain. Réservez des billets, suivez les bus et gérez votre voyage en un seul endroit.
          </p>
          <div className="flex flex-wrap gap-3">
            {quickActions.map((action) => {
              const Icon = action.icon
              return (
                <Link
                  key={action.label}
                  to={action.path}
                  className={`group flex items-center gap-2 px-6 py-3 bg-white/20 backdrop-blur-sm rounded-xl hover:bg-white/30 transition-all duration-300 transform hover:scale-105`}
                >
                  <Icon size={20} />
                  <span className="font-semibold">{action.label}</span>
                  <ArrowRight size={16} className="opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                </Link>
              )
            })}
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <Link
              key={stat.name}
              to={stat.link}
              className="group card hover:scale-105 transition-all duration-300 animate-slide-up"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              <div className="flex items-start justify-between mb-4">
                <div className={`p-3 ${stat.bgColor} rounded-xl group-hover:scale-110 transition-transform duration-300`}>
                  <Icon className={stat.iconColor} size={24} />
                </div>
                <ArrowRight className="text-gray-400 group-hover:text-primary-600 group-hover:translate-x-1 transition-all" size={20} />
              </div>
              <div>
                <p className="text-sm text-gray-600 mb-1">{stat.name}</p>
                <p className="text-3xl font-bold text-gray-900 mb-1">{stat.value}</p>
                <p className="text-xs text-gray-500">{stat.description}</p>
              </div>
            </Link>
          )
        })}
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Tickets */}
        <div className="card animate-slide-up">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary-100 rounded-lg">
                <Ticket className="text-primary-600" size={20} />
              </div>
              <h2 className="text-xl font-bold text-gray-900">Billets Récents</h2>
            </div>
            <Link
              to="/tickets"
              className="text-primary-600 hover:text-primary-700 font-semibold text-sm flex items-center gap-1 group"
            >
              Voir tout
              <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>
          {ticketsLoading ? (
            <div className="flex items-center justify-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            </div>
          ) : tickets && tickets.length > 0 ? (
            <div className="space-y-3">
              {tickets.slice(0, 5).map((ticket: any) => (
                <div
                  key={ticket.id}
                  className="group flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-white rounded-xl hover:shadow-md transition-all duration-200 border border-gray-100"
                >
                  <div className="flex items-center gap-4">
                    <div className="p-2 bg-primary-100 rounded-lg group-hover:bg-primary-200 transition-colors">
                      <Ticket className="text-primary-600" size={20} />
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">Ticket #{ticket.id}</p>
                      <p className="text-sm text-gray-600 flex items-center gap-1 mt-1">
                        <Clock size={14} />
                        {formatDateFR(ticket.dateAchat)}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-lg font-bold text-gray-900">${ticket.prix.toFixed(2)}</span>
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        ticket.statut === 'ACTIVE'
                          ? 'bg-success-100 text-success-700'
                          : 'bg-gray-100 text-gray-700'
                      }`}
                    >
                      {ticket.statut}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
                <Ticket className="text-gray-400" size={32} />
              </div>
              <p className="text-gray-500 mb-4">Aucun billet pour le moment</p>
              <Link to="/tickets" className="btn btn-primary inline-flex items-center gap-2">
                <Ticket size={18} />
                Acheter un Billet
              </Link>
            </div>
          )}
        </div>

        {/* Upcoming Schedules */}
        <div className="card animate-slide-up">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-accent-100 rounded-lg">
                <Calendar className="text-accent-600" size={20} />
              </div>
              <h2 className="text-xl font-bold text-gray-900">Horaires à Venir</h2>
            </div>
            <Link
              to="/schedules"
              className="text-primary-600 hover:text-primary-700 font-semibold text-sm flex items-center gap-1 group"
            >
              Voir tout
              <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>
          {schedulesLoading ? (
            <div className="flex items-center justify-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            </div>
          ) : schedules?.content && schedules.content.length > 0 ? (
            <div className="space-y-3">
              {schedules.content.slice(0, 5).map((schedule: any) => (
                <div
                  key={schedule.id}
                  className="group flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-white rounded-xl hover:shadow-md transition-all duration-200 border border-gray-100"
                >
                  <div className="flex items-center gap-4">
                    <div className="p-2 bg-accent-100 rounded-lg group-hover:bg-accent-200 transition-colors">
                      <Bus className="text-accent-600" size={20} />
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">Route #{schedule.routeId}</p>
                      <p className="text-sm text-gray-600 flex items-center gap-1 mt-1">
                        <Clock size={14} />
                        {schedule.departureTime}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="px-3 py-1 bg-primary-100 text-primary-700 rounded-lg text-xs font-semibold">
                      {translateDay(schedule.dayOfWeek)}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
                <Calendar className="text-gray-400" size={32} />
              </div>
              <p className="text-gray-500 mb-4">Aucun horaire disponible</p>
              <Link to="/schedules" className="btn btn-primary inline-flex items-center gap-2">
                <Calendar size={18} />
                Voir les Horaires
              </Link>
            </div>
          )}
        </div>
      </div>

      {/* Features Section */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card-gradient p-6 text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-600 rounded-2xl mb-4 shadow-lg">
            <TrendingUp className="text-white" size={32} />
          </div>
          <h3 className="font-bold text-lg mb-2">Rapide et Fiable</h3>
          <p className="text-gray-600 text-sm">Mises à jour en temps réel et horaires précis</p>
        </div>
        <div className="card-gradient p-6 text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-accent-500 to-accent-600 rounded-2xl mb-4 shadow-lg">
            <Users className="text-white" size={32} />
          </div>
          <h3 className="font-bold text-lg mb-2">Réservation Facile</h3>
          <p className="text-gray-600 text-sm">Réservez vos billets en quelques clics</p>
        </div>
        <div className="card-gradient p-6 text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-accent-500 to-accent-600 rounded-2xl mb-4 shadow-lg">
            <MapPin className="text-white" size={32} />
          </div>
          <h3 className="font-bold text-lg mb-2">Suivi en Direct</h3>
          <p className="text-gray-600 text-sm">Suivez votre bus en temps réel sur la carte</p>
        </div>
      </div>
    </div>
  )
}
