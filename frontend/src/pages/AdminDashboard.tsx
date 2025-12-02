import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { 
  TrendingUp, Users, Bus, Ticket, CreditCard, MapPin, Clock, DollarSign,
  UserPlus, AlertCircle
} from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell, Legend } from 'recharts'
import { statisticsService } from '@/services/statisticsService'
import type { DashboardStatistics } from '@/services/statisticsService'

export default function AdminDashboard() {
  const [dashboardStats, setDashboardStats] = useState<DashboardStatistics | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Charger toutes les statistiques au montage du composant
  useEffect(() => {
    const fetchAllStatistics = async () => {
      try {
        setLoading(true)
        const stats = await statisticsService.getAllStatistics()
        setDashboardStats(stats)
        setError(null)
      } catch (err) {
        console.error('Erreur lors du chargement des statistiques:', err)
        setError('Impossible de charger les statistiques. Veuillez réessayer.')
      } finally {
        setLoading(false)
      }
    }

    fetchAllStatistics()
  }, [])

  // Mock data temporaire pour les graphiques (à remplacer plus tard)
  const revenueData = [
    { month: 'Jan', tickets: 45000, abonnements: 120000 },
    { month: 'Fév', tickets: 52000, abonnements: 135000 },
    { month: 'Mar', tickets: 48000, abonnements: 142000 },
    { month: 'Avr', tickets: 61000, abonnements: 158000 },
    { month: 'Mai', tickets: 55000, abonnements: 165000 },
    { month: 'Juin', tickets: 67000, abonnements: 178000 },
    { month: 'Juil', tickets: 72000, abonnements: 185000 },
    { month: 'Août', tickets: 78000, abonnements: 192000 },
    { month: 'Sep', tickets: 69000, abonnements: 188000 },
    { month: 'Oct', tickets: 74000, abonnements: 195000 },
    { month: 'Nov', tickets: 81000, abonnements: 203000 },
    { month: 'Déc', tickets: 89000, abonnements: 215000 },
  ]

  const routeUsage = [
    { route: 'Ligne 12', passengers: 4500 },
    { route: 'Ligne 5', passengers: 3800 },
    { route: 'Ligne 1', passengers: 5200 },
    { route: 'Ligne 8', passengers: 3200 },
    { route: 'Ligne 3', passengers: 2900 },
  ]

  const recentActivities = [
    { type: 'user', message: 'Nouvel utilisateur inscrit: Ahmed El Fassi', time: 'Il y a 5 min', icon: UserPlus, color: 'text-blue-600' },
    { type: 'ticket', message: 'Ticket T-1245 acheté sur Ligne 12', time: 'Il y a 12 min', icon: Ticket, color: 'text-green-600' },
    { type: 'subscription', message: 'Abonnement Mensuel souscrit par Fatima Z.', time: 'Il y a 23 min', icon: CreditCard, color: 'text-purple-600' },
    { type: 'bus', message: 'Bus L12-03 est en maintenance', time: 'Il y a 45 min', icon: Bus, color: 'text-red-600' },
    { type: 'delay', message: 'Retard signalé sur Ligne 5', time: 'Il y a 1h', icon: Clock, color: 'text-accent-600' },
  ]

  // Calculer les stats à partir des vraies données
  const calculateStats = () => {
    if (!dashboardStats) return []

    const { users, schedules, subscriptions, tickets } = dashboardStats

    return [
      {
        label: 'Utilisateurs Totaux',
        value: users.totalUsers.toLocaleString(),
        change: '+12.5%', // TODO: calculer à partir de l'historique
        icon: Users,
        color: 'from-blue-500 to-blue-600',
        bgColor: 'bg-blue-50',
        link: '/admin/users'
      },
      {
        label: 'Bus Actifs',
        value: schedules.activeBuses.toString(),
        change: `+${schedules.activeBuses - (schedules.totalBuses - schedules.activeBuses)}`,
        icon: Bus,
        color: 'from-red-500 to-red-600',
        bgColor: 'bg-red-50',
        link: '/admin/buses'
      },
      {
        label: 'Tickets Vendus (Total)',
        value: tickets.totalTickets.toLocaleString(),
        change: '+18.3%', // TODO: calculer le pourcentage de croissance
        icon: Ticket,
        color: 'from-green-500 to-green-600',
        bgColor: 'bg-green-50',
        link: '/admin'
      },
      {
        label: 'Revenus Totaux',
        value: `${(tickets.totalRevenue / 1000).toFixed(0)}k MAD`,
        change: '+23.1%', // TODO: calculer à partir de l'historique
        icon: DollarSign,
        color: 'from-purple-500 to-purple-600',
        bgColor: 'bg-purple-50',
        link: '/admin'
      },
      {
        label: 'Abonnements Actifs',
        value: subscriptions.activeSubscriptions.toLocaleString(),
        change: '+8.7%', // TODO: calculer à partir de l'historique
        icon: CreditCard,
        color: 'from-accent-500 to-accent-600',
        bgColor: 'bg-accent-50',
        link: '/admin/subscriptions'
      },
      {
        label: 'Routes Actives',
        value: schedules.activeRoutes.toString(),
        change: '0',
        icon: MapPin,
        color: 'from-primary-500 to-primary-600',
        bgColor: 'bg-primary-50',
        link: '/admin/routes'
      },
      {
        label: 'Horaires Programmés',
        value: schedules.totalSchedules.toLocaleString(),
        change: '+5.2%', // TODO: calculer à partir de l'historique
        icon: Clock,
        color: 'from-indigo-500 to-indigo-600',
        bgColor: 'bg-indigo-50',
        link: '/admin/schedules'
      },
      {
        label: 'Tickets Actifs',
        value: `${((tickets.activeTickets / tickets.totalTickets) * 100).toFixed(0)}%`,
        change: '+3.4%',
        icon: TrendingUp,
        color: 'from-pink-500 to-pink-600',
        bgColor: 'bg-pink-50',
        link: '/admin'
      },
    ]
  }

  // Préparer les données pour le graphique de distribution des utilisateurs
  const getUserDistribution = () => {
    if (!dashboardStats) return []

    const { users } = dashboardStats
    return [
      { name: 'Passagers', value: users.passengers, color: '#3B82F6' },
      { name: 'Conducteurs', value: users.drivers, color: '#F59E0B' },
      { name: 'Admins', value: users.admins, color: '#EF4444' },
    ]
  }

  // État de chargement
  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  // État d'erreur
  if (error) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <AlertCircle className="mx-auto h-12 w-12 text-red-500 mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">Erreur de chargement</h3>
          <p className="text-gray-600 mb-4">{error}</p>
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

  const stats = calculateStats()
  const userDistribution = getUserDistribution()

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold gradient-text mb-2">Tableau de Bord</h1>
        <p className="text-gray-600 text-lg">Vue d'ensemble du système de transport urbain</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <Link 
            key={index}
            to={stat.link}
            className="card hover:scale-105 transition-all duration-300 animate-slide-up group cursor-pointer"
            style={{ animationDelay: `${index * 50}ms` }}
          >
            <div className="flex items-start justify-between mb-4">
              <div className={`p-3 rounded-xl ${stat.bgColor} group-hover:scale-110 transition-transform`}>
                <stat.icon className="text-gray-700" size={24} />
              </div>
              <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                stat.change.startsWith('+') ? 'bg-success-100 text-success-700' : 'bg-gray-100 text-gray-700'
              }`}>
                {stat.change === '0' ? 'Stable' : stat.change + ' ce mois'}
              </span>
            </div>
            <p className="text-sm text-gray-600 mb-2">{stat.label}</p>
            <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
          </Link>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart - TODO: Remplacer par vraies données */}
        <div className="card">
          <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
            <DollarSign className="text-primary-600" size={24} />
            Revenus Mensuels (MAD)
            <span className="text-xs font-normal text-gray-500">(Données simulées)</span>
          </h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="tickets" fill="#DC3545" name="Tickets" />
              <Bar dataKey="abonnements" fill="#FD7E14" name="Abonnements" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Route Usage - TODO: Remplacer par vraies données */}
        <div className="card">
          <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
            <MapPin className="text-primary-600" size={24} />
            Utilisation des Routes (Passagers)
            <span className="text-xs font-normal text-gray-500">(Données simulées)</span>
          </h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={routeUsage}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="route" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="passengers" stroke="#DC3545" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Bottom Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* User Distribution - VRAIES DONNÉES */}
        <div className="card">
          <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
            <Users className="text-primary-600" size={24} />
            Répartition des Utilisateurs
            <span className="text-xs font-normal text-success-600">✓ En direct</span>
          </h3>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={userDistribution}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {userDistribution.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Recent Activity - TODO: Remplacer par vraies données */}
        <div className="card lg:col-span-2">
          <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
            <Clock className="text-primary-600" size={24} />
            Activité Récente
            <span className="text-xs font-normal text-gray-500">(Données simulées)</span>
          </h3>
          <div className="space-y-4">
            {recentActivities.map((activity, index) => (
              <div key={index} className="flex items-start gap-4 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                <div className={`p-2 rounded-lg bg-gray-100 ${activity.color}`}>
                  <activity.icon size={20} />
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">{activity.message}</p>
                  <p className="text-xs text-gray-500 mt-1">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card bg-gradient-to-r from-primary-50 to-accent-50">
        <h3 className="text-2xl font-bold text-gray-900 mb-6">Actions Rapides</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link to="/admin/users" className="flex items-center gap-3 p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-all hover:scale-105">
            <div className="p-3 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg">
              <UserPlus className="text-white" size={24} />
            </div>
            <span className="font-semibold text-gray-900">Gérer Utilisateurs</span>
          </Link>
          
          <Link to="/admin/buses" className="flex items-center gap-3 p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-all hover:scale-105">
            <div className="p-3 bg-gradient-to-br from-red-500 to-red-600 rounded-lg">
              <Bus className="text-white" size={24} />
            </div>
            <span className="font-semibold text-gray-900">Ajouter Bus</span>
          </Link>
          
          <Link to="/admin/routes" className="flex items-center gap-3 p-4 bg-white rounded-xl shadow-sm hover:shadow-md transition-all hover:scale-105">
            <div className="p-3 bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg">
              <MapPin className="text-white" size={24} />
            </div>
            <span className="font-semibold text-gray-900">Nouvelle Route</span>
          </Link>
        </div>
      </div>
    </div>
  )
}

