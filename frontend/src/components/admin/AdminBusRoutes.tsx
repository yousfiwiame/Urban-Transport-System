import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Bus, Plus, Search, MapPin, X, Edit, Trash2 } from 'lucide-react'
import { busService, type BusResponse, type CreateBusRequest } from '@/services/busService'
import { routeService, type RouteResponse, type CreateRouteRequest } from '@/services/routeService'
import { toast } from 'react-hot-toast'
import { BUS_STATUSES, getBusStatusLabel } from '@/types/busStatus'

export default function AdminBusRoutes() {
  const [activeTab, setActiveTab] = useState<'buses' | 'routes'>('buses')
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [showEditModal, setShowEditModal] = useState(false)
  const [selectedBus, setSelectedBus] = useState<BusResponse | null>(null)
  const [selectedRoute, setSelectedRoute] = useState<RouteResponse | null>(null)
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(0)
  const size = 10

  // Filter states
  const [busStatusFilter, setBusStatusFilter] = useState<string>('all')
  const [accessibilityFilter, setAccessibilityFilter] = useState<string>('all')
  const [minCapacity, setMinCapacity] = useState<string>('')
  const [maxCapacity, setMaxCapacity] = useState<string>('')
  const [circularFilter, setCircularFilter] = useState<string>('all')
  const [minDistance, setMinDistance] = useState<string>('')
  const [maxDistance, setMaxDistance] = useState<string>('')

  const queryClient = useQueryClient()

  // Fetch buses
  const { data: busesData, isLoading: busesLoading } = useQuery({
    queryKey: ['buses', page],
    queryFn: () => busService.getAllBuses(page, size),
    enabled: activeTab === 'buses',
  })

  // Fetch routes
  const { data: routesData, isLoading: routesLoading } = useQuery({
    queryKey: ['routes', page],
    queryFn: () => routeService.getAllRoutes(page, size),
    enabled: activeTab === 'routes',
  })

  // Create bus mutation
  const createBusMutation = useMutation({
    mutationFn: (data: CreateBusRequest) => busService.createBus(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buses'] })
      setShowCreateModal(false)
      toast.success('Bus créé avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la création du bus')
    },
  })

  // Update bus mutation
  const updateBusMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateBusRequest> }) =>
      busService.updateBus(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buses'] })
      setShowEditModal(false)
      setSelectedBus(null)
      toast.success('Bus modifié avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la modification du bus')
    },
  })

  // Delete bus mutation
  const deleteBusMutation = useMutation({
    mutationFn: (id: number) => busService.deleteBus(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buses'] })
      toast.success('Bus supprimé avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la suppression du bus')
    },
  })

  // Create route mutation
  const createRouteMutation = useMutation({
    mutationFn: (data: CreateRouteRequest) => routeService.createRoute(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes'] })
      setShowCreateModal(false)
      toast.success('Route créée avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la création de la route')
    },
  })

  // Update route mutation
  const updateRouteMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateRouteRequest> }) =>
      routeService.updateRoute(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes'] })
      setShowEditModal(false)
      setSelectedRoute(null)
      toast.success('Route modifiée avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la modification de la route')
    },
  })

  // Delete route mutation
  const deleteRouteMutation = useMutation({
    mutationFn: (id: number) => routeService.deleteRoute(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes'] })
      toast.success('Route supprimée avec succès')
    },
    onError: () => {
      toast.error('Erreur lors de la suppression de la route')
    },
  })

  const buses = busesData?.content || []
  const routes = routesData?.content || []

  const handleCreateBus = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const data: CreateBusRequest = {
      busNumber: formData.get('busNumber') as string,
      licensePlate: formData.get('licensePlate') as string,
      model: formData.get('model') as string,
      manufacturer: formData.get('manufacturer') as string,
      year: parseInt(formData.get('year') as string),
      capacity: parseInt(formData.get('capacity') as string),
      status: formData.get('status') as any,
      isAccessible: formData.get('isAccessible') === 'true',
    }
    createBusMutation.mutate(data)
  }

  const handleUpdateBus = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!selectedBus) return
    const formData = new FormData(e.currentTarget)
    const data: Partial<CreateBusRequest> = {
      busNumber: formData.get('busNumber') as string,
      licensePlate: formData.get('licensePlate') as string,
      model: formData.get('model') as string,
      manufacturer: formData.get('manufacturer') as string,
      year: parseInt(formData.get('year') as string),
      capacity: parseInt(formData.get('capacity') as string),
      status: formData.get('status') as any,
      isAccessible: formData.get('isAccessible') === 'true',
    }
    updateBusMutation.mutate({ id: selectedBus.id, data })
  }

  const handleDeleteBus = (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce bus ?')) {
      deleteBusMutation.mutate(id)
    }
  }

  const handleCreateRoute = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const data: CreateRouteRequest = {
      routeNumber: formData.get('routeNumber') as string,
      routeName: formData.get('routeName') as string,
      origin: formData.get('origin') as string,
      destination: formData.get('destination') as string,
      distance: parseFloat(formData.get('distance') as string),
      estimatedDuration: parseInt(formData.get('estimatedDuration') as string),
      isCircular: formData.get('isCircular') === 'true',
      description: formData.get('description') as string || undefined,
    }
    createRouteMutation.mutate(data)
  }

  const handleUpdateRoute = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!selectedRoute) return
    const formData = new FormData(e.currentTarget)
    const distanceValue = formData.get('distance') as string
    const durationValue = formData.get('estimatedDuration') as string
    const data: Partial<CreateRouteRequest> = {
      routeNumber: formData.get('routeNumber') as string,
      routeName: formData.get('routeName') as string,
      origin: formData.get('origin') as string,
      destination: formData.get('destination') as string,
      distance: distanceValue ? parseFloat(distanceValue) : undefined,
      estimatedDuration: durationValue ? parseInt(durationValue) : undefined,
      isCircular: formData.get('isCircular') === 'true',
      description: formData.get('description') as string || undefined,
    }
    updateRouteMutation.mutate({ id: selectedRoute.id, data })
  }

  const handleDeleteRoute = (id: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette route ?')) {
      deleteRouteMutation.mutate(id)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'AVAILABLE': return 'bg-success-100 text-success-700 border-success-200'
      case 'IN_USE': return 'bg-blue-100 text-blue-700 border-blue-200'
      case 'MAINTENANCE': return 'bg-accent-100 text-accent-700 border-accent-200'
      case 'OUT_OF_SERVICE': return 'bg-red-100 text-red-700 border-red-200'
      default: return 'bg-gray-100 text-gray-700 border-gray-200'
    }
  }

  const getStatusLabel = (status: string) => {
    switch (status.toUpperCase()) {
      case 'AVAILABLE': return 'Disponible'
      case 'IN_USE': return 'En Service'
      case 'MAINTENANCE': return 'Maintenance'
      case 'OUT_OF_SERVICE': return 'Hors Service'
      default: return status
    }
  }

  const filteredBuses = buses.filter((bus) => {
    // Search query filter
    const matchesSearch = !searchQuery ||
      bus.busNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      bus.licensePlate.toLowerCase().includes(searchQuery.toLowerCase()) ||
      bus.model.toLowerCase().includes(searchQuery.toLowerCase()) ||
      bus.manufacturer.toLowerCase().includes(searchQuery.toLowerCase())

    // Status filter
    const matchesStatus = busStatusFilter === 'all' || bus.status.toUpperCase() === busStatusFilter.toUpperCase()

    // Accessibility filter
    const matchesAccessibility = accessibilityFilter === 'all' ||
      (accessibilityFilter === 'true' && bus.isAccessible) ||
      (accessibilityFilter === 'false' && !bus.isAccessible)

    // Capacity filter
    const matchesMinCapacity = !minCapacity || bus.capacity >= parseInt(minCapacity)
    const matchesMaxCapacity = !maxCapacity || bus.capacity <= parseInt(maxCapacity)

    return matchesSearch && matchesStatus && matchesAccessibility && matchesMinCapacity && matchesMaxCapacity
  })

  const filteredRoutes = routes.filter((route) => {
    // Search query filter
    const matchesSearch = !searchQuery ||
      route.routeNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      route.routeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      route.origin.toLowerCase().includes(searchQuery.toLowerCase()) ||
      route.destination.toLowerCase().includes(searchQuery.toLowerCase())

    // Circular filter
    const matchesCircular = circularFilter === 'all' ||
      (circularFilter === 'true' && route.isCircular) ||
      (circularFilter === 'false' && !route.isCircular)

    // Distance filter
    const matchesMinDistance = !minDistance || route.distance >= parseFloat(minDistance)
    const matchesMaxDistance = !maxDistance || route.distance <= parseFloat(maxDistance)

    return matchesSearch && matchesCircular && matchesMinDistance && matchesMaxDistance
  })

  return (
    <div className="space-y-6">
      {/* Tabs */}
      <div className="card-gradient p-2">
        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={() => setActiveTab('buses')}
            className={`py-3 px-6 rounded-xl font-semibold transition-all ${
              activeTab === 'buses'
                ? 'bg-gradient-to-r from-primary-500 to-primary-600 text-white shadow-lg'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            <Bus className="inline mr-2" size={20} />
            Gestion des Bus
          </button>
          <button
            onClick={() => setActiveTab('routes')}
            className={`py-3 px-6 rounded-xl font-semibold transition-all ${
              activeTab === 'routes'
                ? 'bg-gradient-to-r from-primary-500 to-primary-600 text-white shadow-lg'
                : 'bg-white text-gray-700 hover:bg-gray-50'
            }`}
          >
            <MapPin className="inline mr-2" size={20} />
            Gestion des Routes
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="card-gradient">
        <div className="flex items-center gap-2 mb-4">
          <Search className="h-5 w-5 text-gray-600" />
          <h3 className="font-semibold text-gray-900">Filtres</h3>
        </div>

        {activeTab === 'buses' ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Status Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Statut</label>
              <select
                value={busStatusFilter}
                onChange={(e) => setBusStatusFilter(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="all">Tous les statuts</option>
                <option value="AVAILABLE">Disponible</option>
                <option value="IN_USE">En Service</option>
                <option value="MAINTENANCE">Maintenance</option>
                <option value="OUT_OF_SERVICE">Hors Service</option>
              </select>
            </div>

            {/* Accessibility Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Accessibilité</label>
              <select
                value={accessibilityFilter}
                onChange={(e) => setAccessibilityFilter(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="all">Tous</option>
                <option value="true">Accessible</option>
                <option value="false">Non Accessible</option>
              </select>
            </div>

            {/* Min Capacity */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Capacité Min</label>
              <input
                type="number"
                value={minCapacity}
                onChange={(e) => setMinCapacity(e.target.value)}
                placeholder="Ex: 30"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            {/* Max Capacity */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Capacité Max</label>
              <input
                type="number"
                value={maxCapacity}
                onChange={(e) => setMaxCapacity(e.target.value)}
                placeholder="Ex: 50"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {/* Circular Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Type de Route</label>
              <select
                value={circularFilter}
                onChange={(e) => setCircularFilter(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="all">Tous les types</option>
                <option value="true">Circulaire</option>
                <option value="false">Non Circulaire</option>
              </select>
            </div>

            {/* Min Distance */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Distance Min (km)</label>
              <input
                type="number"
                step="0.1"
                value={minDistance}
                onChange={(e) => setMinDistance(e.target.value)}
                placeholder="Ex: 5.0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>

            {/* Max Distance */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Distance Max (km)</label>
              <input
                type="number"
                step="0.1"
                value={maxDistance}
                onChange={(e) => setMaxDistance(e.target.value)}
                placeholder="Ex: 20.0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>
        )}

        {/* Clear Filters Button */}
        <div className="mt-4">
          <button
            onClick={() => {
              setBusStatusFilter('all')
              setAccessibilityFilter('all')
              setMinCapacity('')
              setMaxCapacity('')
              setCircularFilter('all')
              setMinDistance('')
              setMaxDistance('')
              setSearchQuery('')
            }}
            className="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Réinitialiser tous les filtres
          </button>
        </div>
      </div>

      {/* Action Bar */}
      <div className="card-gradient">
        <div className="flex gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder={`Rechercher un ${activeTab === 'buses' ? 'bus' : 'route'}...`}
              className="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
          <button 
            onClick={() => {
              setSelectedBus(null)
              setSelectedRoute(null)
              setShowCreateModal(true)
            }}
            className="btn btn-primary flex items-center gap-2"
          >
            <Plus size={18} />
            {activeTab === 'buses' ? 'Ajouter Bus' : 'Ajouter Route'}
          </button>
        </div>
      </div>

      {/* Content */}
      {activeTab === 'buses' ? (
        <div className="card-gradient">
          {busesLoading ? (
            <div className="flex items-center justify-center py-16">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
            </div>
          ) : filteredBuses.length > 0 ? (
            <>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200">
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Numéro</th>
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Plaque</th>
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Capacité</th>
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Modèle</th>
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Statut</th>
                      <th className="text-left py-4 px-4 font-semibold text-gray-700">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredBuses.map((bus) => (
                      <tr key={bus.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                        <td className="py-4 px-4 font-semibold text-primary-600">{bus.busNumber}</td>
                        <td className="py-4 px-4 font-mono text-sm">{bus.licensePlate}</td>
                        <td className="py-4 px-4">{bus.capacity} places</td>
                        <td className="py-4 px-4">{bus.model || '-'}</td>
                        <td className="py-4 px-4">
                          <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(bus.status)}`}>
                            {getStatusLabel(bus.status)}
                          </span>
                        </td>
                        <td className="py-4 px-4">
                          <div className="flex gap-2">
                            <button
                              onClick={() => {
                                setSelectedBus(bus)
                                setShowEditModal(true)
                              }}
                              className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                            >
                              <Edit size={16} />
                            </button>
                            <button
                              onClick={() => handleDeleteBus(bus.id)}
                              className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                            >
                              <Trash2 size={16} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              {busesData && busesData.totalPages > 1 && (
                <div className="mt-6 flex items-center justify-between border-t border-gray-200 pt-6">
                  <div className="text-sm text-gray-600">
                    Page {page + 1} sur {busesData.totalPages}
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => setPage(p => Math.max(0, p - 1))}
                      disabled={page === 0}
                      className="btn btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Précédent
                    </button>
                    <button
                      onClick={() => setPage(p => Math.min(busesData.totalPages - 1, p + 1))}
                      disabled={page >= busesData.totalPages - 1}
                      className="btn btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Suivant
                    </button>
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-16">
              <Bus className="mx-auto text-gray-400 mb-4" size={48} />
              <p className="text-gray-500">Aucun bus trouvé</p>
            </div>
          )}
        </div>
      ) : routesLoading ? (
        <div className="flex items-center justify-center py-16">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      ) : filteredRoutes.length > 0 ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {filteredRoutes.map((route) => (
              <div key={route.id} className="card hover:shadow-lg transition-shadow">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h3 className="text-xl font-bold text-primary-600">{route.routeName}</h3>
                    <p className="text-sm text-gray-600 mt-1">{route.routeNumber}</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => {
                        setSelectedRoute(route)
                        setShowEditModal(true)
                      }}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                    >
                      <Edit size={18} />
                    </button>
                    <button
                      onClick={() => handleDeleteRoute(route.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    >
                      <Trash2 size={18} />
                    </button>
                  </div>
                </div>

                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <MapPin className="text-primary-600" size={20} />
                    <div>
                      <p className="text-sm text-gray-600">De</p>
                      <p className="font-semibold">{route.origin}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <MapPin className="text-accent-600" size={20} />
                    <div>
                      <p className="text-sm text-gray-600">À</p>
                      <p className="font-semibold">{route.destination}</p>
                    </div>
                  </div>
                </div>

                {route.distance && (
                  <div className="mt-4 pt-4 border-t border-gray-200">
                    <div className="text-center">
                      <p className="text-2xl font-bold text-gray-900">{route.distance} km</p>
                      <p className="text-xs text-gray-600">Distance</p>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
          {routesData && routesData.totalPages > 1 && (
            <div className="mt-6 flex items-center justify-between">
              <div className="text-sm text-gray-600">
                Page {page + 1} sur {routesData.totalPages}
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="btn btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Précédent
                </button>
                <button
                  onClick={() => setPage(p => Math.min(routesData.totalPages - 1, p + 1))}
                  disabled={page >= routesData.totalPages - 1}
                  className="btn btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Suivant
                </button>
              </div>
            </div>
          )}
        </>
      ) : (
        <div className="text-center py-16">
          <MapPin className="mx-auto text-gray-400 mb-4" size={48} />
          <p className="text-gray-500">Aucune route trouvée</p>
        </div>
      )}

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between sticky top-0 bg-white">
              <h2 className="text-2xl font-bold text-gray-900">
                {activeTab === 'buses' ? 'Ajouter un Bus' : 'Ajouter une Route'}
              </h2>
              <button
                onClick={() => setShowCreateModal(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={activeTab === 'buses' ? handleCreateBus : handleCreateRoute} className="p-6 space-y-4">
              {activeTab === 'buses' ? (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Numéro du Bus *</label>
                    <input type="text" name="busNumber" className="input" placeholder="BUS-001" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Plaque d'immatriculation *</label>
                    <input type="text" name="licensePlate" className="input" placeholder="12345-A-67" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Capacité *</label>
                    <input type="number" name="capacity" className="input" placeholder="50" required min="1" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Modèle *</label>
                    <input type="text" name="model" className="input" placeholder="Mercedes Citaro" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Fabricant *</label>
                    <input type="text" name="manufacturer" className="input" placeholder="Mercedes-Benz" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Année de fabrication *</label>
                    <input type="number" name="year" className="input" placeholder="2022" required min="1900" max="2100" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Statut *</label>
                    <select name="status" className="input" required defaultValue="ACTIVE">
                      {BUS_STATUSES.map((status) => (
                        <option key={status} value={status}>
                          {getBusStatusLabel(status)}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" name="isAccessible" value="true" className="rounded" />
                      <span className="text-sm font-medium text-gray-700">Accessible PMR</span>
                    </label>
                  </div>
                </>
              ) : (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Numéro de Route *</label>
                    <input type="text" name="routeNumber" className="input" placeholder="L15" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Nom de la Ligne *</label>
                    <input type="text" name="routeName" className="input" placeholder="Ligne 15" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Départ *</label>
                    <input type="text" name="origin" className="input" placeholder="Gare Casa-Voyageurs" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Arrivée *</label>
                    <input type="text" name="destination" className="input" placeholder="Aéroport Mohammed V" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Distance (km) *</label>
                    <input type="number" name="distance" className="input" placeholder="25" step="0.1" min="0" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Durée estimée (minutes) *</label>
                    <input type="number" name="estimatedDuration" className="input" placeholder="45" min="0" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                    <textarea name="description" className="input" rows={3} placeholder="Description de la route..."></textarea>
                  </div>
                  <div>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" name="isCircular" value="true" className="rounded" />
                      <span className="text-sm font-medium text-gray-700">Route circulaire</span>
                    </label>
                  </div>
                </>
              )}

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
      {showEditModal && (selectedBus || selectedRoute) && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between sticky top-0 bg-white">
              <h2 className="text-2xl font-bold text-gray-900">
                {activeTab === 'buses' ? 'Modifier le Bus' : 'Modifier la Route'}
              </h2>
              <button
                onClick={() => {
                  setShowEditModal(false)
                  setSelectedBus(null)
                  setSelectedRoute(null)
                }}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={activeTab === 'buses' ? handleUpdateBus : handleUpdateRoute} className="p-6 space-y-4">
              {activeTab === 'buses' && selectedBus ? (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Numéro du Bus *</label>
                    <input type="text" name="busNumber" className="input" defaultValue={selectedBus.busNumber} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Plaque d'immatriculation *</label>
                    <input type="text" name="licensePlate" className="input" defaultValue={selectedBus.licensePlate} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Capacité *</label>
                    <input type="number" name="capacity" className="input" defaultValue={selectedBus.capacity} required min="1" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Modèle *</label>
                    <input type="text" name="model" className="input" defaultValue={selectedBus.model} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Fabricant *</label>
                    <input type="text" name="manufacturer" className="input" defaultValue={selectedBus.manufacturer} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Année de fabrication *</label>
                    <input type="number" name="year" className="input" defaultValue={selectedBus.year} required min="1900" max="2100" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Statut *</label>
                    <select name="status" className="input" required defaultValue={selectedBus.status}>
                      {BUS_STATUSES.map((status) => (
                        <option key={status} value={status}>
                          {getBusStatusLabel(status)}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" name="isAccessible" value="true" defaultChecked={selectedBus.isAccessible} className="rounded" />
                      <span className="text-sm font-medium text-gray-700">Accessible PMR</span>
                    </label>
                  </div>
                </>
              ) : selectedRoute ? (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Numéro de Route *</label>
                    <input type="text" name="routeNumber" className="input" defaultValue={selectedRoute.routeNumber} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Nom de la Ligne *</label>
                    <input type="text" name="routeName" className="input" defaultValue={selectedRoute.routeName} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Départ *</label>
                    <input type="text" name="origin" className="input" defaultValue={selectedRoute.origin} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Arrivée *</label>
                    <input type="text" name="destination" className="input" defaultValue={selectedRoute.destination} required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Distance (km)</label>
                    <input type="number" name="distance" className="input" defaultValue={selectedRoute.distance || ''} step="0.1" min="0" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Durée estimée (minutes)</label>
                    <input type="number" name="estimatedDuration" className="input" defaultValue={selectedRoute.estimatedDuration || ''} min="0" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                    <textarea name="description" className="input" rows={3} defaultValue={selectedRoute.description || ''}></textarea>
                  </div>
                  <div>
                    <label className="flex items-center gap-2">
                      <input type="checkbox" name="isCircular" value="true" defaultChecked={selectedRoute.isCircular} className="rounded" />
                      <span className="text-sm font-medium text-gray-700">Route circulaire</span>
                    </label>
                  </div>
                </>
              ) : null}

              <div className="border-t border-gray-200 pt-4 flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false)
                    setSelectedBus(null)
                    setSelectedRoute(null)
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

