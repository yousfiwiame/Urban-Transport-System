import api from '@/lib/api'

export interface LigneBus {
  idLigne: string
  numeroLigne: string
  nomLigne: string
  couleur: string
}

export interface Direction {
  idDirection: string
  nomDirection: string
  pointDepart: string
  pointArrivee: string
}

export interface Bus {
  idBus: string
  immatriculation: string
  modele: string
  marque: string
  capacite: number
  statut: string
  ligneActuelle?: LigneBus
  directionActuelle?: Direction
}

export interface PositionBus {
  idPosition: string
  latitude: number
  longitude: number
  vitesse: number
  timestamp: string
  bus: Bus | null
}

export interface TrajetInfo {
  idBus: string
  immatriculation: string
  ligne: {
    numeroLigne: string
    nomLigne: string
    couleur: string
  } | null
  direction: {
    nomDirection: string
    pointDepart: string
    pointArrivee: string
  } | null
  latitudeActuelle: number
  longitudeActuelle: number
  vitesseActuelle: number
  derniereMiseAJour: string
  heureDepart: string
  distanceParcourue: number
  dureeTrajetMinutes: number
  nombreArretsEffectues: number
  prochainArret?: string
  distanceProchainArret?: number
  tempsEstimeProchainArret?: number
}

export interface EnrichedPositionResponse {
  position: {
    id: string
    busId: number
    latitude: number
    longitude: number
    vitesse: number
    direction: number
    timestamp: string
  }
  bus: {
    id: number
    busNumber: string
    licensePlate: string
    model: string
    manufacturer: string
    seatingCapacity: number
    standingCapacity: number
    year: number
    status: string
    lastMaintenance?: string
    nextMaintenance?: string
  } | null
}

export const geolocationService = {
  // New enriched endpoint
  getEnrichedPositions: async (): Promise<EnrichedPositionResponse[]> => {
    const response = await api.get<EnrichedPositionResponse[]>('/api/tracking/positions-enriched')
    return response.data
  },

  // Legacy endpoint (kept for compatibility)
  getAllPositions: async (): Promise<PositionBus[]> => {
    const response = await api.get<PositionBus[]>('/api/positions')
    return response.data
  },

  getPositionsByBus: async (busId: string): Promise<PositionBus[]> => {
    const response = await api.get<PositionBus[]>(`/api/positions/bus/${busId}`)
    return response.data
  },

  getAllLignes: async (): Promise<LigneBus[]> => {
    const response = await api.get<LigneBus[]>('/api/lignes/actives')
    return response.data
  },

  getDirectionsByLigne: async (ligneId: string): Promise<Direction[]> => {
    const response = await api.get<Direction[]>(`/api/directions/ligne/${ligneId}`)
    return response.data
  },

  searchBuses: async (ligneId?: string, directionId?: string): Promise<Bus[]> => {
    const params = new URLSearchParams()
    if (ligneId) params.append('ligneId', ligneId)
    if (directionId) params.append('directionId', directionId)
    const response = await api.get<Bus[]>(`/api/bus/search?${params.toString()}`)
    return response.data
  },

  getTrajetInfo: async (busId: string): Promise<TrajetInfo> => {
    const response = await api.get<TrajetInfo>(`/api/trajet/bus/${busId}`)
    return response.data
  },
}

