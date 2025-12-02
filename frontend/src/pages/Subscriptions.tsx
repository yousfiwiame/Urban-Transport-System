import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { subscriptionService } from '@/services/subscriptionService'
import { CreditCard, Check, Star, Zap, Crown, ArrowRight, Calendar, X } from 'lucide-react'
import toast from 'react-hot-toast'
import { useAuthStore } from '@/store/authStore'
import { formatDurationDays } from '@/utils/dateUtils'

export default function Subscriptions() {
  const { user } = useAuthStore()
  const queryClient = useQueryClient()

  const { data: plans, isLoading: plansLoading } = useQuery({
    queryKey: ['subscriptionPlans'],
    queryFn: subscriptionService.getPlans,
  })

  const { data: subscriptions } = useQuery({
    queryKey: ['mySubscriptions', user?.id],
    queryFn: () => subscriptionService.getMySubscriptions(String(user?.id || '')),
    enabled: !!user?.id,
  })

  const subscribeMutation = useMutation({
    mutationFn: ({ userId, planId }: { userId: string; planId: string }) =>
      subscriptionService.subscribe(userId, planId),
    onSuccess: () => {
      toast.success('Abonnement activé avec succès ! 🎉')
      queryClient.invalidateQueries({ queryKey: ['mySubscriptions'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec de l\'abonnement')
    },
  })

  const cancelMutation = useMutation({
    mutationFn: (subscriptionId: string) => subscriptionService.cancelSubscription(subscriptionId),
    onSuccess: () => {
      toast.success('Abonnement annulé')
      queryClient.invalidateQueries({ queryKey: ['mySubscriptions'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec de l\'annulation')
    },
  })

  const handleSubscribe = (planId: string) => {
    if (!user?.id) {
      toast.error('Veuillez vous connecter pour vous abonner')
      return
    }
    subscribeMutation.mutate({ userId: String(user.id), planId })
  }

  const handleCancel = (subscriptionId: string) => {
    if (confirm('Êtes-vous sûr de vouloir annuler cet abonnement ?')) {
      cancelMutation.mutate(subscriptionId)
    }
  }

  const planIcons = [Zap, Star, Crown]
  const planColors = [
    'from-primary-500 to-primary-600',
    'from-primary-600 to-accent-500',
    'from-accent-500 to-accent-600',
  ]

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold gradient-text mb-2">Abonnements</h1>
        <p className="text-gray-600 text-lg">Choisissez un forfait adapté à vos besoins de déplacement</p>
      </div>

      {/* Active Subscriptions */}
      {subscriptions && subscriptions.length > 0 && (
        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-gray-900">Abonnements Actifs</h2>
          {subscriptions.map((subscription) => (
            <div key={subscription.subscriptionId} className="card-gradient border-2 border-primary-200 animate-slide-up">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-4 bg-gradient-to-br from-primary-500 to-primary-600 rounded-2xl shadow-lg">
                    <CreditCard className="text-white" size={28} />
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-1">
                      {subscription.plan?.planName || 'Abonnement'}
                    </h3>
                    <div className="flex items-center gap-4 text-sm text-gray-600">
                      <div className="flex items-center gap-1">
                        <Calendar size={16} />
                        <span>
                          {new Date(subscription.startDate).toLocaleDateString('fr-FR')} -{' '}
                          {new Date(subscription.endDate).toLocaleDateString('fr-FR')}
                        </span>
                      </div>
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          subscription.status === 'ACTIVE'
                            ? 'bg-success-100 text-success-700'
                            : 'bg-gray-100 text-gray-700'
                        }`}
                      >
                        {subscription.status === 'ACTIVE' ? 'ACTIF' : subscription.status}
                      </span>
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => handleCancel(subscription.subscriptionId)}
                  className="btn btn-secondary flex items-center gap-2 text-red-600 hover:bg-red-50"
                >
                  <X size={18} />
                  Annuler
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Subscription Plans */}
      <div>
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Forfaits Disponibles</h2>
        {plansLoading ? (
          <div className="card text-center py-16">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            </div>
            <p className="text-gray-500 text-lg">Chargement des forfaits...</p>
          </div>
        ) : plans && plans.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {plans.map((plan, index) => {
              const Icon = planIcons[index % planIcons.length]
              const color = planColors[index % planColors.length]
              const isPopular = index === 1
              
              return (
                <div
                  key={plan.planId}
                  className={`card relative hover:scale-105 transition-all duration-300 animate-slide-up ${
                    isPopular ? 'ring-2 ring-primary-500 shadow-2xl' : ''
                  }`}
                  style={{ animationDelay: `${index * 100}ms` }}
                >
                  {isPopular && (
                    <div className="absolute -top-4 left-1/2 transform -translate-x-1/2 px-4 py-1 bg-gradient-to-r from-primary-600 to-accent-600 text-white text-xs font-bold rounded-full">
                      PLUS POPULAIRE
                    </div>
                  )}
                  
                  <div className="text-center mb-6">
                    <div className={`inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br ${color} rounded-2xl mb-4 shadow-lg`}>
                      <Icon className="text-white" size={32} />
                    </div>
                    <h3 className="text-2xl font-bold text-gray-900 mb-2">{plan.planName}</h3>
                    <p className="text-gray-600 text-sm">{plan.description}</p>
                  </div>

                  <div className="mb-6">
                    <div className="flex items-baseline justify-center gap-2 mb-4">
                      <span className="text-5xl font-bold gradient-text">
                        {plan.price.toFixed(2)}
                      </span>
                      <span className="text-gray-600 text-lg">
                        {plan.currency}
                      </span>
                    </div>
                    <p className="text-center text-gray-500 text-sm">
                      Durée: {formatDurationDays(plan.durationDays)}
                    </p>
                  </div>

                  <ul className="space-y-3 mb-6">
                    {plan.features?.map((feature, idx) => (
                      <li key={idx} className="flex items-start gap-2">
                        <Check className="text-success-600 flex-shrink-0 mt-0.5" size={18} />
                        <span className="text-sm text-gray-700">{feature}</span>
                      </li>
                    ))}
                  </ul>

                  <button
                    onClick={() => handleSubscribe(plan.planId)}
                    disabled={subscribeMutation.isPending}
                    className={`w-full btn ${
                      isPopular ? 'btn-primary' : 'btn-secondary'
                    } flex items-center justify-center gap-2 disabled:opacity-50`}
                  >
                    {subscribeMutation.isPending ? (
                      <>
                        <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-current"></div>
                        <span>Traitement...</span>
                      </>
                    ) : (
                      <>
                        <span>S'abonner Maintenant</span>
                        <ArrowRight size={18} />
                      </>
                    )}
                  </button>
                </div>
              )
            })}
          </div>
        ) : (
          <div className="card text-center py-16 animate-scale-in">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-primary-100 to-accent-100 rounded-3xl mb-6">
              <CreditCard className="text-primary-600" size={40} />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-2">Aucun forfait disponible</h3>
            <p className="text-gray-600">Revenez plus tard pour consulter les forfaits d'abonnement</p>
          </div>
        )}
      </div>
    </div>
  )
}
