import { TrendingUp, Users, Bus, Ticket, CreditCard, MapPin, Clock, DollarSign } from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell, Legend } from 'recharts'

export default function AdminDashboard() {
  // Mock data for charts
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

  const userDistribution = [
    { name: 'Passagers', value: 3245, color: '#3B82F6' },
    { name: 'Conducteurs', value: 87, color: '#F59E0B' },
    { name: 'Admins', value: 5, color: '#EF4444' },
  ]

  const routeUsage = [
    { route: 'Ligne 12', passengers: 4500 },
    { route: 'Ligne 5', passengers: 3800 },
    { route: 'Ligne 1', passengers: 5200 },
    { route: 'Ligne 8', passengers: 3200 },
    { route: 'Ligne 3', passengers: 2900 },
  ]

  const stats = [
    {
      label: 'Utilisateurs Totaux',
      value: '3,337',
      change: '+12.5%',
      icon: Users,
      color: 'from-blue-500 to-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      label: 'Bus Actifs',
      value: '42',
      change: '+2',
      icon: Bus,
      color: 'from-red-500 to-red-600',
      bgColor: 'bg-red-50',
    },
    {
      label: 'Tickets Vendus (Mois)',
      value: '12,847',
      change: '+18.3%',
      icon: Ticket,
      color: 'from-green-500 to-green-600',
      bgColor: 'bg-green-50',
    },
    {
      label: 'Revenus (Mois)',
      value: '304k MAD',
      change: '+23.1%',
      icon: DollarSign,
      color: 'from-purple-500 to-purple-600',
      bgColor: 'bg-purple-50',
    },
    {
      label: 'Abonnements Actifs',
      value: '1,769',
      change: '+8.7%',
      icon: CreditCard,
      color: 'from-accent-500 to-accent-600',
      bgColor: 'bg-accent-50',
    },
    {
      label: 'Routes Actives',
      value: '24',
      change: '0',
      icon: MapPin,
      color: 'from-primary-500 to-primary-600',
      bgColor: 'bg-primary-50',
    },
    {
      label: 'Trajets Aujourd\'hui',
      value: '847',
      change: '+5.2%',
      icon: Clock,
      color: 'from-indigo-500 to-indigo-600',
      bgColor: 'bg-indigo-50',
    },
    {
      label: 'Taux d\'Occupation',
      value: '68%',
      change: '+3.4%',
      icon: TrendingUp,
      color: 'from-pink-500 to-pink-600',
      bgColor: 'bg-pink-50',
    },
  ]

  const recentActivities = [
    { type: 'user', message: 'Nouvel utilisateur inscrit: Ahmed El Fassi', time: 'Il y a 5 min' },
    { type: 'ticket', message: 'Ticket T-1245 acheté sur Ligne 12', time: 'Il y a 12 min' },
    { type: 'subscription', message: 'Abonnement Mensuel souscrit par Fatima Z.', time: 'Il y a 23 min' },
    { type: 'bus', message: 'Bus L12-03 est en maintenance', time: 'Il y a 45 min' },
    { type: 'alert', message: 'Retard signalé sur Ligne 5', time: 'Il y a 1h' },
  ]

  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'user': return <Users size={16} className="text-blue-600" />
      case 'ticket': return <Ticket size={16} className="text-green-600" />
      case 'subscription': return <CreditCard size={16} className="text-purple-600" />
      case 'bus': return <Bus size={16} className="text-red-600" />
      case 'alert': return <Clock size={16} className="text-accent-600" />
      default: return <Clock size={16} className="text-gray-600" />
    }
  }

  return (
    <div className="space-y-6">
      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <div
              key={index}
              className="card hover:shadow-lg transition-all duration-300 animate-slide-up"
              style={{ animationDelay: `${index * 50}ms` }}
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <p className="text-sm text-gray-600 font-medium mb-1">{stat.label}</p>
                  <p className="text-2xl font-bold text-gray-900 mb-1">{stat.value}</p>
                  <p className={`text-xs font-semibold ${stat.change.startsWith('+') ? 'text-success-600' : 'text-gray-500'}`}>
                    {stat.change} ce mois
                  </p>
                </div>
                <div className={`p-4 ${stat.bgColor} rounded-2xl`}>
                  <Icon className={`bg-gradient-to-br ${stat.color} text-transparent bg-clip-text`} size={28} />
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="card-gradient">
          <h3 className="text-xl font-bold text-gray-900 mb-6">Revenus Mensuels (MAD)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
              <XAxis dataKey="month" stroke="#6B7280" />
              <YAxis stroke="#6B7280" />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#FFF',
                  border: '1px solid #E5E7EB',
                  borderRadius: '12px',
                  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                }}
              />
              <Legend />
              <Bar dataKey="tickets" name="Tickets" fill="#3B82F6" radius={[8, 8, 0, 0]} />
              <Bar dataKey="abonnements" name="Abonnements" fill="#F59E0B" radius={[8, 8, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* User Distribution */}
        <div className="card-gradient">
          <h3 className="text-xl font-bold text-gray-900 mb-6">Répartition des Utilisateurs</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={userDistribution}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {userDistribution.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: '#FFF',
                  border: '1px solid #E5E7EB',
                  borderRadius: '12px',
                  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Route Usage */}
        <div className="card-gradient">
          <h3 className="text-xl font-bold text-gray-900 mb-6">Utilisation des Routes (Passagers)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={routeUsage}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
              <XAxis dataKey="route" stroke="#6B7280" />
              <YAxis stroke="#6B7280" />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#FFF',
                  border: '1px solid #E5E7EB',
                  borderRadius: '12px',
                  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                }}
              />
              <Line
                type="monotone"
                dataKey="passengers"
                stroke="#DC2626"
                strokeWidth={3}
                dot={{ fill: '#DC2626', r: 6 }}
                activeDot={{ r: 8 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Recent Activity */}
        <div className="card-gradient">
          <h3 className="text-xl font-bold text-gray-900 mb-6">Activité Récente</h3>
          <div className="space-y-4">
            {recentActivities.map((activity, index) => (
              <div
                key={index}
                className="flex items-start gap-3 p-3 bg-white rounded-xl border border-gray-100 hover:shadow-md transition-shadow"
              >
                <div className="p-2 bg-gray-50 rounded-lg flex-shrink-0">
                  {getActivityIcon(activity.type)}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-gray-900 font-medium">{activity.message}</p>
                  <p className="text-xs text-gray-500 mt-1">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card-gradient">
        <h3 className="text-xl font-bold text-gray-900 mb-4">Actions Rapides</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <button className="btn btn-primary flex flex-col items-center gap-2 py-6">
            <Users size={24} />
            <span className="text-sm">Gérer Utilisateurs</span>
          </button>
          <button className="btn btn-secondary flex flex-col items-center gap-2 py-6">
            <Bus size={24} />
            <span className="text-sm">Ajouter Bus</span>
          </button>
          <button className="btn btn-secondary flex flex-col items-center gap-2 py-6">
            <Ticket size={24} />
            <span className="text-sm">Créer Ticket</span>
          </button>
          <button className="btn btn-secondary flex flex-col items-center gap-2 py-6">
            <MapPin size={24} />
            <span className="text-sm">Nouvelle Route</span>
          </button>
        </div>
      </div>
    </div>
  )
}
