import { useEffect, useRef } from 'react'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { useBusTracking, BusPosition } from '@/hooks/useBusTracking'

// Fix default marker icon issue in Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

interface BusTrackingMapProps {
  center?: [number, number]
  zoom?: number
  height?: string
}

export default function BusTrackingMap({
  center = [33.5731, -7.5898], // Casablanca, Morocco
  zoom = 12,
  height = '600px',
}: BusTrackingMapProps) {
  const mapRef = useRef<L.Map | null>(null)
  const markersRef = useRef<{ [busId: number]: L.Marker }>({})
  const { busPositions, isConnected, error } = useBusTracking()

  // Initialize map
  useEffect(() => {
    if (!mapRef.current) {
      const map = L.map('bus-tracking-map').setView(center, zoom)

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 19,
      }).addTo(map)

      mapRef.current = map
    }

    return () => {
      if (mapRef.current) {
        mapRef.current.remove()
        mapRef.current = null
      }
    }
  }, [center, zoom])

  // Update bus markers
  useEffect(() => {
    if (!mapRef.current) return

    const map = mapRef.current
    const currentBusIds = new Set<number>()

    // Create custom bus icon
    const busIcon = L.divIcon({
      className: 'custom-bus-marker',
      html: `
        <div style="
          background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 3px solid white;
          box-shadow: 0 4px 12px rgba(0,0,0,0.3);
          cursor: pointer;
        ">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="white">
            <path d="M4 16c0 .88.39 1.67 1 2.22V20c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h8v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1.78c.61-.55 1-1.34 1-2.22V6c0-3.5-3.58-4-8-4s-8 .5-8 4v10zm3.5 1c-.83 0-1.5-.67-1.5-1.5S6.67 14 7.5 14s1.5.67 1.5 1.5S8.33 17 7.5 17zm9 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm1.5-6H6V6h12v5z"/>
          </svg>
        </div>
      `,
      iconSize: [40, 40],
      iconAnchor: [20, 20],
    })

    busPositions.forEach((position: BusPosition) => {
      if (!position.busId) return

      currentBusIds.add(position.busId)

      if (markersRef.current[position.busId]) {
        // Update existing marker
        markersRef.current[position.busId].setLatLng([position.latitude, position.longitude])
        markersRef.current[position.busId].setPopupContent(createPopupContent(position))
      } else {
        // Create new marker
        const marker = L.marker([position.latitude, position.longitude], { icon: busIcon })
          .addTo(map)
          .bindPopup(createPopupContent(position))

        markersRef.current[position.busId] = marker
      }
    })

    // Remove markers for buses that are no longer active
    Object.keys(markersRef.current).forEach((busIdStr) => {
      const busId = parseInt(busIdStr)
      if (!currentBusIds.has(busId)) {
        markersRef.current[busId].remove()
        delete markersRef.current[busId]
      }
    })
  }, [busPositions])

  const createPopupContent = (position: BusPosition): string => {
    const timestamp = new Date(position.timestamp).toLocaleString('fr-FR')
    return `
      <div style="font-family: system-ui, -apple-system, sans-serif; min-width: 200px;">
        <h3 style="margin: 0 0 8px 0; color: #dc3545; font-size: 16px; font-weight: 600;">
          🚌 Bus #${position.busId}
        </h3>
        <div style="font-size: 13px; color: #495057; line-height: 1.6;">
          <div style="margin-bottom: 4px;">
            <strong>Vitesse:</strong> ${position.vitesse.toFixed(1)} km/h
          </div>
          <div style="margin-bottom: 4px;">
            <strong>Direction:</strong> ${position.direction.toFixed(0)}°
          </div>
          <div style="margin-bottom: 4px;">
            <strong>Position:</strong> ${position.latitude.toFixed(6)}, ${position.longitude.toFixed(6)}
          </div>
          <div style="font-size: 11px; color: #6c757d; margin-top: 8px;">
            📅 ${timestamp}
          </div>
        </div>
      </div>
    `
  }

  return (
    <div className="relative">
      {/* Connection Status */}
      <div className="absolute top-4 right-4 z-[1000] bg-white rounded-lg shadow-lg p-3 flex items-center gap-2">
        <div className={`w-3 h-3 rounded-full ${isConnected ? 'bg-green-500 animate-pulse' : 'bg-red-500'}`} />
        <span className="text-sm font-medium">
          {isConnected ? 'Connecté' : 'Déconnecté'}
        </span>
        {busPositions.length > 0 && (
          <span className="text-xs text-gray-500 ml-2">
            {busPositions.length} bus actif{busPositions.length > 1 ? 's' : ''}
          </span>
        )}
      </div>

      {/* Error Message */}
      {error && (
        <div className="absolute top-20 right-4 z-[1000] bg-red-50 border border-red-200 rounded-lg shadow-lg p-3 max-w-xs">
          <p className="text-sm text-red-800">{error}</p>
        </div>
      )}

      {/* Map Container */}
      <div
        id="bus-tracking-map"
        style={{ height, width: '100%' }}
        className="rounded-xl shadow-2xl border-4 border-white"
      />

      {/* Legend */}
      <div className="absolute bottom-4 left-4 z-[1000] bg-white rounded-lg shadow-lg p-4">
        <h4 className="text-sm font-bold text-gray-900 mb-2">Légende</h4>
        <div className="flex items-center gap-2 text-xs text-gray-700">
          <div className="w-6 h-6 rounded-full bg-gradient-to-br from-red-500 to-red-700 flex items-center justify-center">
            <span className="text-white text-[10px]">🚌</span>
          </div>
          <span>Bus en circulation</span>
        </div>
      </div>
    </div>
  )
}
