import { useEffect, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { Icon } from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { geolocationService, type EnrichedPositionResponse } from '@/services/geolocationService'
import { RefreshCw, Bus as BusIcon, Navigation } from 'lucide-react'
import toast from 'react-hot-toast'

// Custom bus icon (you can customize this)
const myBusIcon = new Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
})

export default function DriverTrack() {
  const [myPosition, setMyPosition] = useState<EnrichedPositionResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)

  useEffect(() => {
    loadPositions()
    const interval = setInterval(loadPositions, 5000) // Refresh every 5 seconds
    return () => clearInterval(interval)
  }, [])

  const loadPositions = async () => {
    try {
      const data = await geolocationService.getEnrichedPositions()
      
      // Find my bus position (you'll need to determine which bus belongs to the driver)
      // For now, we'll just show the most recent one
      const validPositions = data.filter((pos) => pos.bus !== null)
      if (validPositions.length > 0) {
        // TODO: Filter by driver's assigned bus
        setMyPosition(validPositions[0])
      }
      
      setIsLoading(false)
    } catch (error) {
      console.error('Error loading positions:', error)
      toast.error('Erreur de chargement de la position')
      setIsLoading(false)
    }
  }

  const handleRefresh = async () => {
    setRefreshing(true)
    await loadPositions()
    setRefreshing(false)
    toast.success('Position mise à jour')
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-[calc(100vh-8rem)]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600 text-lg">Chargement de votre position...</p>
        </div>
      </div>
    )
  }

  const center: [number, number] = myPosition
    ? [myPosition.position.latitude, myPosition.position.longitude]
    : [33.5731, -7.5898] // Casablanca default

  return (
    <div className="space-y-4 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold gradient-text mb-2">Ma Position GPS</h1>
          <p className="text-gray-600">Suivez votre position en temps réel</p>
        </div>
        <button
          onClick={handleRefresh}
          disabled={refreshing}
          className="btn btn-primary flex items-center gap-2 disabled:opacity-50"
        >
          <RefreshCw className={refreshing ? 'animate-spin' : ''} size={18} />
          Actualiser
        </button>
      </div>

      {/* Stats Cards */}
      {myPosition && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-primary-100 rounded-xl">
                <BusIcon className="text-primary-600" size={24} />
              </div>
              <div>
                <p className="text-sm text-gray-600">Bus</p>
                <p className="text-xl font-bold text-gray-900">{myPosition.bus?.busNumber || 'N/A'}</p>
              </div>
            </div>
          </div>
          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-accent-100 rounded-xl">
                <Navigation className="text-accent-600" size={24} />
              </div>
              <div>
                <p className="text-sm text-gray-600">Vitesse</p>
                <p className="text-xl font-bold text-gray-900">{myPosition.position.vitesse.toFixed(1)} km/h</p>
              </div>
            </div>
          </div>
          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-success-100 rounded-xl">
                <div className="text-success-600 font-bold text-lg">GPS</div>
              </div>
              <div>
                <p className="text-sm text-gray-600">Statut</p>
                <p className="text-xl font-bold text-success-700">Actif</p>
              </div>
            </div>
          </div>
          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-accent-100 rounded-xl">
                <div className="text-accent-600 font-bold text-lg">⏱️</div>
              </div>
              <div>
                <p className="text-sm text-gray-600">Dernière màj</p>
                <p className="text-sm font-semibold text-gray-900">
                  {new Date(myPosition.position.timestamp).toLocaleTimeString('fr-FR')}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Map */}
      <div className="h-[calc(100vh-24rem)] relative rounded-xl overflow-hidden shadow-lg border border-gray-200">
        <MapContainer
          center={center}
          zoom={15}
          scrollWheelZoom={true}
          className="h-full w-full z-0"
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {myPosition && myPosition.bus && (
            <Marker
              position={[myPosition.position.latitude, myPosition.position.longitude]}
              icon={myBusIcon}
            >
              <Popup>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 mb-3 pb-2 border-b">
                    <BusIcon className="text-primary-600" size={20} />
                    <strong className="text-gray-900">Votre Bus</strong>
                  </div>
                  <div className="space-y-1 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Numéro:</span>
                      <span className="font-medium">{myPosition.bus.busNumber}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Immatriculation:</span>
                      <span className="font-medium">{myPosition.bus.licensePlate}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Vitesse:</span>
                      <span className="font-semibold text-primary-600">
                        {myPosition.position.vitesse.toFixed(1)} km/h
                      </span>
                    </div>
                    <div className="pt-1 border-t text-xs text-gray-500">
                      {new Date(myPosition.position.timestamp).toLocaleString('fr-FR')}
                    </div>
                  </div>
                </div>
              </Popup>
            </Marker>
          )}
        </MapContainer>

        {!myPosition && (
          <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[1500] bg-yellow-50 border-2 border-yellow-200 px-6 py-4 rounded-xl shadow-xl">
            <p className="font-semibold text-yellow-800 text-center">
              ⚠️ Aucune position GPS détectée
            </p>
            <p className="text-sm text-yellow-700 mt-2 text-center">
              Assurez-vous d'avoir démarré le service
            </p>
          </div>
        )}
      </div>
    </div>
  )
}

