import { useEffect, useRef, useState, useCallback } from 'react'
import { gpsService } from '@/services/gpsService'
import toast from 'react-hot-toast'

interface GPSTrackingOptions {
  busId: number
  updateInterval?: number // En millisecondes (défaut: 10000 = 10 secondes)
  onSuccess?: (position: GeolocationPosition) => void
  onError?: (error: GeolocationPositionError) => void
}

interface GPSTrackingState {
  isTracking: boolean
  lastPosition: GeolocationPosition | null
  error: string | null
  positionsSent: number
}

/**
 * Hook personnalisé pour gérer le tracking GPS automatique du conducteur
 * 
 * @param options Options de configuration du tracking
 * @returns État du tracking et fonctions de contrôle
 */
export function useGPSTracking(options: GPSTrackingOptions) {
  const { busId, updateInterval = 10000, onSuccess, onError } = options

  const [state, setState] = useState<GPSTrackingState>({
    isTracking: false,
    lastPosition: null,
    error: null,
    positionsSent: 0,
  })

  const watchIdRef = useRef<number | null>(null)
  const lastPositionRef = useRef<GeolocationPosition | null>(null)
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null)

  /**
   * Envoie la position GPS actuelle au backend
   */
  const sendPosition = useCallback(
    async (position: GeolocationPosition) => {
      try {
        const { latitude, longitude, altitude, accuracy } = position.coords

        // Calculer la vitesse et la direction si on a une position précédente
        let vitesse = 0
        let direction = 0

        if (lastPositionRef.current) {
          const lastCoords = lastPositionRef.current.coords
          const timeElapsed =
            (position.timestamp - lastPositionRef.current.timestamp) / 1000 // en secondes

          if (timeElapsed > 0) {
            vitesse = gpsService.calculateSpeed(
              lastCoords.latitude,
              lastCoords.longitude,
              latitude,
              longitude,
              timeElapsed
            )

            direction = gpsService.calculateDirection(
              lastCoords.latitude,
              lastCoords.longitude,
              latitude,
              longitude
            )
          }
        }

        // Envoyer la position au backend
        await gpsService.sendPosition({
          busId,
          latitude,
          longitude,
          altitude: altitude || undefined,
          precision: accuracy,
          vitesse,
          direction,
        })

        // Mettre à jour l'état
        setState((prev) => ({
          ...prev,
          lastPosition: position,
          positionsSent: prev.positionsSent + 1,
          error: null,
        }))

        lastPositionRef.current = position

        if (onSuccess) {
          onSuccess(position)
        }
      } catch (error) {
        console.error('Erreur lors de l\'envoi de la position GPS:', error)
        setState((prev) => ({
          ...prev,
          error: 'Erreur lors de l\'envoi de la position',
        }))
      }
    },
    [busId, onSuccess]
  )

  /**
   * Gestionnaire d'erreur GPS
   */
  const handleError = useCallback(
    (error: GeolocationPositionError) => {
      let errorMessage = ''

      switch (error.code) {
        case error.PERMISSION_DENIED:
          errorMessage = 'Permission de géolocalisation refusée'
          break
        case error.POSITION_UNAVAILABLE:
          errorMessage = 'Position GPS non disponible'
          break
        case error.TIMEOUT:
          errorMessage = 'Délai de géolocalisation dépassé'
          break
        default:
          errorMessage = 'Erreur de géolocalisation inconnue'
      }

      setState((prev) => ({
        ...prev,
        error: errorMessage,
      }))

      if (onError) {
        onError(error)
      }

      toast.error(errorMessage)
    },
    [onError]
  )

  /**
   * Démarre le tracking GPS
   */
  const startTracking = useCallback(() => {
    if (!navigator.geolocation) {
      toast.error('La géolocalisation n\'est pas supportée par votre navigateur')
      return
    }

    setState((prev) => ({ ...prev, isTracking: true, error: null }))

    // Options de géolocalisation
    const geoOptions: PositionOptions = {
      enableHighAccuracy: true,
      timeout: 5000,
      maximumAge: 0,
    }

    // Obtenir la position immédiatement
    navigator.geolocation.getCurrentPosition(
      (position) => {
        sendPosition(position)
        toast.success('Tracking GPS démarré ✅')
      },
      handleError,
      geoOptions
    )

    // Surveiller la position en continu
    watchIdRef.current = navigator.geolocation.watchPosition(
      (position) => {
        // La position est mise à jour mais on n'envoie pas immédiatement
        setState((prev) => ({ ...prev, lastPosition: position }))
      },
      handleError,
      geoOptions
    )

    // Envoyer la position au backend à intervalles réguliers
    intervalRef.current = setInterval(() => {
      if (state.lastPosition || lastPositionRef.current) {
        const currentPosition = state.lastPosition || lastPositionRef.current
        if (currentPosition) {
          sendPosition(currentPosition)
        }
      }
    }, updateInterval)
  }, [sendPosition, handleError, updateInterval, state.lastPosition])

  /**
   * Arrête le tracking GPS
   */
  const stopTracking = useCallback(() => {
    if (watchIdRef.current !== null) {
      navigator.geolocation.clearWatch(watchIdRef.current)
      watchIdRef.current = null
    }

    if (intervalRef.current !== null) {
      clearInterval(intervalRef.current)
      intervalRef.current = null
    }

    setState({
      isTracking: false,
      lastPosition: null,
      error: null,
      positionsSent: 0,
    })

    lastPositionRef.current = null

    toast.success('Tracking GPS arrêté')
  }, [])

  // Nettoyage lors du démontage du composant
  useEffect(() => {
    return () => {
      if (watchIdRef.current !== null) {
        navigator.geolocation.clearWatch(watchIdRef.current)
      }
      if (intervalRef.current !== null) {
        clearInterval(intervalRef.current)
      }
    }
  }, [])

  return {
    ...state,
    startTracking,
    stopTracking,
  }
}

