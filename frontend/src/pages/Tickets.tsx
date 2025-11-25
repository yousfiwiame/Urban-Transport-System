import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ticketService, type PurchaseTicketRequest } from '@/services/ticketService'
import { Ticket as TicketIcon, Plus, Clock, DollarSign, QrCode, CheckCircle, Download } from 'lucide-react'
import toast from 'react-hot-toast'
import { useState } from 'react'
import { useAuthStore } from '@/store/authStore'

export default function Tickets() {
  const { user } = useAuthStore()
  const queryClient = useQueryClient()
  const [showPurchaseForm, setShowPurchaseForm] = useState(false)
  
  const { data: tickets = [], isLoading } = useQuery({
    queryKey: ['myTickets', user?.id],
    queryFn: () => user?.id ? ticketService.getMyTickets(user.id) : Promise.resolve([]),
    enabled: !!user?.id,
  })

  const purchaseMutation = useMutation({
    mutationFn: (request: PurchaseTicketRequest) => ticketService.purchaseTicket(request),
    onSuccess: () => {
      toast.success('Billet acheté avec succès! 🎉')
      queryClient.invalidateQueries({ queryKey: ['myTickets'] })
      setShowPurchaseForm(false)
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erreur lors de l\'achat du billet')
    },
  })

  const handlePurchase = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    
    if (!user?.id) {
      toast.error('Vous devez être connecté pour acheter un billet')
      return
    }

    const formData = new FormData(e.currentTarget)
    const request: PurchaseTicketRequest = {
      idPassager: user.id,
      idTrajet: parseInt(formData.get('idTrajet') as string),
      prix: parseFloat(formData.get('prix') as string),
      methodePaiement: formData.get('methodePaiement') as any,
    }

    purchaseMutation.mutate(request)
  }

  const handleDownloadPDF = async (ticketId: number) => {
    try {
      toast.loading('Téléchargement du billet...', { id: 'download' })
      await ticketService.downloadTicketPDF(ticketId)
      toast.success('Billet téléchargé avec succès!', { id: 'download' })
    } catch (error) {
      toast.error('Erreur lors du téléchargement du billet', { id: 'download' })
      console.error('Error downloading ticket:', error)
    }
  }

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-4xl font-bold gradient-text mb-2">Mes Billets</h1>
          <p className="text-gray-600 text-lg">Gérez vos billets et votre historique de voyage</p>
        </div>
        <button
          onClick={() => setShowPurchaseForm(!showPurchaseForm)}
          className="btn btn-primary flex items-center gap-2 self-start md:self-auto"
        >
          <Plus size={20} />
          Acheter un Billet
        </button>
      </div>

      {/* Purchase Form */}
      {showPurchaseForm && (
        <div className="card-gradient animate-slide-down border-2 border-primary-200">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl">
              <Plus className="text-white" size={24} />
            </div>
            <h2 className="text-2xl font-bold text-gray-900">Acheter un Nouveau Billet</h2>
          </div>
          <form onSubmit={handlePurchase} className="space-y-5">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  ID de l'Itinéraire
                </label>
                <input
                  type="number"
                  name="idTrajet"
                  required
                  className="input"
                  placeholder="Entrez l'ID de l'itinéraire"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Prix (DH)</label>
                <div className="relative">
                  <DollarSign className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="number"
                    name="prix"
                    required
                    step="0.01"
                    className="input pl-12"
                    placeholder="0.00"
                  />
                </div>
              </div>
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Méthode de Paiement
              </label>
              <select name="methodePaiement" required className="input">
                <option value="">Sélectionnez une méthode de paiement</option>
                <option value="CREDIT_CARD">💳 Carte de Crédit</option>
                <option value="DEBIT_CARD">💳 Carte de Débit</option>
                <option value="MOBILE_PAYMENT">📱 Paiement Mobile</option>
              </select>
            </div>
            <div className="flex gap-3 pt-2">
              <button 
                type="submit" 
                disabled={purchaseMutation.isPending}
                className="btn btn-primary flex-1 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {purchaseMutation.isPending ? (
                  <>
                    <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2 inline-block"></div>
                    Traitement...
                  </>
                ) : (
                  'Acheter le Billet'
                )}
              </button>
              <button
                type="button"
                onClick={() => setShowPurchaseForm(false)}
                className="btn btn-secondary"
                disabled={purchaseMutation.isPending}
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Tickets Grid */}
      {isLoading ? (
        <div className="card text-center py-16">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          </div>
          <p className="text-gray-500 text-lg">Chargement de vos billets...</p>
        </div>
      ) : tickets && tickets.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tickets.map((ticket: any, index: number) => (
            <div 
              key={ticket.id} 
              className="card hover:scale-105 transition-all duration-300 animate-slide-up group"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              {/* Header */}
              <div className="flex items-start justify-between mb-6">
                <div className="flex items-center gap-3">
                  <div className="p-3 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl shadow-lg group-hover:scale-110 transition-transform">
                    <TicketIcon className="text-white" size={24} />
                  </div>
                  <div>
                    <h3 className="font-bold text-lg text-gray-900">Ticket #{ticket.id}</h3>
                    <p className="text-sm text-gray-600">Route #{ticket.idTrajet}</p>
                  </div>
                </div>
                <span
                  className={`px-3 py-1.5 rounded-full text-xs font-bold flex items-center gap-1 ${
                    ticket.statut === 'ACTIVE'
                      ? 'bg-success-100 text-success-700'
                      : 'bg-gray-100 text-gray-700'
                  }`}
                >
                  {ticket.statut === 'ACTIVE' && <CheckCircle size={12} />}
                  {ticket.statut}
                </span>
              </div>

              {/* Details */}
              <div className="space-y-3 mb-6">
                <div className="flex items-center justify-between p-3 bg-gradient-to-r from-gray-50 to-primary-50 rounded-xl">
                  <div className="flex items-center gap-2 text-gray-600">
                    <DollarSign size={18} />
                    <span className="text-sm font-medium">Price</span>
                  </div>
                  <span className="text-xl font-bold text-gray-900">${ticket.prix.toFixed(2)}</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-gradient-to-r from-gray-50 to-accent-50 rounded-xl">
                  <div className="flex items-center gap-2 text-gray-600">
                    <Clock size={18} />
                    <span className="text-sm font-medium">Date d'Achat</span>
                  </div>
                  <span className="text-sm font-semibold text-gray-900">
                    {new Date(ticket.dateAchat).toLocaleDateString('fr-FR', {
                      month: 'short',
                      day: 'numeric',
                      year: 'numeric',
                    })}
                  </span>
                </div>
              </div>

              {/* QR Code */}
              {ticket.qrCode && (
                <div className="pt-6 border-t border-gray-200">
                  <div className="flex items-center gap-2 mb-3">
                    <QrCode size={18} className="text-primary-600" />
                    <p className="text-sm font-semibold text-gray-700">Code QR</p>
                  </div>
                  <div className="bg-white p-4 rounded-xl border-2 border-gray-100 flex justify-center">
                    <img 
                      src={ticket.qrCode} 
                      alt="Code QR" 
                      className="w-32 h-32 object-contain"
                    />
                  </div>
                </div>
              )}

              {/* Download PDF Button */}
              <div className="pt-4">
                <button
                  onClick={() => handleDownloadPDF(ticket.id)}
                  className="w-full btn btn-secondary flex items-center justify-center gap-2"
                >
                  <Download size={18} />
                  Télécharger le Billet
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card text-center py-16 animate-scale-in">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-primary-100 to-accent-100 rounded-3xl mb-6">
            <TicketIcon className="text-primary-600" size={40} />
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-2">Aucun billet pour le moment</h3>
          <p className="text-gray-600 mb-6">Commencez votre voyage en achetant votre premier billet</p>
          <button
            onClick={() => setShowPurchaseForm(true)}
            className="btn btn-primary inline-flex items-center gap-2"
          >
            <Plus size={20} />
            Acheter Votre Premier Billet
          </button>
        </div>
      )}
    </div>
  )
}
