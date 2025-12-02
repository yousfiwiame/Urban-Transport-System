import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { userService, UserResponse } from '@/services/userService'
import { useAuthStore } from '@/store/authStore'
import { Users, Search, Trash2, Unlock, Mail, Phone, X, CheckCircle, AlertCircle, Shield, Plus, Minus } from 'lucide-react'
import toast from 'react-hot-toast'
import { useState } from 'react'
import { getRoleDisplayName, getRoleBadgeColor, ROLES } from '@/utils/roles'

export default function AdminUsers() {
  const { user } = useAuthStore()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [selectedUser, setSelectedUser] = useState<UserResponse | null>(null)
  const [showRoleModal, setShowRoleModal] = useState(false)
  const size = 10

  const { data: usersData, isLoading } = useQuery({
    queryKey: ['adminUsers', page, searchKeyword],
    queryFn: () => searchKeyword
      ? userService.searchUsers(searchKeyword, page, size)
      : userService.getAllUsers(page, size),
  })

  const deleteMutation = useMutation({
    mutationFn: userService.deleteUser,
    onSuccess: () => {
      toast.success('Utilisateur supprimé avec succès')
      queryClient.invalidateQueries({ queryKey: ['adminUsers'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec de la suppression')
    },
  })

  const unlockMutation = useMutation({
    mutationFn: userService.unlockAccount,
    onSuccess: () => {
      toast.success('Compte déverrouillé avec succès')
      queryClient.invalidateQueries({ queryKey: ['adminUsers'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec du déverrouillage')
    },
  })

  const addRoleMutation = useMutation({
    mutationFn: ({ userId, roleName }: { userId: number; roleName: string }) =>
      userService.addRoleToUser(userId, roleName),
    onSuccess: () => {
      toast.success('Rôle ajouté avec succès')
      queryClient.invalidateQueries({ queryKey: ['adminUsers'] })
      setShowRoleModal(false)
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec de l\'ajout du rôle')
    },
  })

  const removeRoleMutation = useMutation({
    mutationFn: ({ userId, roleName }: { userId: number; roleName: string }) =>
      userService.removeRoleFromUser(userId, roleName),
    onSuccess: () => {
      toast.success('Rôle retiré avec succès')
      queryClient.invalidateQueries({ queryKey: ['adminUsers'] })
      setShowRoleModal(false)
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec du retrait du rôle')
    },
  })

  const handleDelete = (userId: number) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.')) {
      deleteMutation.mutate(userId)
    }
  }

  const handleUnlock = (userId: number) => {
    unlockMutation.mutate(userId)
  }

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setPage(0)
  }

  const handleManageRoles = (userItem: UserResponse) => {
    setSelectedUser(userItem)
    setShowRoleModal(true)
  }

  const handleAddRole = (roleName: string) => {
    if (selectedUser) {
      addRoleMutation.mutate({ userId: selectedUser.id, roleName })
    }
  }

  const handleRemoveRole = (roleName: string) => {
    if (selectedUser) {
      if (window.confirm(`Êtes-vous sûr de vouloir retirer le rôle ${getRoleDisplayName(roleName)} ?`)) {
        removeRoleMutation.mutate({ userId: selectedUser.id, roleName })
      }
    }
  }

  const availableRoles = [ROLES.ADMIN, ROLES.PASSENGER, ROLES.DRIVER]

  return (
    <div className="space-y-6">
      {/* Search Bar */}
      <div className="card-gradient">
        <form onSubmit={handleSearch} className="flex gap-3">
          <div className="flex-1 relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              placeholder="Rechercher par nom, email ou téléphone..."
              className="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
          <button
            type="submit"
            className="btn btn-primary flex items-center gap-2"
          >
            <Search size={18} />
            Rechercher
          </button>
          {searchKeyword && (
            <button
              type="button"
              onClick={() => {
                setSearchKeyword('')
                setPage(0)
              }}
              className="btn btn-secondary flex items-center gap-2"
            >
              <X size={18} />
              Effacer
            </button>
          )}
        </form>
      </div>

      {/* Users Table */}
      <div className="card-gradient">
        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
          </div>
        ) : usersData && usersData.content.length > 0 ? (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-4 px-4 font-semibold text-gray-700">Utilisateur</th>
                    <th className="text-left py-4 px-4 font-semibold text-gray-700">Contact</th>
                    <th className="text-left py-4 px-4 font-semibold text-gray-700">Rôle</th>
                    <th className="text-left py-4 px-4 font-semibold text-gray-700">Statut</th>
                    <th className="text-left py-4 px-4 font-semibold text-gray-700">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {usersData.content.map((userItem: UserResponse) => (
                    <tr
                      key={userItem.id}
                      className="border-b border-gray-100 hover:bg-gray-50 transition-colors"
                    >
                      <td className="py-4 px-4">
                        <div>
                          <p className="font-semibold text-gray-900">
                            {userItem.firstName} {userItem.lastName}
                          </p>
                          <p className="text-sm text-gray-500">
                            ID: {userItem.id}
                          </p>
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <div className="space-y-1">
                          <div className="flex items-center gap-2 text-sm">
                            <Mail size={14} className="text-gray-400" />
                            <span className="text-gray-700">{userItem.email}</span>
                            {userItem.emailVerified && (
                              <span title="Email vérifié">
                                <CheckCircle size={14} className="text-success-600" />
                              </span>
                            )}
                          </div>
                          {userItem.phoneNumber && (
                            <div className="flex items-center gap-2 text-sm">
                              <Phone size={14} className="text-gray-400" />
                              <span className="text-gray-700">{userItem.phoneNumber}</span>
                              {userItem.phoneVerified && (
                                <span title="Téléphone vérifié">
                                  <CheckCircle size={14} className="text-success-600" />
                                </span>
                              )}
                            </div>
                          )}
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <div className="flex flex-wrap gap-2 items-center">
                          {userItem.roles?.map((role: string) => (
                            <span
                              key={role}
                              className={`px-3 py-1 rounded-full text-xs font-semibold border ${getRoleBadgeColor(role)}`}
                            >
                              {getRoleDisplayName(role)}
                            </span>
                          ))}
                          <button
                            onClick={() => handleManageRoles(userItem)}
                            className="p-1.5 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors"
                            title="Gérer les rôles"
                          >
                            <Shield size={16} />
                          </button>
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <div className="space-y-1">
                          <div className="flex items-center gap-2">
                            {userItem.enabled ? (
                              <CheckCircle size={16} className="text-success-600" />
                            ) : (
                              <AlertCircle size={16} className="text-red-600" />
                            )}
                            <span className={`text-sm font-medium ${userItem.enabled ? 'text-success-700' : 'text-red-700'}`}>
                              {userItem.enabled ? 'Actif' : 'Désactivé'}
                            </span>
                          </div>
                          {userItem.accountNonLocked === false && (
                            <span className="text-xs text-red-600 flex items-center gap-1">
                              <AlertCircle size={12} />
                              Compte verrouillé
                            </span>
                          )}
                          {userItem.status && (
                            <span className="text-xs text-gray-600">
                              {userItem.status}
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <div className="flex items-center gap-2">
                          {userItem.accountNonLocked === false && (
                            <button
                              onClick={() => handleUnlock(userItem.id)}
                              disabled={unlockMutation.isPending}
                              className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                              title="Déverrouiller le compte"
                            >
                              <Unlock size={18} />
                            </button>
                          )}
                          {userItem.id !== user?.id && (
                            <button
                              onClick={() => handleDelete(userItem.id)}
                              disabled={deleteMutation.isPending}
                              className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                              title="Supprimer l'utilisateur"
                            >
                              <Trash2 size={18} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {usersData.totalPages > 1 && (
              <div className="mt-6 flex items-center justify-between border-t border-gray-200 pt-6">
                <div className="text-sm text-gray-600">
                  Affichage de {page * size + 1} à {Math.min((page + 1) * size, usersData.totalElements)} sur {usersData.totalElements} utilisateurs
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => setPage(p => Math.max(0, p - 1))}
                    disabled={page === 0}
                    className="btn btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Précédent
                  </button>
                  <span className="flex items-center px-4 text-gray-700 font-medium">
                    Page {page + 1} sur {usersData.totalPages}
                  </span>
                  <button
                    onClick={() => setPage(p => Math.min(usersData.totalPages - 1, p + 1))}
                    disabled={page >= usersData.totalPages - 1}
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
            <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
              <Users className="text-gray-400" size={32} />
            </div>
            <p className="text-gray-500 text-lg mb-2">Aucun utilisateur trouvé</p>
            <p className="text-gray-400 text-sm">
              {searchKeyword ? 'Essayez avec d\'autres mots-clés' : 'Aucun utilisateur dans le système'}
            </p>
          </div>
        )}
      </div>

      {/* Role Management Modal */}
      {showRoleModal && selectedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full max-h-[80vh] overflow-y-auto">
            <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
              <div>
                <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                  <Shield className="text-primary-600" size={24} />
                  Gérer les Rôles
                </h2>
                <p className="text-sm text-gray-600 mt-1">
                  {selectedUser.firstName} {selectedUser.lastName}
                </p>
              </div>
              <button
                onClick={() => setShowRoleModal(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            <div className="p-6 space-y-6">
              {/* Current Roles */}
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Rôles actuels</h3>
                {selectedUser.roles && selectedUser.roles.length > 0 ? (
                  <div className="space-y-2">
                    {selectedUser.roles.map((role) => (
                      <div
                        key={role}
                        className="flex items-center justify-between p-3 bg-gray-50 rounded-xl"
                      >
                        <span className={`px-3 py-1 rounded-full text-sm font-semibold border ${getRoleBadgeColor(role)}`}>
                          {getRoleDisplayName(role)}
                        </span>
                        <button
                          onClick={() => handleRemoveRole(role)}
                          disabled={removeRoleMutation.isPending || selectedUser.roles.length <= 1}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                          title={selectedUser.roles.length <= 1 ? "Impossible de retirer le dernier rôle" : "Retirer ce rôle"}
                        >
                          <Minus size={18} />
                        </button>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-gray-500 text-sm">Aucun rôle assigné</p>
                )}
              </div>

              {/* Available Roles to Add */}
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Ajouter un rôle</h3>
                <div className="space-y-2">
                  {availableRoles
                    .filter((role) => !selectedUser.roles?.includes(role))
                    .map((role) => (
                      <div
                        key={role}
                        className="flex items-center justify-between p-3 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
                      >
                        <span className={`px-3 py-1 rounded-full text-sm font-semibold border ${getRoleBadgeColor(role)}`}>
                          {getRoleDisplayName(role)}
                        </span>
                        <button
                          onClick={() => handleAddRole(role)}
                          disabled={addRoleMutation.isPending}
                          className="p-2 text-success-600 hover:bg-success-50 rounded-lg transition-colors"
                          title="Ajouter ce rôle"
                        >
                          <Plus size={18} />
                        </button>
                      </div>
                    ))}
                  {availableRoles.filter((role) => !selectedUser.roles?.includes(role)).length === 0 && (
                    <p className="text-gray-500 text-sm">Tous les rôles sont déjà assignés</p>
                  )}
                </div>
              </div>

              {/* Info Message */}
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <div className="flex gap-3">
                  <AlertCircle className="text-blue-600 flex-shrink-0" size={20} />
                  <div className="text-sm text-blue-800">
                    <p className="font-semibold mb-1">Informations importantes :</p>
                    <ul className="list-disc list-inside space-y-1">
                      <li>Un utilisateur doit avoir au moins un rôle</li>
                      <li>Les changements prennent effet immédiatement</li>
                      <li>L'utilisateur devra se reconnecter pour voir les nouveaux accès</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <div className="sticky bottom-0 bg-gray-50 border-t border-gray-200 px-6 py-4">
              <button
                onClick={() => setShowRoleModal(false)}
                className="w-full btn btn-secondary"
              >
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

