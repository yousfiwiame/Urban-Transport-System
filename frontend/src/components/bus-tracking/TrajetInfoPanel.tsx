import { useState, useEffect } from 'react'
import { X, RefreshCw, Bus, MapPin, Clock, TrendingUp, Navigation } from 'lucide-react'
import { geolocationService, TrajetInfo } from '@/services/geolocationService'

interface TrajetInfoPanelProps {
  busId: string
  onClose: () => void
}

export default function TrajetInfoPanel({ busId, onClose }: TrajetInfoPanelProps) {
  const [trajetInfo, setTrajetInfo] = useState<TrajetInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadTrajetInfo()
    const interval = setInterval(loadTrajetInfo, 30000)
    return () => clearInterval(interval)
  }, [busId])

  const loadTrajetInfo = async () => {
    setLoading(true)
    try {
      const data = await geolocationService.getTrajetInfo(busId)
      setTrajetInfo(data)
      setError(null)
    } catch (err) {
      console.error('Error loading trajet info:', err)
      setError('Impossible de charger les informations du trajet')
    } finally {
      setLoading(false)
    }
  }

  if (loading && !trajetInfo) {
    return (
      <div className="absolute top-4 right-4 z-[1000] bg-white rounded-xl shadow-2xl w-96 max-h-[90vh] overflow-auto border border-gray-100">
        <div className="p-12 text-center">
          <RefreshCw className="animate-spin mx-auto mb-4 text-primary-600" size={32} />
          <p className="text-gray-600">Chargement...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="absolute top-4 right-4 z-[1000] bg-white rounded-xl shadow-2xl w-96 border border-gray-100">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-bold text-gray-900">Erreur</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X size={24} />
            </button>
          </div>
          <div className="text-red-600 text-center py-8">{error}</div>
        </div>
      </div>
    )
  }

  if (!trajetInfo) return null

  return (
    <div className="absolute top-4 right-4 z-[1000] bg-white rounded-xl shadow-2xl w-96 max-h-[90vh] overflow-auto border border-gray-100">
      <div className="sticky top-0 bg-white border-b border-gray-200 p-4 flex items-center justify-between z-10">
        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
          <TrendingUp className="text-primary-600" size={20} />
          Informations du Trajet
        </h3>
        <button
          onClick={onClose}
          className="text-gray-400 hover:text-gray-600 transition-colors p-1 hover:bg-gray-100 rounded"
        >
          <X size={20} />
        </button>
      </div>

      <div className="p-6 space-y-6">
        {/* Bus Info */}
        <div className="bg-gradient-to-r from-primary-50 to-blue-50 rounded-lg p-4 border border-primary-100">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 bg-primary-600 rounded-lg">
              <Bus className="text-white" size={20} />
            </div>
            <div>
              <h4 className="font-bold text-gray-900">{trajetInfo.immatriculation}</h4>
              {trajetInfo.ligne && (
                <p className="text-sm text-gray-600">
                  Ligne{' '}
                  <span
                    className="font-bold px-2 py-0.5 rounded"
                    style={{ color: trajetInfo.ligne.couleur }}
                  >
                    {trajetInfo.ligne.numeroLigne}
                  </span>{' '}
                  - {trajetInfo.ligne.nomLigne}
                </p>
              )}
            </div>
          </div>
          {trajetInfo.direction && (
            <div className="mt-3 pt-3 border-t border-primary-200">
              <p className="text-sm font-medium text-gray-700 mb-1">
                {trajetInfo.direction.nomDirection}
              </p>
              <p className="text-xs text-gray-600">
                {trajetInfo.direction.pointDepart} → {trajetInfo.direction.pointArrivee}
              </p>
            </div>
          )}
        </div>

        {/* Current Position */}
        <div className="space-y-3">
          <h4 className="font-semibold text-gray-900 flex items-center gap-2">
            <MapPin className="text-primary-600" size={18} />
            Position Actuelle
          </h4>
          <div className="bg-gray-50 rounded-lg p-4 space-y-2">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Vitesse</span>
              <span className="font-bold text-primary-600">
                {trajetInfo.vitesseActuelle.toFixed(1)} km/h
              </span>
            </div>
            <div className="pt-2 border-t border-gray-200">
              <p className="text-xs text-gray-500 mb-1">Coordonnées</p>
              <p className="text-xs font-mono text-gray-700">
                Lat: {trajetInfo.latitudeActuelle.toFixed(6)}
              </p>
              <p className="text-xs font-mono text-gray-700">
                Lng: {trajetInfo.longitudeActuelle.toFixed(6)}
              </p>
            </div>
            <div className="pt-2 border-t border-gray-200">
              <p className="text-xs text-gray-500">Dernière mise à jour</p>
              <p className="text-sm font-medium text-gray-700">
                {new Date(trajetInfo.derniereMiseAJour).toLocaleTimeString()}
              </p>
            </div>
          </div>
        </div>

        {/* Trip Statistics */}
        <div className="space-y-3">
          <h4 className="font-semibold text-gray-900 flex items-center gap-2">
            <Clock className="text-primary-600" size={18} />
            Statistiques du Trajet
          </h4>
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-gray-50 rounded-lg p-3">
              <p className="text-xs text-gray-500 mb-1">Départ</p>
              <p className="text-sm font-semibold text-gray-900">
                {new Date(trajetInfo.heureDepart).toLocaleTimeString()}
              </p>
            </div>
            <div className="bg-gray-50 rounded-lg p-3">
              <p className="text-xs text-gray-500 mb-1">Distance</p>
              <p className="text-sm font-semibold text-gray-900">
                {trajetInfo.distanceParcourue} km
              </p>
            </div>
            <div className="bg-gray-50 rounded-lg p-3">
              <p className="text-xs text-gray-500 mb-1">Durée</p>
              <p className="text-sm font-semibold text-gray-900">
                {trajetInfo.dureeTrajetMinutes} min
              </p>
            </div>
            <div className="bg-gray-50 rounded-lg p-3">
              <p className="text-xs text-gray-500 mb-1">Arrêts</p>
              <p className="text-sm font-semibold text-gray-900">
                {trajetInfo.nombreArretsEffectues}
              </p>
            </div>
          </div>
        </div>

        {/* Next Stop */}
        {trajetInfo.prochainArret && (
          <div className="bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg p-4 border border-green-200">
            <h4 className="font-semibold text-gray-900 flex items-center gap-2 mb-3">
              <Navigation className="text-green-600" size={18} />
              Prochaine Étape
            </h4>
            <div className="space-y-2">
              <p className="text-sm font-medium text-gray-900">{trajetInfo.prochainArret}</p>
              {trajetInfo.distanceProchainArret && (
                <p className="text-xs text-gray-600">
                  Distance: {trajetInfo.distanceProchainArret} km
                </p>
              )}
              {trajetInfo.tempsEstimeProchainArret && (
                <p className="text-xs text-gray-600">
                  Temps estimé: {trajetInfo.tempsEstimeProchainArret} min
                </p>
              )}
            </div>
          </div>
        )}
      </div>

      <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4">
        <button
          onClick={loadTrajetInfo}
          className="w-full btn btn-primary flex items-center justify-center gap-2"
        >
          <RefreshCw size={18} />
          Actualiser
        </button>
      </div>
    </div>
  )
}

