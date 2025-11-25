import { useState } from 'react'
import { CreditCard, Plus, X, Edit, Trash2 } from 'lucide-react'

export default function AdminSubscriptions() {
  const [showCreateModal, setShowCreateModal] = useState(false)

  // Mock data
  const plans = [
    { id: 1, name: 'Mensuel Standard', price: 200, duration: '1 mois', features: ['Trajets illimités', 'Toutes lignes'], users: 487, color: 'from-blue-500 to-blue-600' },
    { id: 2, name: 'Annuel Premium', price: 2000, duration: '12 mois', features: ['Trajets illimités', 'Priorité embarquement', 'Support 24/7'], users: 156, color: 'from-purple-500 to-purple-600' },
    { id: 3, name: 'Hebdomadaire', price: 60, duration: '7 jours', features: ['Trajets illimités', 'Lignes principales'], users: 234, color: 'from-green-500 to-green-600' },
    { id: 4, name: 'Étudiant', price: 120, duration: '1 mois', features: ['Trajets illimités', '-40% de réduction', 'Carte requise'], users: 892, color: 'from-accent-500 to-accent-600' },
  ]


  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
          <p className="text-sm font-medium text-blue-700 mb-1">Abonnements Actifs</p>
          <p className="text-3xl font-bold text-blue-900">1,769</p>
        </div>
        <div className="card bg-gradient-to-br from-green-50 to-green-100">
          <p className="text-sm font-medium text-green-700 mb-1">Revenus Mensuels</p>
          <p className="text-3xl font-bold text-green-900">247k MAD</p>
        </div>
        <div className="card bg-gradient-to-br from-purple-50 to-purple-100">
          <p className="text-sm font-medium text-purple-700 mb-1">Nouveaux ce Mois</p>
          <p className="text-3xl font-bold text-purple-900">187</p>
        </div>
        <div className="card bg-gradient-to-br from-red-50 to-red-100">
          <p className="text-sm font-medium text-red-700 mb-1">Expirations ce Mois</p>
          <p className="text-3xl font-bold text-red-900">45</p>
        </div>
      </div>

      {/* Plans */}
      <div>
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Plans d'Abonnement</h2>
          <button 
            onClick={() => setShowCreateModal(true)}
            className="btn btn-primary flex items-center gap-2"
          >
            <Plus size={18} />
            Nouveau Plan
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {plans.map((plan) => (
            <div key={plan.id} className="card hover:shadow-lg transition-all">
              <div className={`w-full h-2 bg-gradient-to-r ${plan.color} rounded-t-xl -mt-6 mb-4`}></div>
              
              <div className="text-center mb-4">
                <h3 className="text-xl font-bold text-gray-900 mb-2">{plan.name}</h3>
                <div className="flex items-baseline justify-center gap-1">
                  <span className="text-4xl font-bold text-primary-600">{plan.price}</span>
                  <span className="text-gray-600">MAD</span>
                </div>
                <p className="text-sm text-gray-500 mt-1">{plan.duration}</p>
              </div>

              <ul className="space-y-2 mb-6">
                {plan.features.map((feature, index) => (
                  <li key={index} className="flex items-center gap-2 text-sm text-gray-700">
                    <div className="w-1.5 h-1.5 bg-primary-600 rounded-full"></div>
                    {feature}
                  </li>
                ))}
              </ul>

              <div className="border-t border-gray-200 pt-4 mb-4">
                <p className="text-center text-sm text-gray-600">
                  <span className="font-bold text-primary-600">{plan.users}</span> abonnés actifs
                </p>
              </div>

              <div className="flex gap-2">
                <button className="flex-1 btn btn-secondary text-sm">
                  <Edit size={14} className="inline mr-1" />
                  Modifier
                </button>
                <button className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <CreditCard className="text-primary-600" size={24} />
                Créer un Plan
              </h2>
              <button
                onClick={() => setShowCreateModal(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Nom du Plan</label>
                <input type="text" className="input" placeholder="Mensuel Standard" />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Prix (MAD)</label>
                <input type="number" className="input" placeholder="200" />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Durée</label>
                <select className="input">
                  <option>1 semaine</option>
                  <option>1 mois</option>
                  <option>3 mois</option>
                  <option>6 mois</option>
                  <option>12 mois</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                <textarea className="input" rows={3} placeholder="Décrivez les avantages..."></textarea>
              </div>
            </div>

            <div className="border-t border-gray-200 px-6 py-4 flex gap-3">
              <button
                onClick={() => setShowCreateModal(false)}
                className="flex-1 btn btn-secondary"
              >
                Annuler
              </button>
              <button className="flex-1 btn btn-primary">
                Créer le Plan
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

