/**
 * Utility functions for role-based access control
 */

export const ROLES = {
  ADMIN: 'ADMIN',
  PASSENGER: 'PASSENGER',
  DRIVER: 'DRIVER',
} as const

export type Role = typeof ROLES[keyof typeof ROLES]

/**
 * Check if user has a specific role
 */
export const hasRole = (userRoles: string[] | undefined, role: Role): boolean => {
  if (!userRoles) return false
  return userRoles.includes(role)
}

/**
 * Check if user is an admin
 */
export const isAdmin = (userRoles: string[] | undefined): boolean => {
  return hasRole(userRoles, ROLES.ADMIN)
}

/**
 * Check if user is a passenger (regular user)
 */
export const isPassenger = (userRoles: string[] | undefined): boolean => {
  return hasRole(userRoles, ROLES.PASSENGER)
}

/**
 * Check if user is a driver
 */
export const isDriver = (userRoles: string[] | undefined): boolean => {
  return hasRole(userRoles, ROLES.DRIVER)
}

/**
 * Get user's primary role (highest priority)
 */
export const getPrimaryRole = (userRoles: string[] | undefined): Role | null => {
  if (!userRoles || userRoles.length === 0) return null
  if (isAdmin(userRoles)) return ROLES.ADMIN
  if (isDriver(userRoles)) return ROLES.DRIVER
  if (isPassenger(userRoles)) return ROLES.PASSENGER
  return null
}

/**
 * Get role display name in French
 */
export const getRoleDisplayName = (role: string): string => {
  switch (role) {
    case ROLES.ADMIN:
      return 'Administrateur'
    case ROLES.PASSENGER:
      return 'Passager'
    case ROLES.DRIVER:
      return 'Conducteur'
    default:
      return role
  }
}

/**
 * Get role badge color
 */
export const getRoleBadgeColor = (role: string): string => {
  switch (role) {
    case ROLES.ADMIN:
      return 'bg-purple-100 text-purple-700 border-purple-200'
    case ROLES.PASSENGER:
      return 'bg-blue-100 text-blue-700 border-blue-200'
    case ROLES.DRIVER:
      return 'bg-green-100 text-green-700 border-green-200'
    default:
      return 'bg-gray-100 text-gray-700 border-gray-200'
  }
}

