import { useState } from 'react'
import { Search, X } from 'lucide-react'

interface BusSearchPanelProps {
  onSearch: (searchTerm: string) => void
}

export default function BusSearchPanel({ onSearch }: BusSearchPanelProps) {
  const [searchTerm, setSearchTerm] = useState('')

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    onSearch(searchTerm)
  }

  const handleReset = () => {
    setSearchTerm('')
    onSearch('')
  }

  return (
    <div className="absolute top-4 left-4 z-[1000] bg-white rounded-xl shadow-2xl p-6 min-w-[320px] max-w-[400px] border border-gray-100">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
          <Search className="text-primary-600" size={20} />
          Recherche de Bus
        </h3>
      </div>

      <form onSubmit={handleSearch} className="space-y-4">
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            Numéro de bus, Immatriculation ou Modèle
          </label>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Ex: BUS-001, Mercedes..."
            className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white text-gray-900"
          />
        </div>

        <div className="flex gap-3 pt-2">
          <button
            type="submit"
            className="flex-1 btn btn-primary flex items-center justify-center gap-2"
          >
            <Search size={18} />
            Rechercher
          </button>
          <button
            type="button"
            onClick={handleReset}
            className="flex-1 btn btn-secondary flex items-center justify-center gap-2"
          >
            <X size={18} />
            Réinitialiser
          </button>
        </div>
      </form>
    </div>
  )
}

