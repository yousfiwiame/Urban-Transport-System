import api from '@/lib/api'

/**
 * Interface pour envoyer une position GPS au backend
 */
export interface GPSPosition {
  busId: number
  latitude: number
  longitude: number
  altitude?: number
  precision?: number
  vitesse: number  // en km/h
  direction: number  // en degrés (0-360)
}

/**
 * Interface pour la réponse du backend
 */
export interface GPSPositionResponse {
  idPosition: string
  busId: number
  latitude: number
  longitude: number
  altitude: number
  precision: number
  vitesse: number
  direction: number
  timestamp: string
}

/**
 * Service pour gérer l'envoi des positions GPS
 */
export const gpsService = {
  /**
   * Envoie une position GPS au backend
   * POST /api/positions/driver
   */
  sendPosition: async (position: GPSPosition): Promise<GPSPositionResponse> => {
    const response = await api.post<GPSPositionResponse>('/api/positions/driver', position)
    return response.data
  },

  /**
   * Calcule la vitesse entre deux positions GPS
   * @param lat1 Latitude position 1
   * @param lon1 Longitude position 1
   * @param lat2 Latitude position 2
   * @param lon2 Longitude position 2
   * @param timeInSeconds Temps écoulé en secondes
   * @returns Vitesse en km/h
   */
  calculateSpeed: (
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number,
    timeInSeconds: number
  ): number => {
    // Formule de Haversine pour calculer la distance
    const R = 6371 // Rayon de la Terre en km
    const dLat = (lat2 - lat1) * (Math.PI / 180)
    const dLon = (lon2 - lon1) * (Math.PI / 180)
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * (Math.PI / 180)) *
        Math.cos(lat2 * (Math.PI / 180)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    const distance = R * c // Distance en km

    // Vitesse = distance / temps
    const speed = (distance / timeInSeconds) * 3600 // Convertir en km/h
    return Math.round(speed * 10) / 10 // Arrondir à 1 décimale
  },

  /**
   * Calcule la direction (cap) entre deux positions GPS
   * @param lat1 Latitude position 1
   * @param lon1 Longitude position 1
   * @param lat2 Latitude position 2
   * @param lon2 Longitude position 2
   * @returns Direction en degrés (0-360)
   */
  calculateDirection: (lat1: number, lon1: number, lat2: number, lon2: number): number => {
    const dLon = (lon2 - lon1) * (Math.PI / 180)
    const y = Math.sin(dLon) * Math.cos(lat2 * (Math.PI / 180))
    const x =
      Math.cos(lat1 * (Math.PI / 180)) * Math.sin(lat2 * (Math.PI / 180)) -
      Math.sin(lat1 * (Math.PI / 180)) *
        Math.cos(lat2 * (Math.PI / 180)) *
        Math.cos(dLon)
    let direction = Math.atan2(y, x) * (180 / Math.PI)
    direction = (direction + 360) % 360 // Normaliser entre 0-360
    return Math.round(direction)
  },
}

