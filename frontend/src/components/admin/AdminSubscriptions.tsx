import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { CreditCard, Plus, X, Edit, Trash2, Check, AlertCircle } from 'lucide-react'
import { planService, CreatePlanRequest, SubscriptionPlanResponse } from '@/services/planService'
import { formatDurationDays } from '@/utils/dateUtils'
import toast from 'react-hot-toast'

export default function AdminSubscriptions() {
  const queryClient = useQueryClient()
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [showEditModal, setShowEditModal] = useState(false)
  const [selectedPlan, setSelectedPlan] = useState<SubscriptionPlanResponse | null>(null)
  const [formData, setFormData] = useState<CreatePlanRequest>({
    planCode: '',
    planName: '',
    description: '',
    price: 0,
    durationDays: 30,
    currency: 'MAD',
    features: [],
    isActive: true,
  })
  const [featureInput, setFeatureInput] = useState('')

  // Fetch all plans
  const { data: plans, isLoading } = useQuery({
    queryKey: ['subscription-plans'],
    queryFn: planService.getAllPlans,
  })

  // Create plan mutation
  const createMutation = useMutation({
    mutationFn: planService.createPlan,
    onSuccess: () => {
      toast.success('Plan créé avec succès!')
      queryClient.invalidateQueries({ queryKey: ['subscription-plans'] })
      setShowCreateModal(false)
      resetForm()
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erreur lors de la création du plan')
    },
  })

  // Update plan mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreatePlanRequest }) =>
      planService.updatePlan(id, data),
    onSuccess: () => {
      toast.success('Plan modifié avec succès!')
      queryClient.invalidateQueries({ queryKey: ['subscription-plans'] })
      setShowEditModal(false)
      resetForm()
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erreur lors de la modification')
    },
  })

  // Delete plan mutation
  const deleteMutation = useMutation({
    mutationFn: planService.deletePlan,
    onSuccess: () => {
      toast.success('Plan désactivé avec succès!')
      queryClient.invalidateQueries({ queryKey: ['subscription-plans'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erreur lors de la suppression')
    },
  })

  const resetForm = () => {
    setFormData({
      planCode: '',
      planName: '',
      description: '',
      price: 0,
      durationDays: 30,
      currency: 'MAD',
      features: [],
      isActive: true,
    })
    setFeatureInput('')
    setSelectedPlan(null)
  }

  const handleCreate = () => {
    setShowCreateModal(true)
    resetForm()
  }

  const handleEdit = (plan: SubscriptionPlanResponse) => {
    setSelectedPlan(plan)
    setFormData({
      planCode: plan.planCode,
      planName: plan.planName,
      description: plan.description,
      price: plan.price,
      durationDays: plan.durationDays,
      currency: plan.currency,
      features: plan.features || [],
      isActive: plan.isActive,
    })
    setShowEditModal(true)
  }

  const handleDelete = (id: string, planName: string) => {
    if (window.confirm(`Êtes-vous sûr de vouloir désactiver le plan "${planName}" ?`)) {
      deleteMutation.mutate(id)
    }
  }

  const handleSubmitCreate = (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.planCode || !formData.planName || !formData.description) {
      toast.error('Veuillez remplir tous les champs obligatoires')
      return
    }

    if (formData.price <= 0) {
      toast.error('Le prix doit être supérieur à 0')
      return
    }

    createMutation.mutate(formData)
  }

  const handleSubmitEdit = (e: React.FormEvent) => {
    e.preventDefault()

    if (!selectedPlan) return

    if (!formData.planCode || !formData.planName || !formData.description) {
      toast.error('Veuillez remplir tous les champs obligatoires')
      return
    }

    updateMutation.mutate({ id: selectedPlan.planId, data: formData })
  }

  const handleAddFeature = () => {
    if (featureInput.trim()) {
      setFormData({
        ...formData,
        features: [...(formData.features || []), featureInput.trim()],
      })
      setFeatureInput('')
    }
  }

  const handleRemoveFeature = (index: number) => {
    setFormData({
      ...formData,
      features: formData.features?.filter((_, i) => i !== index) || [],
    })
  }

  const getPlanColor = (index: number) => {
    const colors = [
      'from-blue-500 to-blue-600',
      'from-purple-500 to-purple-600',
      'from-green-500 to-green-600',
      'from-accent-500 to-accent-600',
      'from-pink-500 to-pink-600',
      'from-indigo-500 to-indigo-600',
    ]
    return colors[index % colors.length]
  }

  return (
    <div className="space-y-6">
      {/* Stats - TODO: Connect to real stats API */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
          <p className="text-sm font-medium text-blue-700 mb-1">Plans Actifs</p>
          <p className="text-3xl font-bold text-blue-900">
            {plans?.filter(p => p.isActive).length || 0}
          </p>
        </div>
        <div className="card bg-gradient-to-br from-green-50 to-green-100">
          <p className="text-sm font-medium text-green-700 mb-1">Plans Totaux</p>
          <p className="text-3xl font-bold text-green-900">{plans?.length || 0}</p>
        </div>
        <div className="card bg-gradient-to-br from-purple-50 to-purple-100">
          <p className="text-sm font-medium text-purple-700 mb-1">Prix Moyen</p>
          <p className="text-3xl font-bold text-purple-900">
            {plans && plans.length > 0
              ? Math.round(plans.reduce((sum, p) => sum + p.price, 0) / plans.length)
              : 0}{' '}
            MAD
          </p>
        </div>
        <div className="card bg-gradient-to-br from-red-50 to-red-100">
          <p className="text-sm font-medium text-red-700 mb-1">Plans Inactifs</p>
          <p className="text-3xl font-bold text-red-900">
            {plans?.filter(p => !p.isActive).length || 0}
          </p>
        </div>
      </div>

      {/* Plans */}
      <div>
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Plans d'Abonnement</h2>
          <button onClick={handleCreate} className="btn btn-primary flex items-center gap-2">
            <Plus size={18} />
            Nouveau Plan
          </button>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
          </div>
        ) : plans && plans.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {plans.map((plan, index) => (
              <div key={plan.planId} className="card hover:shadow-lg transition-all relative">
                {!plan.isActive && (
                  <div className="absolute top-4 right-4 px-3 py-1 bg-red-100 text-red-700 text-xs font-semibold rounded-full">
                    Inactif
                  </div>
                )}

                <div
                  className={`w-full h-2 bg-gradient-to-r ${getPlanColor(index)} rounded-t-xl -mt-6 mb-4`}
                ></div>

                <div className="text-center mb-4">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">{plan.planName}</h3>
                  <div className="flex items-baseline justify-center gap-1">
                    <span className="text-4xl font-bold text-primary-600">{plan.price}</span>
                    <span className="text-gray-600">MAD</span>
                  </div>
                  <p className="text-sm text-gray-500 mt-1">{formatDurationDays(plan.durationDays)}</p>
                  <p className="text-xs text-gray-400 mt-1">Code: {plan.planCode}</p>
                </div>

                <p className="text-sm text-gray-600 mb-4 text-center px-2">{plan.description}</p>

                {plan.features && plan.features.length > 0 && (
                  <ul className="space-y-2 mb-6">
                    {plan.features.map((feature, idx) => (
                      <li key={idx} className="flex items-center gap-2 text-sm text-gray-700">
                        <Check size={16} className="text-success-600 flex-shrink-0" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                )}

                <div className="flex gap-2">
                  <button
                    onClick={() => handleEdit(plan)}
                    className="flex-1 btn btn-secondary text-sm"
                  >
                    <Edit size={14} className="inline mr-1" />
                    Modifier
                  </button>
                  <button
                    onClick={() => handleDelete(plan.planId, plan.planName)}
                    disabled={deleteMutation.isPending}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-16 card">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
              <CreditCard className="text-gray-400" size={32} />
            </div>
            <p className="text-gray-500 text-lg mb-2">Aucun plan d'abonnement</p>
            <p className="text-gray-400 text-sm mb-4">
              Créez votre premier plan pour permettre aux passagers de s'abonner
            </p>
            <button onClick={handleCreate} className="btn btn-primary">
              <Plus size={18} className="inline mr-2" />
              Créer un Plan
            </button>
          </div>
        )}
      </div>

      {/* Create/Edit Modal */}
      {(showCreateModal || showEditModal) && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <CreditCard className="text-primary-600" size={24} />
                {showCreateModal ? 'Créer un Plan' : 'Modifier le Plan'}
              </h2>
              <button
                onClick={() => {
                  setShowCreateModal(false)
                  setShowEditModal(false)
                  resetForm()
                }}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={showCreateModal ? handleSubmitCreate : handleSubmitEdit} className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Code du Plan <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.planCode}
                    onChange={(e) => setFormData({ ...formData, planCode: e.target.value })}
                    className="input"
                    placeholder="MONTHLY_STANDARD"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Nom du Plan <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.planName}
                    onChange={(e) => setFormData({ ...formData, planName: e.target.value })}
                    className="input"
                    placeholder="Mensuel Standard"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description <span className="text-red-500">*</span>
                </label>
                <textarea
                  required
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="input"
                  rows={3}
                  placeholder="Décrivez les avantages de ce plan..."
                ></textarea>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Prix (MAD) <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    required
                    min="0"
                    step="0.01"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: parseFloat(e.target.value) })}
                    className="input"
                    placeholder="200.00"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Durée (jours) <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    required
                    min="1"
                    value={formData.durationDays}
                    onChange={(e) =>
                      setFormData({ ...formData, durationDays: parseInt(e.target.value) })
                    }
                    className="input"
                    placeholder="30"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Fonctionnalités
                </label>
                <div className="flex gap-2 mb-2">
                  <input
                    type="text"
                    value={featureInput}
                    onChange={(e) => setFeatureInput(e.target.value)}
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault()
                        handleAddFeature()
                      }
                    }}
                    className="input flex-1"
                    placeholder="Ajouter une fonctionnalité..."
                  />
                  <button
                    type="button"
                    onClick={handleAddFeature}
                    className="btn btn-secondary"
                  >
                    <Plus size={18} />
                  </button>
                </div>
                {formData.features && formData.features.length > 0 && (
                  <ul className="space-y-2">
                    {formData.features.map((feature, index) => (
                      <li
                        key={index}
                        className="flex items-center justify-between p-2 bg-gray-50 rounded-lg"
                      >
                        <span className="text-sm text-gray-700">{feature}</span>
                        <button
                          type="button"
                          onClick={() => handleRemoveFeature(index)}
                          className="p-1 text-red-600 hover:bg-red-50 rounded"
                        >
                          <X size={16} />
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="isActive"
                  checked={formData.isActive}
                  onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                  className="w-4 h-4 text-primary-600 rounded focus:ring-primary-500"
                />
                <label htmlFor="isActive" className="text-sm font-medium text-gray-700">
                  Plan actif (visible pour les passagers)
                </label>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <div className="flex gap-3">
                  <AlertCircle className="text-blue-600 flex-shrink-0" size={20} />
                  <div className="text-sm text-blue-800">
                    <p className="font-semibold mb-1">Informations importantes :</p>
                    <ul className="list-disc list-inside space-y-1">
                      <li>Le code du plan doit être unique</li>
                      <li>Laissez les champs "Max" vides pour un usage illimité</li>
                      <li>Les plans inactifs ne sont pas visibles aux passagers</li>
                    </ul>
                  </div>
                </div>
              </div>
            </form>

            <div className="sticky bottom-0 bg-gray-50 border-t border-gray-200 px-6 py-4 flex gap-3">
              <button
                type="button"
                onClick={() => {
                  setShowCreateModal(false)
                  setShowEditModal(false)
                  resetForm()
                }}
                className="flex-1 btn btn-secondary"
              >
                Annuler
              </button>
              <button
                type="button"
                onClick={showCreateModal ? handleSubmitCreate : handleSubmitEdit}
                disabled={createMutation.isPending || updateMutation.isPending}
                className="flex-1 btn btn-primary"
              >
                {createMutation.isPending || updateMutation.isPending
                  ? 'En cours...'
                  : showCreateModal
                  ? 'Créer le Plan'
                  : 'Sauvegarder'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
