import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Clock, Plus, Calendar, X, Edit, Trash2 } from 'lucide-react'
import { scheduleService, type ScheduleResponse, type CreateScheduleRequest } from '@/services/scheduleService'
import { routeService } from '@/services/routeService'
import { busService } from '@/services/busService'
import { toast } from 'react-hot-toast'
import { SCHEDULE_TYPES, getScheduleTypeLabel, getScheduleTypeColor, type ScheduleType } from '@/types/scheduleType'
import { DAYS_FR } from '@/utils/dateHelpers'

// Reverse mapping: French -> English day names for API
const DAYS_MAP: Record<string, string> = Object.entries(DAYS_FR).reduce((acc, [eng, fr]) => {
  acc[fr] = eng
  return acc
}, {} as Record<string, string>)

export default function AdminSchedules() {
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [showEditModal, setShowEditModal] = useState(false)
  const [selectedSchedule, setSelectedSchedule] = useState<ScheduleResponse | null>(null)
  const [selectedDay, setSelectedDay] = useState('Lundi')
  const page = 0
  const size = 20

  // Filter states
  const [scheduleTypeFilter, setScheduleTypeFilter] = useState<string>('all')
  const [routeFilter, setRouteFilter] = useState<string>('all')
  const [busFilter, setBusFilter] = useState<string>('all')
  const [activeFilter, setActiveFilter] = useState<string>('all')
  const [departureTimeFrom, setDepartureTimeFrom] = useState<string>('')
  const [departureTimeTo, setDepartureTimeTo] = useState<string>('')

  const queryClient = useQueryClient()
  // French day names in order from DAYS_FR utility
  const days = Object.values(DAYS_FR)

  // Fetch schedules
  const { data: schedulesData, isLoading: schedulesLoading } = useQuery({
    queryKey: ['schedules', page],
    queryFn: () => scheduleService.getAllSchedules(page, size),
  })

  // Fetch routes for dropdown
  const { data: routesData } = useQuery({
    queryKey: ['routes-all'],
    queryFn: () => routeService.getAllRoutes(0, 100),
  })

  // Fetch buses for dropdown
  const { data: busesData } = useQuery({
    queryKey: ['buses-all'],
    queryFn: () => busService.getAllBuses(0, 100),
  })

  // Create schedule mutation
  const createScheduleMutation = useMutation({
    mutationFn: (data: CreateScheduleRequest) => scheduleService.createSchedule(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schedules'] })
      setShowCreateModal(false)
      toast.success('Horaire créé avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la création de l\'horaire')
    },
  })

  // Update schedule mutation
  const updateScheduleMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateScheduleRequest> }) =>
      scheduleService.updateSchedule(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schedules'] })
      setShowEditModal(false)
      setSelectedSchedule(null)
      toast.success('Horaire modifié avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la modification de l\'horaire')
    },
  })

  // Delete schedule mutation
  const deleteScheduleMutation = useMutation({
    mutationFn: (id: number) => scheduleService.deleteSchedule(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schedules'] })
      toast.success('Horaire supprimé avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la suppression de l\'horaire')
    },
  })

  const handleCreateSchedule = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    
    const selectedDays = Array.from(formData.getAll('daysOfWeek') as string[])
    const daysOfWeek = selectedDays.map(day => DAYS_MAP[day] || day)

    const frequencyValue = formData.get('frequency') as string
    const notesValue = formData.get('notes') as string

    const data: CreateScheduleRequest = {
      routeId: parseInt(formData.get('routeId') as string),
      busId: parseInt(formData.get('busId') as string),
      departureTime: formData.get('departureTime') as string,
      arrivalTime: formData.get('arrivalTime') as string,
      daysOfWeek: daysOfWeek,
      scheduleType: (formData.get('scheduleType') as ScheduleType) || 'REGULAR',
      frequency: frequencyValue ? parseInt(frequencyValue) : undefined,
      notes: notesValue || undefined,
    }
    createScheduleMutation.mutate(data)
  }

  const handleUpdateSchedule = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!selectedSchedule) return
    const formData = new FormData(e.currentTarget)
    
    const selectedDays = Array.from(formData.getAll('daysOfWeek') as string[])
    const daysOfWeek = selectedDays.map(day => DAYS_MAP[day] || day)

    const frequencyValue = formData.get('frequency') as string
    const notesValue = formData.get('notes') as string

    const data: Partial<CreateScheduleRequest> = {
      routeId: parseInt(formData.get('routeId') as string),
      busId: parseInt(formData.get('busId') as string),
      departureTime: formData.get('departureTime') as string,
      arrivalTime: formData.get('arrivalTime') as string,
      daysOfWeek: daysOfWeek,
      scheduleType: (formData.get('scheduleType') as ScheduleType) || 'REGULAR',
      frequency: frequencyValue ? parseInt(frequencyValue) : undefined,
      notes: notesValue || undefined,
    }
    updateScheduleMutation.mutate({ id: selectedSchedule.id, data })
  }

  const handleDeleteSchedule = (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet horaire ?')) {
      deleteScheduleMutation.mutate(id)
    }
  }

  const schedules = schedulesData?.content || []

  // Filter schedules by selected day and additional filters
  const filteredSchedules = schedules.filter(schedule => {
    // Day filter
    const matchesDay = schedule.daysOfWeek.includes(DAYS_MAP[selectedDay])

    // Schedule type filter
    const matchesType = scheduleTypeFilter === 'all' || schedule.scheduleType === scheduleTypeFilter

    // Route filter
    const matchesRoute = routeFilter === 'all' || schedule.routeId?.toString() === routeFilter

    // Bus filter
    const matchesBus = busFilter === 'all' || schedule.busId?.toString() === busFilter

    // Active filter
    const matchesActive = activeFilter === 'all' ||
      (activeFilter === 'true' && schedule.isActive) ||
      (activeFilter === 'false' && !schedule.isActive)

    // Time range filter
    const matchesDepartureFrom = !departureTimeFrom || schedule.departureTime >= departureTimeFrom
    const matchesDepartureTo = !departureTimeTo || schedule.departureTime <= departureTimeTo

    return matchesDay && matchesType && matchesRoute && matchesBus && matchesActive &&
           matchesDepartureFrom && matchesDepartureTo
  })

  const getRouteInfo = (schedule: ScheduleResponse) => {
    if (schedule.routeName && schedule.routeNumber) {
      return `${schedule.routeName} (${schedule.routeNumber})`
    }
    const route = routesData?.content.find(r => r.id === schedule.routeId)
    return route ? `${route.routeName} (${route.routeNumber})` : `Route #${schedule.routeId}`
  }

  const getBusInfo = (schedule: ScheduleResponse) => {
    if (schedule.busNumber) {
      return schedule.busNumber
    }
    const bus = busesData?.content.find(b => b.id === schedule.busId)
    return bus ? bus.busNumber : `Bus #${schedule.busId}`
  }

  return (
    <div className="space-y-6">
      {/* Day Selector */}
      <div className="card-gradient">
        <div className="flex items-center gap-4 overflow-x-auto">
          <Calendar className="text-primary-600 flex-shrink-0" size={24} />
          {days.map((day) => (
            <button
              key={day}
              onClick={() => setSelectedDay(day)}
              className={`px-6 py-3 rounded-xl font-semibold whitespace-nowrap transition-all ${
                selectedDay === day
                  ? 'bg-gradient-to-r from-primary-500 to-primary-600 text-white shadow-lg'
                  : 'bg-white text-gray-700 hover:bg-gray-50'
              }`}
            >
              {day}
            </button>
          ))}
        </div>
      </div>

      {/* Filters */}
      <div className="card-gradient">
        <div className="flex items-center gap-2 mb-4">
          <Clock className="h-5 w-5 text-gray-600" />
          <h3 className="font-semibold text-gray-900">Filtres</h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {/* Schedule Type Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Type d'horaire</label>
            <select
              value={scheduleTypeFilter}
              onChange={(e) => setScheduleTypeFilter(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Tous les types</option>
              <option value="REGULAR">Régulier</option>
              <option value="PEAK">Heures de pointe</option>
              <option value="NIGHT">Nocturne</option>
              <option value="WEEKEND">Week-end</option>
            </select>
          </div>

          {/* Route Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Ligne</label>
            <select
              value={routeFilter}
              onChange={(e) => setRouteFilter(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Toutes les lignes</option>
              {routesData?.content.map((route) => (
                <option key={route.id} value={route.id}>
                  {route.routeNumber} - {route.routeName}
                </option>
              ))}
            </select>
          </div>

          {/* Bus Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Bus</label>
            <select
              value={busFilter}
              onChange={(e) => setBusFilter(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Tous les bus</option>
              {busesData?.content.map((bus) => (
                <option key={bus.id} value={bus.id}>
                  {bus.busNumber} - {bus.model}
                </option>
              ))}
            </select>
          </div>

          {/* Active Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Statut</label>
            <select
              value={activeFilter}
              onChange={(e) => setActiveFilter(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Tous</option>
              <option value="true">Actifs</option>
              <option value="false">Inactifs</option>
            </select>
          </div>

          {/* Departure Time From */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Départ après</label>
            <input
              type="time"
              value={departureTimeFrom}
              onChange={(e) => setDepartureTimeFrom(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Departure Time To */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Départ avant</label>
            <input
              type="time"
              value={departureTimeTo}
              onChange={(e) => setDepartureTimeTo(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Clear Filters Button */}
        <div className="mt-4">
          <button
            onClick={() => {
              setScheduleTypeFilter('all')
              setRouteFilter('all')
              setBusFilter('all')
              setActiveFilter('all')
              setDepartureTimeFrom('')
              setDepartureTimeTo('')
            }}
            className="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Réinitialiser tous les filtres
          </button>
        </div>
      </div>

      {/* Header */}
      <div className="card-gradient">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Horaires du {selectedDay}</h2>
            <p className="text-gray-600 mt-1">{filteredSchedules.length} départ(s) programmé(s)</p>
          </div>
          <button 
            onClick={() => {
              setSelectedSchedule(null)
              setShowCreateModal(true)
            }}
            className="btn btn-primary flex items-center gap-2"
          >
            <Plus size={18} />
            Ajouter un Horaire
          </button>
        </div>
      </div>

      {/* Schedules Timeline */}
      <div className="card-gradient">
        {schedulesLoading ? (
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
          </div>
        ) : filteredSchedules.length > 0 ? (
          <div className="space-y-4">
            {filteredSchedules.map((schedule) => (
              <div
                key={schedule.id}
                className="flex items-center gap-6 p-4 bg-white rounded-xl hover:shadow-md transition-shadow border border-gray-100"
              >
                <div className="flex items-center gap-3">
                  <div className="w-20 h-20 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center text-white">
                    <div className="text-center">
                      <Clock size={24} className="mx-auto mb-1" />
                      <p className="text-lg font-bold">{schedule.departureTime}</p>
                    </div>
                  </div>
                </div>

                <div className="flex-1 grid grid-cols-1 md:grid-cols-4 gap-4">
                  <div>
                    <p className="text-sm text-gray-600">Ligne</p>
                    <p className="font-semibold text-gray-900">{getRouteInfo(schedule)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Bus</p>
                    <p className="font-semibold text-primary-600">{getBusInfo(schedule)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Arrivée</p>
                    <p className="font-semibold text-gray-900">{schedule.arrivalTime}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Type</p>
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getScheduleTypeColor(schedule.scheduleType)}`}>
                      {getScheduleTypeLabel(schedule.scheduleType)}
                    </span>
                  </div>
                </div>

                <div className="flex gap-2">
                  <button
                    onClick={() => {
                      setSelectedSchedule(schedule)
                      setShowEditModal(true)
                    }}
                    className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                  >
                    <Edit size={18} />
                  </button>
                  <button
                    onClick={() => handleDeleteSchedule(schedule.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <Clock className="mx-auto text-gray-400 mb-4" size={48} />
            <p className="text-gray-500 text-lg mb-2">Aucun horaire trouvé</p>
            <p className="text-gray-400 text-sm">
              {schedulesData?.content.length === 0 
                ? 'Aucun horaire dans le système' 
                : `Aucun horaire pour ${selectedDay}`}
            </p>
          </div>
        )}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between sticky top-0 bg-white">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <Clock className="text-primary-600" size={24} />
                Ajouter un Horaire
              </h2>
              <button
                onClick={() => setShowCreateModal(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateSchedule} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Route *</label>
                <select name="routeId" className="input" required>
                  <option value="">Sélectionner une route</option>
                  {routesData?.content.map((route) => (
                    <option key={route.id} value={route.id}>
                      {route.routeName} ({route.routeNumber})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Bus *</label>
                <select name="busId" className="input" required>
                  <option value="">Sélectionner un bus</option>
                  {busesData?.content.map((bus) => (
                    <option key={bus.id} value={bus.id}>
                      {bus.busNumber} - {bus.licensePlate}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Heure de Départ *</label>
                <input type="time" name="departureTime" className="input" required />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Heure d'Arrivée *</label>
                <input type="time" name="arrivalTime" className="input" required />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Jours de la semaine *</label>
                <div className="space-y-2">
                  {days.map((day) => (
                    <label key={day} className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        name="daysOfWeek"
                        value={day}
                        defaultChecked={day === selectedDay}
                        className="rounded"
                      />
                      <span className="text-sm">{day}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Type d'horaire *</label>
                <select name="scheduleType" className="input" required>
                  {SCHEDULE_TYPES.map((type) => (
                    <option key={type} value={type}>
                      {getScheduleTypeLabel(type)}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Fréquence (minutes)</label>
                <input 
                  type="number" 
                  name="frequency" 
                  className="input" 
                  min="0"
                  placeholder="0 = trajet unique"
                />
                <p className="text-xs text-gray-500 mt-1">0 = trajet unique, sinon intervalle en minutes</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Notes</label>
                <textarea 
                  name="notes" 
                  className="input" 
                  rows={3}
                  maxLength={500}
                  placeholder="Notes additionnelles..."
                />
              </div>

              <div className="border-t border-gray-200 pt-4 flex gap-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="flex-1 btn btn-secondary"
                >
                  Annuler
                </button>
                <button type="submit" className="flex-1 btn btn-primary">
                  Créer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && selectedSchedule && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between sticky top-0 bg-white">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <Clock className="text-primary-600" size={24} />
                Modifier l'Horaire
              </h2>
              <button
                onClick={() => {
                  setShowEditModal(false)
                  setSelectedSchedule(null)
                }}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleUpdateSchedule} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Route *</label>
                <select name="routeId" className="input" defaultValue={selectedSchedule.routeId} required>
                  <option value="">Sélectionner une route</option>
                  {routesData?.content.map((route) => (
                    <option key={route.id} value={route.id}>
                      {route.routeName} ({route.routeNumber})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Bus *</label>
                <select name="busId" className="input" defaultValue={selectedSchedule.busId} required>
                  <option value="">Sélectionner un bus</option>
                  {busesData?.content.map((bus) => (
                    <option key={bus.id} value={bus.id}>
                      {bus.busNumber} - {bus.licensePlate}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Heure de Départ *</label>
                <input type="time" name="departureTime" className="input" defaultValue={selectedSchedule.departureTime} required />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Heure d'Arrivée *</label>
                <input type="time" name="arrivalTime" className="input" defaultValue={selectedSchedule.arrivalTime} required />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Jours de la semaine *</label>
                <div className="space-y-2">
                  {days.map((day) => (
                    <label key={day} className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        name="daysOfWeek"
                        value={day}
                        defaultChecked={selectedSchedule.daysOfWeek.includes(DAYS_MAP[day])}
                        className="rounded"
                      />
                      <span className="text-sm">{day}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Type d'horaire *</label>
                <select name="scheduleType" className="input" defaultValue={selectedSchedule.scheduleType} required>
                  {SCHEDULE_TYPES.map((type) => (
                    <option key={type} value={type}>
                      {getScheduleTypeLabel(type)}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Fréquence (minutes)</label>
                <input 
                  type="number" 
                  name="frequency" 
                  className="input" 
                  min="0"
                  defaultValue={selectedSchedule.frequency || 0}
                  placeholder="0 = trajet unique"
                />
                <p className="text-xs text-gray-500 mt-1">0 = trajet unique, sinon intervalle en minutes</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Notes</label>
                <textarea 
                  name="notes" 
                  className="input" 
                  rows={3}
                  maxLength={500}
                  defaultValue={selectedSchedule.notes || ''}
                  placeholder="Notes additionnelles..."
                />
              </div>

              <div className="border-t border-gray-200 pt-4 flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false)
                    setSelectedSchedule(null)
                  }}
                  className="flex-1 btn btn-secondary"
                >
                  Annuler
                </button>
                <button type="submit" className="flex-1 btn btn-primary">
                  Modifier
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

