import { useEffect, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { Icon } from 'leaflet'
import 'leaflet/dist/leaflet.css'
import BusSearchPanel from '@/components/bus-tracking/BusSearchPanel'
import TrajetInfoPanel from '@/components/bus-tracking/TrajetInfoPanel'
import { geolocationService, type EnrichedPositionResponse } from '@/services/geolocationService'
import { RefreshCw, Bus as BusIcon } from 'lucide-react'
import toast from 'react-hot-toast'

// Custom bus icon
const busIcon = new Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
})

export default function BusTracking() {
  const [positions, setPositions] = useState<EnrichedPositionResponse[]>([])
  const [filteredPositions, setFilteredPositions] = useState<EnrichedPositionResponse[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [selectedBusId, setSelectedBusId] = useState<number | null>(null)
  const [refreshing, setRefreshing] = useState(false)

  useEffect(() => {
    loadPositions()
    const interval = setInterval(loadPositions, 10000) // Refresh every 10 seconds
    return () => clearInterval(interval)
  }, [])

  const loadPositions = async () => {
    try {
      const data = await geolocationService.getEnrichedPositions()
      const validPositions = data.filter((pos) => pos.bus !== null && pos.bus !== undefined)
      setPositions(validPositions)
      if (filteredPositions.length === 0) {
        setFilteredPositions(validPositions)
      }
    } catch (error) {
      console.error('Error loading positions:', error)
      toast.error('Erreur de chargement des positions')
    }
  }

  const handleSearch = async (searchTerm: string) => {
    if (!searchTerm) {
      setFilteredPositions(positions)
      return
    }

    setIsLoading(true)
    try {
      const lowerSearch = searchTerm.toLowerCase()
      const filtered = positions.filter(
        (pos) =>
          pos.bus &&
          (pos.bus.busNumber?.toLowerCase().includes(lowerSearch) ||
            pos.bus.licensePlate?.toLowerCase().includes(lowerSearch) ||
            pos.bus.model?.toLowerCase().includes(lowerSearch))
      )
      
      if (filtered.length === 0) {
        setFilteredPositions([])
        toast('Aucun bus trouvé pour cette recherche')
      } else {
        setFilteredPositions(filtered)
        toast.success(`${filtered.length} bus trouvé(s)`)
      }
    } catch (error) {
      console.error('Error searching buses:', error)
      toast.error('Erreur de recherche')
    } finally {
      setIsLoading(false)
    }
  }

  const handleRefresh = async () => {
    setRefreshing(true)
    await loadPositions()
    setRefreshing(false)
    toast.success('Positions mises à jour')
  }

  return (
    <div className="h-[calc(100vh-8rem)] relative rounded-xl overflow-hidden shadow-lg border border-gray-200">
      {/* Search Panel */}
      <BusSearchPanel onSearch={handleSearch} />

      {/* Loading Overlay */}
      {isLoading && (
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[2000] bg-white/95 backdrop-blur-sm px-8 py-4 rounded-xl shadow-xl border border-gray-200">
          <div className="flex items-center gap-3">
            <RefreshCw className="animate-spin text-primary-600" size={24} />
            <span className="font-semibold text-gray-900">Recherche en cours...</span>
          </div>
        </div>
      )}

      {/* Map */}
      <MapContainer
        center={[33.5731, -7.5898]} // Casablanca
        zoom={12}
        scrollWheelZoom={true}
        className="h-full w-full z-0"
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {filteredPositions.map((enrichedPos) => {
          if (!enrichedPos.bus) return null
          const { position, bus } = enrichedPos
          return (
            <Marker
              key={position.id}
              position={[position.latitude, position.longitude]}
              icon={busIcon}
            >
              <Popup className="min-w-[250px]">
                <div className="space-y-2">
                  <div className="flex items-center gap-2 mb-3 pb-2 border-b">
                    <BusIcon className="text-primary-600" size={20} />
                    <strong className="text-gray-900">{bus.busNumber || bus.licensePlate || 'Inconnu'}</strong>
                  </div>
                  <div className="space-y-1 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Immatriculation:</span>
                      <span className="font-medium">{bus.licensePlate || 'N/A'}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Modèle:</span>
                      <span className="font-medium">{bus.model || 'N/A'}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Marque:</span>
                      <span className="font-medium">{bus.manufacturer || 'N/A'}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Capacité:</span>
                      <span className="font-medium">{bus.seatingCapacity + bus.standingCapacity} places</span>
                    </div>
                    <div className="pt-1 border-t flex justify-between">
                      <span className="text-gray-600">Vitesse:</span>
                      <span className="font-semibold text-primary-600">{position.vitesse.toFixed(1)} km/h</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Statut:</span>
                      <span
                        className={`px-2 py-0.5 rounded text-xs font-medium ${
                          bus.status === 'IN_SERVICE' || bus.status === 'ACTIVE'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-gray-100 text-gray-800'
                        }`}
                      >
                        {bus.status || 'Inconnu'}
                      </span>
                    </div>
                    <div className="pt-1 border-t text-xs text-gray-500">
                      {new Date(position.timestamp).toLocaleString('fr-FR')}
                    </div>
                  </div>
                  <button
                    onClick={() => setSelectedBusId(bus.id)}
                    className="w-full mt-3 btn btn-primary text-sm py-2"
                  >
                    📊 Voir Détails du Trajet
                  </button>
                </div>
              </Popup>
            </Marker>
          )
        })}
      </MapContainer>

      {/* No buses message */}
      {!isLoading && filteredPositions.length === 0 && positions.length > 0 && (
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[1500] bg-yellow-50 border-2 border-yellow-200 px-6 py-4 rounded-xl shadow-xl">
          <p className="font-semibold text-yellow-800 text-center">
            ⚠️ Aucun bus trouvé pour ces critères de recherche
          </p>
        </div>
      )}

      {/* Bus counter */}
      <div className="absolute bottom-4 right-4 z-[1000] bg-white/95 backdrop-blur-sm px-4 py-3 rounded-xl shadow-lg border border-gray-200">
        <div className="flex items-center gap-2">
          <BusIcon className="text-primary-600" size={20} />
          <span className="font-semibold text-gray-900">
            Bus affichés:{' '}
            <span className="text-primary-600">{filteredPositions.length}</span> / {positions.length}
          </span>
        </div>
      </div>

      {/* Refresh button */}
      <button
        onClick={handleRefresh}
        disabled={refreshing}
        className="absolute bottom-4 left-4 z-[1000] bg-white/95 backdrop-blur-sm hover:bg-white px-4 py-3 rounded-xl shadow-lg border border-gray-200 flex items-center gap-2 font-semibold text-gray-900 transition-all hover:shadow-xl disabled:opacity-50"
      >
        <RefreshCw className={refreshing ? 'animate-spin' : ''} size={18} />
        Actualiser
      </button>

      {/* Trip Info Panel */}
      {selectedBusId && (
        <TrajetInfoPanel busId={selectedBusId} onClose={() => setSelectedBusId(null)} />
      )}
    </div>
  )
}
