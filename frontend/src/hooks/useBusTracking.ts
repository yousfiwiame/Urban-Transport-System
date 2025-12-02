import { useEffect, useState, useRef } from 'react'
// @ts-ignore - sockjs-client types not available
import SockJS from 'sockjs-client'
import { Client, IMessage } from '@stomp/stompjs'

export interface BusPosition {
  idPosition: string
  latitude: number
  longitude: number
  altitude: number
  precision: number
  vitesse: number
  direction: number
  timestamp: string
  busId: number
}

export const useBusTracking = (enabled = true) => {
  const [busPositions, setBusPositions] = useState<BusPosition[]>([])
  const [isConnected, setIsConnected] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    if (!enabled) return

    const socket = new SockJS('http://localhost:8083/ws')
    const stompClient = new Client({
      webSocketFactory: () => socket,

      onConnect: () => {
        console.log('✅ WebSocket connected')
        setIsConnected(true)
        setError(null)

        // Subscribe to all active bus locations
        stompClient.subscribe('/topic/buses/all', (message: IMessage) => {
          try {
            const positions: BusPosition[] = JSON.parse(message.body)
            setBusPositions(positions)
          } catch (err) {
            console.error('Error parsing bus positions:', err)
            setError('Failed to parse bus positions')
          }
        })
      },

      onDisconnect: () => {
        console.log('❌ WebSocket disconnected')
        setIsConnected(false)
      },

      onStompError: (frame) => {
        console.error('STOMP error:', frame)
        setError('WebSocket connection error')
      },

      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    clientRef.current = stompClient
    stompClient.activate()

    // Cleanup on unmount
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate()
      }
    }
  }, [enabled])

  return { busPositions, isConnected, error }
}

export const useSpecificBusTracking = (busId: number | null, enabled = true) => {
  const [busPosition, setBusPosition] = useState<BusPosition | null>(null)
  const [isConnected, setIsConnected] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    if (!enabled || !busId) return

    const socket = new SockJS('http://localhost:8083/ws')
    const stompClient = new Client({
      webSocketFactory: () => socket,

      onConnect: () => {
        console.log(`✅ WebSocket connected for bus ${busId}`)
        setIsConnected(true)
        setError(null)

        // Subscribe to specific bus location
        stompClient.subscribe(`/topic/bus/${busId}`, (message: IMessage) => {
          try {
            const position: BusPosition = JSON.parse(message.body)
            setBusPosition(position)
          } catch (err) {
            console.error('Error parsing bus position:', err)
            setError('Failed to parse bus position')
          }
        })
      },

      onDisconnect: () => {
        console.log(`❌ WebSocket disconnected for bus ${busId}`)
        setIsConnected(false)
      },

      onStompError: (frame) => {
        console.error('STOMP error:', frame)
        setError('WebSocket connection error')
      },

      reconnectDelay: 5000,
    })

    clientRef.current = stompClient
    stompClient.activate()

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate()
      }
    }
  }, [busId, enabled])

  return { busPosition, isConnected, error }
}
