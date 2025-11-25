import { useQuery } from '@tanstack/react-query'
import { scheduleService } from '@/services/scheduleService'
import { routeService } from '@/services/routeService'
import { Calendar, Clock, MapPin, Bus, ArrowRight, Navigation } from 'lucide-react'
import { formatDaysList } from '@/utils/dateHelpers'

export default function Schedules() {
  const { data: schedules, isLoading } = useQuery({
    queryKey: ['schedules'],
    queryFn: () => scheduleService.getAllSchedules(0, 50),
  })

  const { data: routes } = useQuery({
    queryKey: ['routes'],
    queryFn: () => routeService.getAllRoutes(0, 100),
  })

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold gradient-text mb-2">Horaires des Bus</h1>
        <p className="text-gray-600 text-lg">Consultez les itinéraires disponibles et les horaires de départ</p>
      </div>

      {isLoading ? (
        <div className="card text-center py-16">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          </div>
          <p className="text-gray-500 text-lg">Chargement des horaires...</p>
        </div>
      ) : schedules?.content && schedules.content.length > 0 ? (
        <div className="space-y-4">
          {schedules.content.map((schedule: any, index: number) => {
            const route = routes?.content?.find((r: any) => r.id === schedule.routeId)
            return (
              <div 
                key={schedule.id} 
                className="card hover:scale-[1.02] transition-all duration-300 animate-slide-up group"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
                  {/* Left Section */}
                  <div className="flex-1">
                    <div className="flex items-start gap-4 mb-4">
                      <div className="p-3 bg-gradient-to-br from-accent-500 to-accent-600 rounded-xl shadow-lg group-hover:scale-110 transition-transform">
                        <Bus className="text-white" size={24} />
                      </div>
                      <div className="flex-1">
                        <h3 className="font-bold text-xl text-gray-900 mb-2">
                          {schedule.routeName || route?.routeName || `Ligne ${schedule.routeNumber || schedule.routeId}`}
                        </h3>
                        {(schedule.routeNumber || route) && (
                          <div className="flex items-center gap-2 text-gray-600 mb-4">
                            <MapPin size={16} className="text-accent-600" />
                            <span className="font-medium">{route?.origin || 'Départ'}</span>
                            <ArrowRight size={14} className="text-gray-400" />
                            <span className="font-medium">{route?.destination || 'Arrivée'}</span>
                          </div>
                        )}
                        
                        {/* Time Info */}
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                          <div className="flex items-center gap-3 p-3 bg-gradient-to-r from-primary-50 to-primary-100 rounded-xl">
                            <div className="p-2 bg-primary-100 rounded-lg">
                              <Clock size={18} className="text-primary-600" />
                            </div>
                            <div>
                              <p className="text-xs text-gray-600 font-medium">Départ</p>
                              <p className="text-lg font-bold text-gray-900">
                                {schedule.departureTime}
                              </p>
                            </div>
                          </div>
                          <div className="flex items-center gap-3 p-3 bg-gradient-to-r from-accent-50 to-success-50 rounded-xl">
                            <div className="p-2 bg-accent-100 rounded-lg">
                              <Clock size={18} className="text-accent-600" />
                            </div>
                            <div>
                              <p className="text-xs text-gray-600 font-medium">Arrivée</p>
                              <p className="text-lg font-bold text-gray-900">
                                {schedule.arrivalTime}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Right Section */}
                  <div className="flex flex-col items-center md:items-end gap-3">
                    <div className="px-6 py-4 bg-gradient-to-br from-primary-500 to-primary-600 rounded-2xl shadow-lg text-center min-w-[150px]">
                      <p className="text-xs text-primary-100 font-medium mb-1">Jours</p>
                      <p className="text-sm font-bold text-white">
                        {schedule.daysOfWeek && schedule.daysOfWeek.length > 0
                          ? formatDaysList(schedule.daysOfWeek)
                          : 'N/A'}
                      </p>
                    </div>
                    <div className="px-4 py-2 bg-accent-100 rounded-xl text-center">
                      <p className="text-xs text-accent-700 font-medium">Bus</p>
                      <p className="font-bold text-accent-800">{schedule.busNumber || `#${schedule.busId}`}</p>
                    </div>
                    {route && (
                      <button className="flex items-center gap-2 px-4 py-2 bg-accent-50 hover:bg-accent-100 text-accent-700 rounded-xl font-semibold text-sm transition-colors">
                        <Navigation size={16} />
                        Voir l'Itinéraire
                      </button>
                    )}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      ) : (
        <div className="card text-center py-16 animate-scale-in">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-primary-100 to-accent-100 rounded-3xl mb-6">
            <Calendar className="text-primary-600" size={40} />
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-2">Aucun horaire disponible</h3>
          <p className="text-gray-600">Revenez plus tard pour les horaires mis à jour</p>
        </div>
      )}
    </div>
  )
}
