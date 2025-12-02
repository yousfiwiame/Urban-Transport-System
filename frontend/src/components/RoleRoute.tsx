import { Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { Role, ROLES } from '@/utils/roles'
import toast from 'react-hot-toast'
import { useEffect, useState } from 'react'

interface RoleRouteProps {
  children: React.ReactNode
  allowedRoles: Role[]
}

export function RoleRoute({ children, allowedRoles }: RoleRouteProps) {
  const { user, isAuthenticated } = useAuthStore()
  const [hasAccess, setHasAccess] = useState<boolean | null>(null)

  useEffect(() => {
    if (!isAuthenticated) {
      setHasAccess(false)
      return
    }

    if (!user?.roles || user.roles.length === 0) {
      setHasAccess(false)
      toast.error('Aucun rôle attribué')
      return
    }

    const userHasAllowedRole = allowedRoles.some(role => user.roles.includes(role))
    setHasAccess(userHasAllowedRole)

    if (!userHasAllowedRole) {
      toast.error('Accès refusé - Rôle non autorisé')
    }
  }, [user, isAuthenticated, allowedRoles])

  if (hasAccess === null) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Vérification des autorisations...</p>
        </div>
      </div>
    )
  }

  if (!hasAccess) {
    return <Navigate to="/" replace />
  }

  return <>{children}</>
}

export function AdminRoute({ children }: { children: React.ReactNode }) {
  return <RoleRoute allowedRoles={[ROLES.ADMIN]}>{children}</RoleRoute>
}

export function DriverRoute({ children }: { children: React.ReactNode }) {
  return <RoleRoute allowedRoles={[ROLES.DRIVER, ROLES.ADMIN]}>{children}</RoleRoute>
}

export function PassengerRoute({ children }: { children: React.ReactNode }) {
  return <RoleRoute allowedRoles={[ROLES.PASSENGER]}>{children}</RoleRoute>
}

export function PassengerOrDriverRoute({ children }: { children: React.ReactNode }) {
  return <RoleRoute allowedRoles={[ROLES.PASSENGER, ROLES.DRIVER, ROLES.ADMIN]}>{children}</RoleRoute>
}

