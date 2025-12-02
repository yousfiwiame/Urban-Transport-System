/**
 * Types et constantes pour les statuts de ticket
 * Synchronisé avec le backend TicketStatus.java
 */

export type TicketStatus = 
  | 'ACTIVE' 
  | 'USED' 
  | 'EXPIRED' 
  | 'CANCELLED'

/**
 * Labels français pour affichage dans l'interface
 */
export const TICKET_STATUS_LABELS: Record<TicketStatus, string> = {
  ACTIVE: 'Actif',
  USED: 'Utilisé',
  EXPIRED: 'Expiré',
  CANCELLED: 'Annulé',
}

/**
 * Classes CSS pour les badges de statut
 */
export const TICKET_STATUS_COLORS: Record<TicketStatus, string> = {
  ACTIVE: 'bg-success-100 text-success-700 border-success-200',
  USED: 'bg-gray-100 text-gray-700 border-gray-200',
  EXPIRED: 'bg-red-100 text-red-700 border-red-200',
  CANCELLED: 'bg-orange-100 text-orange-700 border-orange-200',
}

/**
 * Liste des statuts de ticket disponibles
 */
export const TICKET_STATUSES: TicketStatus[] = [
  'ACTIVE',
  'USED',
  'EXPIRED',
  'CANCELLED',
]

/**
 * Obtenir le label d'un statut de ticket
 * @param status - Le statut du ticket
 * @returns Le label en français
 */
export const getTicketStatusLabel = (status: string): string => {
  const upperStatus = status.toUpperCase() as TicketStatus
  return TICKET_STATUS_LABELS[upperStatus] || status
}

/**
 * Obtenir les classes CSS d'un statut de ticket
 * @param status - Le statut du ticket
 * @returns Les classes CSS pour le badge
 */
export const getTicketStatusColor = (status: string): string => {
  const upperStatus = status.toUpperCase() as TicketStatus
  return TICKET_STATUS_COLORS[upperStatus] || 'bg-gray-100 text-gray-700 border-gray-200'
}

