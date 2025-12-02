import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { Home, Ticket, Calendar, MapPin, CreditCard, User, LogOut, Menu, X, Bus } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService } from '@/services/authService'
import { useState } from 'react'
import { isAdmin, isDriver, getPrimaryRole, getRoleDisplayName } from '@/utils/roles'
import Logo from '@/components/Logo'
import NotificationBell from '@/components/NotificationBell'

export default function Layout() {
  const { user, logout: logoutStore } = useAuthStore()
  const location = useLocation()
  const navigate = useNavigate()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const handleLogout = async () => {
    try {
      await authService.logout()
      logoutStore()
      toast.success('Déconnexion réussie')
      navigate('/login')
    } catch (error) {
      logoutStore()
      toast.error('Déconnexion')
      navigate('/login')
    }
  }

  // Navigation pour les passagers
  const passengerNavItems = [
    { path: '/', icon: Home, label: 'Tableau de Bord' },
    { path: '/tickets', icon: Ticket, label: 'Billets' },
    { path: '/schedules', icon: Calendar, label: 'Horaires' },
    { path: '/tracking', icon: MapPin, label: 'Suivi des Bus' },
    { path: '/subscriptions', icon: CreditCard, label: 'Abonnements' },
    { path: '/profile', icon: User, label: 'Profil' },
  ]

  // Navigation pour les conducteurs
  const driverNavItems = [
    { path: '/driver', icon: Home, label: 'Tableau de Bord' },
    { path: '/driver/start-service', icon: Bus, label: 'Démarrer Service' },
    { path: '/driver/track', icon: MapPin, label: 'Ma Position GPS' },
    { path: '/schedules', icon: Calendar, label: 'Mes Horaires' },
    { path: '/tracking', icon: MapPin, label: 'Suivi des Bus' },
    { path: '/profile', icon: User, label: 'Profil' },
  ]

  // Navigation pour les admins
  const adminNavItems = [
    { path: '/admin', icon: Home, label: 'Tableau de Bord' },
    { path: '/admin/users', icon: User, label: 'Utilisateurs' },
    { path: '/admin/buses', icon: Bus, label: 'Gestion des Bus' },
    { path: '/admin/schedules', icon: Calendar, label: 'Horaires' },
    { path: '/admin/subscriptions', icon: CreditCard, label: 'Abonnements' },
  ]

  // Déterminer les éléments de navigation selon le rôle
  const navItems = isAdmin(user?.roles) 
    ? adminNavItems
    : isDriver(user?.roles)
    ? driverNavItems
    : passengerNavItems

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-gray-50">
      {/* Sidebar - Desktop */}
      <aside className="hidden lg:flex fixed left-0 top-0 h-full w-72 bg-white shadow-2xl z-50 border-r border-gray-200 flex-col">
        <div className="p-6 border-b border-gray-200/50">
          <div className="mb-4 flex items-center justify-between">
            <Logo size="md" showText={true} showTagline={true} to="/" />
            <NotificationBell />
          </div>
          {user && (
            <div className="mt-3 p-3 bg-gradient-to-r from-primary-50 to-accent-50 rounded-xl">
              <p className="text-xs text-gray-600 mb-1">Connecté en tant que</p>
              <p className="font-bold text-gray-900">{user.firstName} {user.lastName}</p>
              <span className={`inline-block mt-2 px-3 py-1 rounded-full text-xs font-semibold border ${
                isAdmin(user.roles)
                  ? 'bg-red-100 text-red-700 border-red-200'
                  : isDriver(user.roles)
                  ? 'bg-green-100 text-green-700 border-green-200'
                  : 'bg-blue-100 text-blue-700 border-blue-200'
              }`}>
                {getRoleDisplayName(getPrimaryRole(user.roles) || '')}
              </span>
            </div>
          )}
        </div>
        
        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon
            const isActive = location.pathname === item.path
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                className={`group flex items-center gap-4 px-4 py-3 rounded-xl transition-all duration-200 ${
                  isActive
                    ? 'bg-gradient-to-r from-primary-600 to-primary-700 text-white shadow-lg shadow-primary-500/30'
                    : 'text-gray-700 hover:bg-gradient-to-r hover:from-primary-50 hover:to-accent-50 hover:text-primary-700'
                }`}
              >
                <Icon size={22} className={isActive ? 'text-white' : 'text-gray-500 group-hover:text-primary-600'} />
                <span className="font-semibold">{item.label}</span>
                {isActive && (
                  <div className="ml-auto w-2 h-2 bg-white rounded-full"></div>
                )}
              </Link>
            )
          })}
        </nav>
        
        <div className="p-4 border-t border-gray-200/50 space-y-3">
          <div className="px-4 py-3 bg-gradient-to-r from-gray-50 to-primary-50/50 rounded-xl">
            <p className="text-sm font-semibold text-gray-900">
              {user?.firstName} {user?.lastName}
            </p>
            <p className="text-xs text-gray-600 truncate">{user?.email}</p>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-600 hover:bg-red-50 transition-all duration-200 font-semibold group"
          >
            <LogOut size={20} className="group-hover:rotate-12 transition-transform" />
            <span>Déconnexion</span>
          </button>
        </div>
      </aside>

      {/* Mobile Header */}
      <header className="lg:hidden fixed top-0 left-0 right-0 bg-white/80 backdrop-blur-xl shadow-lg z-50 border-b border-gray-200/50">
        <div className="flex items-center justify-between p-4">
          <Logo size="sm" showText={true} to="/" />
          <div className="flex items-center gap-2">
            <NotificationBell />
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
            >
              {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>
        
        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="absolute top-full left-0 right-0 bg-white/95 backdrop-blur-xl border-b border-gray-200/50 shadow-xl animate-slide-down">
            <nav className="p-4 space-y-2">
              {navItems.map((item) => {
                const Icon = item.icon
                const isActive = location.pathname === item.path
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    onClick={() => setMobileMenuOpen(false)}
                    className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                      isActive
                        ? 'bg-gradient-to-r from-primary-600 to-primary-700 text-white'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    <Icon size={20} />
                    <span className="font-semibold">{item.label}</span>
                  </Link>
                )
              })}
              <div className="pt-4 border-t border-gray-200 mt-4">
                <div className="px-4 py-2 mb-2">
                  <p className="text-sm font-semibold text-gray-900">
                    {user?.firstName} {user?.lastName}
                  </p>
                  <p className="text-xs text-gray-600">{user?.email}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-600 hover:bg-red-50 transition-colors font-semibold"
                >
                  <LogOut size={20} />
                  <span>Déconnexion</span>
                </button>
              </div>
            </nav>
          </div>
        )}
      </header>

      {/* Main Content */}
      <main className="lg:ml-72 pt-16 lg:pt-0 p-4 md:p-8 min-h-screen">
        <div className="max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
