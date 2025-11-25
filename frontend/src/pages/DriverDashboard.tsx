import { useAuthStore } from '@/store/authStore'
import { Bus, MapPin, Clock, Calendar, CheckCircle, Navigation, User, TrendingUp, AlertCircle } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { driverService } from '@/services/driverService'

export default function DriverDashboard() {
  const { user } = useAuthStore()
  const driverId = user?.id ? String(user.id) : ''

  // Fetch driver statistics
  const { data: dailyStats, isLoading: statsLoading, error: statsError } = useQuery({
    queryKey: ['driverStats', driverId],
    queryFn: () => driverService.getTodayStats(driverId),
    enabled: !!driverId,
    refetchInterval: 60000, // Refresh every minute
  })

  // Fetch upcoming trips
  const { data: upcomingTrips = [], isLoading: upcomingLoading } = useQuery({
    queryKey: ['upcomingTrips', driverId],
    queryFn: () => driverService.getUpcomingTrips(driverId),
    enabled: !!driverId,
    refetchInterval: 30000, // Refresh every 30 seconds
  })

  // Fetch completed trips today
  const { data: todayTrips = [], isLoading: todayLoading } = useQuery({
    queryKey: ['completedTripsToday', driverId],
    queryFn: () => driverService.getCompletedTripsToday(driverId),
    enabled: !!driverId,
  })

  // Fetch performance metrics
  const { data: performance, isLoading: performanceLoading } = useQuery({
    queryKey: ['driverPerformance', driverId],
    queryFn: () => driverService.getPerformance(driverId),
    enabled: !!driverId,
  })

  // Calculate stats from real data
  const stats = [
    {
      name: 'Trajets Aujourd\'hui',
      value: dailyStats?.tripsToday || 0,
      icon: Navigation,
      color: 'from-primary-500 to-primary-600',
      bgColor: 'bg-primary-50',
      iconColor: 'text-primary-600',
    },
    {
      name: 'Heures de Conduite',
      value: dailyStats?.drivingHours ? `${dailyStats.drivingHours}h` : '0h',
      icon: Clock,
      color: 'from-accent-500 to-accent-600',
      bgColor: 'bg-accent-50',
      iconColor: 'text-accent-600',
    },
    {
      name: 'Passagers Transportés',
      value: dailyStats?.passengersTransported || 0,
      icon: User,
      color: 'from-success-500 to-success-600',
      bgColor: 'bg-success-50',
      iconColor: 'text-success-600',
    },
    {
      name: 'Statut du Bus',
      value: dailyStats?.busStatus || 'Inconnu',
      icon: Bus,
      color: 'from-primary-600 to-accent-500',
      bgColor: 'bg-primary-50',
      iconColor: 'text-primary-600',
    },
  ]

  // Show loading state
  if (statsLoading || upcomingLoading || todayLoading || performanceLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Chargement de vos données...</p>
        </div>
      </div>
    )
  }

  // Show error state
  if (statsError) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <AlertCircle className="mx-auto h-12 w-12 text-red-500 mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">Erreur de chargement</h3>
          <p className="text-gray-600 mb-4">Impossible de charger vos données</p>
          <button 
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
          >
            Réessayer
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Hero Section */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary-600 via-primary-700 to-accent-600 p-8 md:p-12 text-white shadow-2xl">
        <div className="absolute inset-0 opacity-20" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")` }}></div>
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-4">
            <div className="p-3 bg-white/20 backdrop-blur-sm rounded-2xl">
              <Bus size={32} />
            </div>
            <div>
              <p className="text-primary-100 text-sm font-medium">Tableau de Bord Conducteur</p>
              <h1 className="text-4xl md:text-5xl font-bold">
                Bonjour, {user?.firstName}
              </h1>
            </div>
          </div>
          <p className="text-primary-100 text-lg md:text-xl max-w-2xl mb-6">
            Gérez vos trajets, consultez vos horaires et suivez vos performances en temps réel.
          </p>
          <div className="flex flex-wrap gap-3">
            <Link
              to="/driver/start-service"
              className="group flex items-center gap-2 px-6 py-3 bg-white text-primary-600 rounded-xl hover:bg-white/90 transition-all duration-300 transform hover:scale-105 font-bold shadow-lg"
            >
              <Navigation size={20} />
              <span>🚀 Démarrer Service</span>
            </Link>
            <Link
              to="/tracking"
              className="group flex items-center gap-2 px-6 py-3 bg-white/20 backdrop-blur-sm rounded-xl hover:bg-white/30 transition-all duration-300 transform hover:scale-105"
            >
              <MapPin size={20} />
              <span className="font-semibold">Suivi des Bus</span>
            </Link>
            <Link
              to="/schedules"
              className="group flex items-center gap-2 px-6 py-3 bg-white/20 backdrop-blur-sm rounded-xl hover:bg-white/30 transition-all duration-300 transform hover:scale-105"
            >
              <Calendar size={20} />
              <span className="font-semibold">Mes Horaires</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <div
              key={stat.name}
              className="card hover:scale-105 transition-all duration-300 animate-slide-up"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              <div className="flex items-center gap-4">
                <div className={`p-4 ${stat.bgColor} rounded-2xl`}>
                  <Icon className={stat.iconColor} size={28} />
                </div>
                <div className="flex-1">
                  <p className="text-sm text-gray-600 font-medium mb-1">{stat.name}</p>
                  <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Upcoming Trips */}
        <div className="card-gradient">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl shadow-lg">
              <Navigation className="text-white" size={24} />
            </div>
            <h2 className="text-2xl font-bold text-gray-900">Trajets à Venir</h2>
          </div>
          <div className="space-y-4">
            {upcomingTrips.length > 0 ? (
              upcomingTrips.map((trip) => (
                <div
                  key={trip.id}
                  className="p-4 bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow border border-gray-100"
                >
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-primary-100 rounded-lg">
                        <Clock size={20} className="text-primary-600" />
                      </div>
                      <div>
                        <p className="font-bold text-gray-900">
                          {trip.departureTime} → {trip.arrivalTime}
                        </p>
                        <p className="text-sm text-gray-600">{trip.routeName || `Route #${trip.routeId}`}</p>
                      </div>
                    </div>
                    <span className="px-3 py-1 bg-accent-100 text-accent-700 rounded-full text-xs font-semibold">
                      {trip.status === 'SCHEDULED' ? 'À venir' : trip.status}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <User size={16} />
                    <span>{trip.estimatedPassengers || 'N/A'} passagers estimés</span>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-12">
                <Navigation className="mx-auto text-gray-400 mb-4" size={48} />
                <p className="text-gray-500">Aucun trajet à venir</p>
              </div>
            )}
          </div>
        </div>

        {/* Today's Completed Trips */}
        <div className="card-gradient">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-gradient-to-br from-success-500 to-success-600 rounded-xl shadow-lg">
              <CheckCircle className="text-white" size={24} />
            </div>
            <h2 className="text-2xl font-bold text-gray-900">Trajets d'Aujourd'hui</h2>
          </div>
          <div className="space-y-4">
            {todayTrips.length > 0 ? (
              todayTrips.map((trip) => (
                <div
                  key={trip.id}
                  className="p-4 bg-white rounded-xl shadow-md border border-gray-100"
                >
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-success-100 rounded-lg">
                        <CheckCircle size={20} className="text-success-600" />
                      </div>
                      <div>
                        <p className="font-bold text-gray-900">
                          {trip.departureTime} - {trip.arrivalTime}
                        </p>
                        <p className="text-sm text-gray-600">{trip.routeName || `Route #${trip.routeId}`}</p>
                      </div>
                    </div>
                    <span className="px-3 py-1 bg-success-100 text-success-700 rounded-full text-xs font-semibold">
                      {trip.status === 'COMPLETED' ? 'Terminé' : trip.status}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <User size={16} />
                    <span>{trip.actualPassengers || trip.estimatedPassengers || 0} passagers</span>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-12">
                <CheckCircle className="mx-auto text-gray-400 mb-4" size={48} />
                <p className="text-gray-500">Aucun trajet complété aujourd'hui</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Performance Section */}
      <div className="card-gradient">
        <div className="flex items-center gap-3 mb-6">
          <div className="p-3 bg-gradient-to-br from-accent-500 to-accent-600 rounded-xl shadow-lg">
            <TrendingUp className="text-white" size={24} />
          </div>
          <h2 className="text-2xl font-bold text-gray-900">Performances</h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center p-6 bg-white rounded-xl shadow-md">
            <p className="text-sm text-gray-600 mb-2">Ponctualité</p>
            <p className="text-4xl font-bold text-primary-600 mb-1">
              {performance?.punctuality ? `${performance.punctuality.toFixed(0)}%` : 'N/A'}
            </p>
            <p className="text-xs text-gray-500">
              {performance?.punctuality && performance.punctuality >= 95 ? 'Excellent' : 
               performance?.punctuality && performance.punctuality >= 85 ? 'Très bien' : 
               performance?.punctuality && performance.punctuality >= 70 ? 'Bien' : 'À améliorer'}
            </p>
          </div>
          <div className="text-center p-6 bg-white rounded-xl shadow-md">
            <p className="text-sm text-gray-600 mb-2">Satisfaction</p>
            <p className="text-4xl font-bold text-accent-600 mb-1">
              {performance?.averageRating ? `${performance.averageRating.toFixed(1)}/5` : 'N/A'}
            </p>
            <p className="text-xs text-gray-500">
              {performance?.averageRating && performance.averageRating >= 4.5 ? 'Excellent' : 
               performance?.averageRating && performance.averageRating >= 4 ? 'Très bien' : 
               performance?.averageRating && performance.averageRating >= 3 ? 'Bien' : 'À améliorer'}
            </p>
          </div>
          <div className="text-center p-6 bg-white rounded-xl shadow-md">
            <p className="text-sm text-gray-600 mb-2">Trajets ce Mois</p>
            <p className="text-4xl font-bold text-success-600 mb-1">
              {performance?.totalTripsThisMonth || 0}
            </p>
            <p className="text-xs text-gray-500">
              {performance?.totalTripsThisMonth && performance?.totalTripsLastMonth 
                ? `${((performance.totalTripsThisMonth - performance.totalTripsLastMonth) / performance.totalTripsLastMonth * 100).toFixed(0)}% vs mois dernier`
                : 'Premier mois'}
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

