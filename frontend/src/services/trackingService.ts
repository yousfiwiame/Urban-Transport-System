import api from '@/lib/api'

/**
 * Informations complètes d'un bus depuis schedule-service
 */
export interface BusInfo {
  id: number
  busNumber: string
  licensePlate: string
  model: string
  manufacturer: string
  year: number
  capacity: number
  status: string
  isAccessible: boolean
}

/**
 * Information sur une ligne de bus
 */
export interface LigneBus {
  idLigne: string
  numeroLigne: string
  nomLigne: string
  couleur: string
}

/**
 * Information sur une direction
 */
export interface Direction {
  idDirection: string
  nomDirection: string
  pointDepart: string
  pointArrivee: string
}

/**
 * Position enrichie avec toutes les informations du bus
 * Cette interface combine les données de geolocation-service et schedule-service
 */
export interface EnrichedPositionBus {
  idPosition: string
  busId: number
  latitude: number
  longitude: number
  vitesse: number
  timestamp: string
  bus: BusInfo                    // ✅ Infos complètes depuis schedule-service
  ligneActuelle?: LigneBus        // Optionnel : ligne actuelle
  directionActuelle?: Direction   // Optionnel : direction actuelle
}

/**
 * Service pour le suivi en temps réel des bus
 * Utilise l'endpoint enrichi qui combine geolocation-service et schedule-service
 */
export const trackingService = {
  /**
   * ✨ Récupère toutes les positions avec les informations complètes des bus
   * Cet endpoint appelle schedule-service pour enrichir chaque position avec les détails du bus
   */
  getAllPositionsEnriched: async (): Promise<EnrichedPositionBus[]> => {
    const response = await api.get<EnrichedPositionBus[]>('/api/tracking/positions-enriched')
    return response.data
  },

  /**
   * Récupère les positions enrichies d'un bus spécifique
   */
  getBusPositionEnriched: async (busId: number): Promise<EnrichedPositionBus | null> => {
    try {
      const response = await api.get<EnrichedPositionBus>(`/api/tracking/bus/${busId}/position`)
      return response.data
    } catch (error) {
      console.error(`Error fetching position for bus ${busId}:`, error)
      return null
    }
  },
}

