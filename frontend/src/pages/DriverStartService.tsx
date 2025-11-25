import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { busService } from '@/services/busService'
import { routeService } from '@/services/routeService'
import { useGPSTracking } from '@/hooks/useGPSTracking'
import { Bus, MapPin, Navigation, Play, Square, Activity, Clock } from 'lucide-react'
import toast from 'react-hot-toast'

export default function DriverStartService() {
  const [selectedBusId, setSelectedBusId] = useState<number | null>(null)
  const [selectedRouteId, setSelectedRouteId] = useState<number | null>(null)
  const [isServiceActive, setIsServiceActive] = useState(false)

  // Récupérer la liste des bus
  const { data: busesData, isLoading: busesLoading } = useQuery({
    queryKey: ['buses'],
    queryFn: () => busService.getAllBuses(0, 100),
  })

  // Récupérer la liste des routes
  const { data: routesData, isLoading: routesLoading } = useQuery({
    queryKey: ['routes'],
    queryFn: () => routeService.getAllRoutes(0, 100),
  })

  // Hook de tracking GPS
  const {
    isTracking,
    lastPosition,
    positionsSent,
    error: gpsError,
    startTracking,
    stopTracking,
  } = useGPSTracking({
    busId: selectedBusId || 0,
    updateInterval: 10000, // 10 secondes
    onSuccess: (position) => {
      console.log('Position envoyée:', position.coords)
    },
    onError: (error) => {
      console.error('Erreur GPS:', error)
    },
  })

  // Démarrer le service
  const handleStartService = () => {
    if (!selectedBusId) {
      toast.error('Veuillez sélectionner un bus')
      return
    }
    if (!selectedRouteId) {
      toast.error('Veuillez sélectionner une route')
      return
    }

    startTracking()
    setIsServiceActive(true)
  }

  // Arrêter le service
  const handleStopService = () => {
    stopTracking()
    setIsServiceActive(false)
  }

  // Bus sélectionné
  const selectedBus = busesData?.content.find((b) => b.id === selectedBusId)
  const selectedRoute = routesData?.content.find((r) => r.id === selectedRouteId)

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold gradient-text mb-2">Démarrer le Service</h1>
        <p className="text-gray-600 text-lg">
          Sélectionnez votre bus et votre route, puis démarrez le tracking GPS
        </p>
      </div>

      {/* État du Service */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">État du Service</h2>
          <span
            className={`px-4 py-2 rounded-full text-sm font-bold ${
              isServiceActive
                ? 'bg-success-100 text-success-700'
                : 'bg-gray-100 text-gray-700'
            }`}
          >
            {isServiceActive ? '🟢 En Service' : '⚪ Hors Service'}
          </span>
        </div>

        {isServiceActive && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div className="p-4 bg-primary-50 rounded-xl">
              <div className="flex items-center gap-2 mb-2">
                <Activity className="text-primary-600" size={20} />
                <span className="text-sm text-gray-600">GPS Actif</span>
              </div>
              <p className="text-2xl font-bold text-gray-900">
                {isTracking ? 'Oui ✅' : 'Non ❌'}
              </p>
            </div>

            <div className="p-4 bg-accent-50 rounded-xl">
              <div className="flex items-center gap-2 mb-2">
                <Navigation className="text-accent-600" size={20} />
                <span className="text-sm text-gray-600">Positions Envoyées</span>
              </div>
              <p className="text-2xl font-bold text-gray-900">{positionsSent}</p>
            </div>

            <div className="p-4 bg-success-50 rounded-xl">
              <div className="flex items-center gap-2 mb-2">
                <Clock className="text-success-600" size={20} />
                <span className="text-sm text-gray-600">Dernière Position</span>
              </div>
              <p className="text-sm font-bold text-gray-900">
                {lastPosition
                  ? new Date(lastPosition.timestamp).toLocaleTimeString('fr-FR')
                  : 'Aucune'}
              </p>
            </div>
          </div>
        )}

        {gpsError && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-xl mb-6">
            <p className="text-red-700 font-semibold">❌ {gpsError}</p>
          </div>
        )}
      </div>

      {/* Sélection Bus et Route */}
      {!isServiceActive && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Sélection Bus */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl">
                <Bus className="text-white" size={24} />
              </div>
              <h2 className="text-2xl font-bold text-gray-900">Sélectionner le Bus</h2>
            </div>

            {busesLoading ? (
              <div className="text-center py-8">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
                <p className="text-gray-500 mt-4">Chargement des bus...</p>
              </div>
            ) : (
              <div className="space-y-3">
                {busesData?.content.map((bus) => (
                  <button
                    key={bus.id}
                    onClick={() => setSelectedBusId(bus.id)}
                    className={`w-full p-4 rounded-xl border-2 transition-all text-left ${
                      selectedBusId === bus.id
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-gray-200 hover:border-primary-300'
                    }`}
                  >
                    <div className="font-bold text-lg text-gray-900">{bus.busNumber}</div>
                    <div className="text-sm text-gray-600">
                      {bus.manufacturer} {bus.model}
                    </div>
                    <div className="text-xs text-gray-500 mt-1">{bus.licensePlate}</div>
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Sélection Route */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-3 bg-gradient-to-br from-accent-500 to-accent-600 rounded-xl">
                <MapPin className="text-white" size={24} />
              </div>
              <h2 className="text-2xl font-bold text-gray-900">Sélectionner la Route</h2>
            </div>

            {routesLoading ? (
              <div className="text-center py-8">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-accent-600 mx-auto"></div>
                <p className="text-gray-500 mt-4">Chargement des routes...</p>
              </div>
            ) : (
              <div className="space-y-3">
                {routesData?.content.map((route) => (
                  <button
                    key={route.id}
                    onClick={() => setSelectedRouteId(route.id)}
                    className={`w-full p-4 rounded-xl border-2 transition-all text-left ${
                      selectedRouteId === route.id
                        ? 'border-accent-500 bg-accent-50'
                        : 'border-gray-200 hover:border-accent-300'
                    }`}
                  >
                    <div className="font-bold text-lg text-gray-900">
                      {route.routeNumber} - {route.routeName}
                    </div>
                    <div className="text-sm text-gray-600">
                      {route.origin} → {route.destination}
                    </div>
                    <div className="text-xs text-gray-500 mt-1">
                      {route.distance} km • {route.estimatedDuration} min
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Informations du service actif */}
      {isServiceActive && selectedBus && selectedRoute && (
        <div className="card bg-gradient-to-br from-primary-50 to-accent-50">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Service en Cours</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <p className="text-sm text-gray-600 mb-2">Bus Assigné</p>
              <div className="p-4 bg-white rounded-xl">
                <p className="font-bold text-lg text-gray-900">{selectedBus.busNumber}</p>
                <p className="text-sm text-gray-600">
                  {selectedBus.manufacturer} {selectedBus.model}
                </p>
              </div>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-2">Route Assignée</p>
              <div className="p-4 bg-white rounded-xl">
                <p className="font-bold text-lg text-gray-900">
                  {selectedRoute.routeNumber} - {selectedRoute.routeName}
                </p>
                <p className="text-sm text-gray-600">
                  {selectedRoute.origin} → {selectedRoute.destination}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Boutons d'action */}
      <div className="flex justify-center">
        {!isServiceActive ? (
          <button
            onClick={handleStartService}
            disabled={!selectedBusId || !selectedRouteId}
            className="flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-success-500 to-success-600 text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed hover:scale-105"
          >
            <Play size={24} />
            Démarrer le Service
          </button>
        ) : (
          <button
            onClick={handleStopService}
            className="flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-xl transition-all hover:scale-105"
          >
            <Square size={24} />
            Arrêter le Service
          </button>
        )}
      </div>
    </div>
  )
}

