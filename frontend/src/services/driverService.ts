import api from '@/lib/api'

// Types pour les statistiques du conducteur
export interface DriverDailyStats {
  tripsToday: number
  drivingHours: number
  passengersTransported: number
  busStatus: string
  busId?: string
  busNumber?: string
}

// Types pour les trajets
export interface DriverTrip {
  id: number
  scheduleId: number
  departure: string
  arrival: string
  route: string
  routeName: string
  routeId: number
  estimatedPassengers?: number
  actualPassengers?: number
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
  departureTime: string
  arrivalTime: string
}

// Types pour les performances
export interface DriverPerformance {
  punctuality: number // Pourcentage de ponctualité
  averageRating: number // Note moyenne sur 5
  totalTripsThisMonth: number
  totalTripsLastMonth: number
  completionRate: number // Taux de complétion des trajets
  customerSatisfaction: number // Satisfaction client (0-5)
}

/**
 * Service pour gérer les données des conducteurs
 */
export const driverService = {
  /**
   * Récupère les statistiques quotidiennes d'un conducteur
   * GET /api/drivers/{driverId}/stats/today
   */
  getTodayStats: async (driverId: string): Promise<DriverDailyStats> => {
    try {
      const response = await api.get<DriverDailyStats>(`/api/drivers/${driverId}/stats/today`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des stats du jour:', error)
      // Retourner des données par défaut en cas d'erreur
      return {
        tripsToday: 0,
        drivingHours: 0,
        passengersTransported: 0,
        busStatus: 'UNKNOWN'
      }
    }
  },

  /**
   * Récupère les trajets à venir d'un conducteur
   * GET /api/drivers/{driverId}/trips/upcoming
   */
  getUpcomingTrips: async (driverId: string): Promise<DriverTrip[]> => {
    try {
      const response = await api.get<DriverTrip[]>(`/api/drivers/${driverId}/trips/upcoming`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des trajets à venir:', error)
      return []
    }
  },

  /**
   * Récupère les trajets complétés aujourd'hui par un conducteur
   * GET /api/drivers/{driverId}/trips/completed/today
   */
  getCompletedTripsToday: async (driverId: string): Promise<DriverTrip[]> => {
    try {
      const response = await api.get<DriverTrip[]>(`/api/drivers/${driverId}/trips/completed/today`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des trajets complétés:', error)
      return []
    }
  },

  /**
   * Récupère les performances d'un conducteur
   * GET /api/drivers/{driverId}/performance
   */
  getPerformance: async (driverId: string): Promise<DriverPerformance> => {
    try {
      const response = await api.get<DriverPerformance>(`/api/drivers/${driverId}/performance`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des performances:', error)
      return {
        punctuality: 0,
        averageRating: 0,
        totalTripsThisMonth: 0,
        totalTripsLastMonth: 0,
        completionRate: 0,
        customerSatisfaction: 0
      }
    }
  },

  /**
   * Marque un trajet comme commencé
   * POST /api/drivers/{driverId}/trips/{tripId}/start
   */
  startTrip: async (driverId: string, tripId: number): Promise<DriverTrip> => {
    const response = await api.post<DriverTrip>(`/api/drivers/${driverId}/trips/${tripId}/start`)
    return response.data
  },

  /**
   * Marque un trajet comme terminé
   * POST /api/drivers/{driverId}/trips/{tripId}/complete
   */
  completeTrip: async (driverId: string, tripId: number, actualPassengers?: number): Promise<DriverTrip> => {
    const response = await api.post<DriverTrip>(
      `/api/drivers/${driverId}/trips/${tripId}/complete`,
      { actualPassengers }
    )
    return response.data
  },

  /**
   * Signale un incident ou un retard
   * POST /api/drivers/{driverId}/trips/{tripId}/incident
   */
  reportIncident: async (driverId: string, tripId: number, incidentType: string, description: string): Promise<void> => {
    await api.post(`/api/drivers/${driverId}/trips/${tripId}/incident`, {
      incidentType,
      description
    })
  }
}

