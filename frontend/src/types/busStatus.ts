/**
 * Types et constantes pour les statuts de bus
 * Synchronisé avec le backend BusStatus.java (schedule-service)
 */

export type BusStatus = 
  | 'ACTIVE' 
  | 'IN_SERVICE' 
  | 'MAINTENANCE' 
  | 'OUT_OF_SERVICE'
  | 'RETIRED'

/**
 * Labels français pour affichage dans l'interface
 */
export const BUS_STATUS_LABELS: Record<BusStatus, string> = {
  ACTIVE: 'Actif',
  IN_SERVICE: 'En service',
  MAINTENANCE: 'En maintenance',
  OUT_OF_SERVICE: 'Hors service',
  RETIRED: 'Retiré',
}

/**
 * Classes CSS pour les badges de statut
 */
export const BUS_STATUS_COLORS: Record<BusStatus, string> = {
  ACTIVE: 'bg-success-100 text-success-700 border-success-200',
  IN_SERVICE: 'bg-blue-100 text-blue-700 border-blue-200',
  MAINTENANCE: 'bg-orange-100 text-orange-700 border-orange-200',
  OUT_OF_SERVICE: 'bg-red-100 text-red-700 border-red-200',
  RETIRED: 'bg-gray-100 text-gray-700 border-gray-200',
}

/**
 * Liste des statuts de bus disponibles
 */
export const BUS_STATUSES: BusStatus[] = [
  'ACTIVE',
  'IN_SERVICE',
  'MAINTENANCE',
  'OUT_OF_SERVICE',
  'RETIRED',
]

/**
 * Obtenir le label d'un statut de bus
 * @param status - Le statut du bus
 * @returns Le label en français
 */
export const getBusStatusLabel = (status: string): string => {
  const upperStatus = status.toUpperCase() as BusStatus
  return BUS_STATUS_LABELS[upperStatus] || status
}

/**
 * Obtenir les classes CSS d'un statut de bus
 * @param status - Le statut du bus
 * @returns Les classes CSS pour le badge
 */
export const getBusStatusColor = (status: string): string => {
  const upperStatus = status.toUpperCase() as BusStatus
  return BUS_STATUS_COLORS[upperStatus] || 'bg-gray-100 text-gray-700 border-gray-200'
}
