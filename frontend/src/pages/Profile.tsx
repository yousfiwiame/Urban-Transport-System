import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuthStore } from '@/store/authStore'
import { userService } from '@/services/userService'
import { User, Mail, Shield, Calendar, Edit2, CheckCircle, Phone, MapPin, Globe, Briefcase, Heart, FileText, Bell, Save, X } from 'lucide-react'
import toast from 'react-hot-toast'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import type { UpdateProfileRequest } from '@/services/userService'
import { getRoleDisplayName, getRoleBadgeColor } from '@/utils/roles'

export default function Profile() {
  const { user } = useAuthStore()
  const queryClient = useQueryClient()
  const [isEditing, setIsEditing] = useState(false)
  const [showPasswordForm, setShowPasswordForm] = useState(false)

  const { data: profile, isLoading } = useQuery({
    queryKey: ['userProfile', user?.id],
    queryFn: () => userService.getUserProfile(user!.id),
    enabled: !!user?.id,
  })

  const { register, handleSubmit, reset } = useForm<UpdateProfileRequest>({
    defaultValues: {
      firstName: user?.firstName,
      lastName: user?.lastName,
      phoneNumber: user?.phoneNumber,
      ...profile,
    },
  })

  const updateMutation = useMutation({
    mutationFn: (data: UpdateProfileRequest) => userService.updateUserProfile(user!.id, data),
    onSuccess: () => {
      toast.success('Profil mis à jour avec succès ! 🎉')
      setIsEditing(false)
      queryClient.invalidateQueries({ queryKey: ['userProfile', user?.id] })
      queryClient.invalidateQueries({ queryKey: ['auth'] })
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Échec de la mise à jour')
    },
  })

  const onSubmit = (data: UpdateProfileRequest) => {
    updateMutation.mutate(data)
  }

  const handleCancel = () => {
    reset()
    setIsEditing(false)
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-16">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold gradient-text mb-2">Mon Profil</h1>
        <p className="text-gray-600 text-lg">Gérez vos informations de compte et préférences</p>
      </div>

      {/* Profile Card */}
      <div className="card-gradient animate-slide-up">
        {/* Profile Header */}
        <div className="flex flex-col md:flex-row items-center md:items-start gap-6 mb-8 pb-8 border-b border-gray-200">
          <div className="relative">
            <div className="w-32 h-32 bg-gradient-to-br from-primary-500 via-primary-600 to-accent-600 rounded-3xl flex items-center justify-center shadow-2xl">
              {user?.profileImageUrl ? (
                <img src={user.profileImageUrl} alt="Profile" className="w-full h-full rounded-3xl object-cover" />
              ) : (
                <User className="text-white" size={64} />
              )}
            </div>
            <button 
              onClick={() => setIsEditing(!isEditing)}
              className="absolute bottom-0 right-0 p-3 bg-white rounded-full shadow-lg hover:scale-110 transition-transform border-2 border-primary-200"
            >
              <Edit2 size={18} className="text-primary-600" />
            </button>
          </div>
          <div className="flex-1 text-center md:text-left">
            <h2 className="text-3xl font-bold text-gray-900 mb-2">
              {user?.firstName} {user?.lastName}
            </h2>
            <div className="flex items-center justify-center md:justify-start gap-2 mb-4">
              <Mail className="text-gray-400" size={18} />
              <p className="text-gray-600 text-lg">{user?.email}</p>
              {user?.emailVerified && (
                <span title="Email vérifié">
                  <CheckCircle className="text-success-600" size={18} />
                </span>
              )}
            </div>
            {user?.phoneNumber && (
              <div className="flex items-center justify-center md:justify-start gap-2 mb-4">
                <Phone className="text-gray-400" size={18} />
                <p className="text-gray-600">{user.phoneNumber}</p>
                {user?.phoneVerified && (
                  <span title="Téléphone vérifié">
                    <CheckCircle className="text-success-600" size={18} />
                  </span>
                )}
              </div>
            )}
            <div className="flex flex-wrap gap-2 justify-center md:justify-start">
              {user?.roles?.map((role) => (
                <span
                  key={role}
                  className={`px-4 py-1.5 rounded-full text-sm font-semibold flex items-center gap-1 border ${getRoleBadgeColor(role)}`}
                >
                  <Shield size={14} />
                  {getRoleDisplayName(role)}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Profile Form */}
        {isEditing ? (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Prénom</label>
                <input
                  {...register('firstName', { minLength: 2, maxLength: 100 })}
                  className="input"
                  placeholder="Prénom"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Nom</label>
                <input
                  {...register('lastName', { minLength: 2, maxLength: 100 })}
                  className="input"
                  placeholder="Nom"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Téléphone</label>
                <input
                  {...register('phoneNumber')}
                  className="input"
                  placeholder="+212 xxxxxx"
                />
              </div>
              {profile && (
                <>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Date de Naissance</label>
                    <input
                      type="date"
                      {...register('dateOfBirth')}
                      className="input"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Genre</label>
                    <select {...register('gender')} className="input">
                      <option value="">Sélectionner</option>
                      <option value="MALE">Homme</option>
                      <option value="FEMALE">Femme</option>
                      <option value="OTHER">Autre</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Adresse</label>
                    <input
                      {...register('address')}
                      className="input"
                      placeholder="Adresse complète"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Ville</label>
                    <input
                      {...register('city')}
                      className="input"
                      placeholder="Ville"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Pays</label>
                    <input
                      {...register('country')}
                      className="input"
                      placeholder="Pays"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Code Postal</label>
                    <input
                      {...register('postalCode')}
                      className="input"
                      placeholder="Code postal"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Nationalité</label>
                    <input
                      {...register('nationality')}
                      className="input"
                      placeholder="Nationalité"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Profession</label>
                    <input
                      {...register('occupation')}
                      className="input"
                      placeholder="Profession"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Contact d'Urgence - Nom</label>
                    <input
                      {...register('emergencyContactName')}
                      className="input"
                      placeholder="Nom du contact"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Contact d'Urgence - Téléphone</label>
                    <input
                      {...register('emergencyContactPhone')}
                      className="input"
                      placeholder="+212 xxxxxx"
                    />
                  </div>
                  <div className="md:col-span-2">
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Bio</label>
                    <textarea
                      {...register('bio', { maxLength: 1000 })}
                      className="input"
                      rows={4}
                      placeholder="Parlez-nous de vous..."
                    />
                  </div>
                </>
              )}
            </div>

            {/* Notification Preferences */}
            {profile && (
              <div className="pt-6 border-t border-gray-200">
                <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <Bell className="text-primary-600" size={20} />
                  Préférences de Notification
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <label className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100">
                    <input
                      type="checkbox"
                      {...register('notificationsEnabled')}
                      className="w-5 h-5 text-primary-600 rounded"
                    />
                    <span className="font-medium">Notifications activées</span>
                  </label>
                  <label className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100">
                    <input
                      type="checkbox"
                      {...register('emailNotificationsEnabled')}
                      className="w-5 h-5 text-primary-600 rounded"
                    />
                    <span className="font-medium">Notifications par email</span>
                  </label>
                  <label className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100">
                    <input
                      type="checkbox"
                      {...register('smsNotificationsEnabled')}
                      className="w-5 h-5 text-primary-600 rounded"
                    />
                    <span className="font-medium">Notifications par SMS</span>
                  </label>
                  <label className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl cursor-pointer hover:bg-gray-100">
                    <input
                      type="checkbox"
                      {...register('pushNotificationsEnabled')}
                      className="w-5 h-5 text-primary-600 rounded"
                    />
                    <span className="font-medium">Notifications push</span>
                  </label>
                </div>
              </div>
            )}

            <div className="flex gap-3 pt-4">
              <button
                type="submit"
                disabled={updateMutation.isPending}
                className="btn btn-primary flex items-center gap-2"
              >
                <Save size={18} />
                {updateMutation.isPending ? 'Enregistrement...' : 'Enregistrer'}
              </button>
              <button
                type="button"
                onClick={handleCancel}
                className="btn btn-secondary flex items-center gap-2"
              >
                <X size={18} />
                Annuler
              </button>
            </div>
          </form>
        ) : (
          <>
            {/* Profile Details */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="p-6 bg-gradient-to-br from-primary-50 to-primary-100 rounded-2xl border border-primary-100">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-primary-100 rounded-lg">
                    <Mail className="text-primary-600" size={20} />
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 font-medium">Adresse Email</p>
                    <p className="font-bold text-gray-900 text-lg">{user?.email}</p>
                  </div>
                </div>
                <p className="text-xs text-gray-500">Votre email principal pour les notifications</p>
              </div>

              <div className="p-6 bg-gradient-to-br from-accent-50 to-success-50 rounded-2xl border border-accent-100">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-accent-100 rounded-lg">
                    <Shield className="text-accent-600" size={20} />
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 font-medium">Statut du Compte</p>
                    <div className="flex items-center gap-2">
                      <CheckCircle className="text-success-600" size={20} />
                      <p className="font-bold text-gray-900 text-lg">
                        {user?.status === 'ACTIVE' ? 'Actif' : user?.status || 'Actif'}
                      </p>
                    </div>
                  </div>
                </div>
                <p className="text-xs text-gray-500">Votre compte est vérifié et actif</p>
              </div>

              {user?.phoneNumber && (
                <div className="p-6 bg-gradient-to-br from-primary-50 to-accent-50 rounded-2xl border border-primary-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-primary-100 rounded-lg">
                      <Phone className="text-primary-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Téléphone</p>
                      <p className="font-bold text-gray-900 text-lg">{user.phoneNumber}</p>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500">Votre numéro de téléphone</p>
                </div>
              )}

              {profile?.address && (
                <div className="p-6 bg-gradient-to-br from-primary-50 to-accent-50 rounded-2xl border border-primary-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-primary-100 rounded-lg">
                      <MapPin className="text-primary-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Adresse</p>
                      <p className="font-bold text-gray-900 text-lg">{profile.address}</p>
                      {profile.city && <p className="text-sm text-gray-600">{profile.city}, {profile.country}</p>}
                    </div>
                  </div>
                </div>
              )}

              {profile?.occupation && (
                <div className="p-6 bg-gradient-to-br from-accent-50 to-accent-100 rounded-2xl border border-accent-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-accent-100 rounded-lg">
                      <Briefcase className="text-accent-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Profession</p>
                      <p className="font-bold text-gray-900 text-lg">{profile.occupation}</p>
                    </div>
                  </div>
                </div>
              )}

              {profile?.dateOfBirth && (
                <div className="p-6 bg-gradient-to-br from-primary-50 to-accent-50 rounded-2xl border border-primary-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-primary-100 rounded-lg">
                      <Calendar className="text-primary-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Date de Naissance</p>
                      <p className="font-bold text-gray-900 text-lg">
                        {new Date(profile.dateOfBirth).toLocaleDateString('fr-FR')}
                      </p>
                    </div>
                  </div>
                </div>
              )}

              {profile?.nationality && (
                <div className="p-6 bg-gradient-to-br from-accent-50 to-accent-100 rounded-2xl border border-accent-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-accent-100 rounded-lg">
                      <Globe className="text-accent-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Nationalité</p>
                      <p className="font-bold text-gray-900 text-lg">{profile.nationality}</p>
                    </div>
                  </div>
                </div>
              )}

              {profile?.emergencyContactName && (
                <div className="p-6 bg-gradient-to-br from-red-50 to-pink-50 rounded-2xl border border-red-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-red-100 rounded-lg">
                      <Heart className="text-red-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Contact d'Urgence</p>
                      <p className="font-bold text-gray-900 text-lg">{profile.emergencyContactName}</p>
                      {profile.emergencyContactPhone && (
                        <p className="text-sm text-gray-600">{profile.emergencyContactPhone}</p>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {profile?.bio && (
                <div className="md:col-span-2 p-6 bg-gradient-to-br from-gray-50 to-primary-50 rounded-2xl border border-gray-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 bg-gray-100 rounded-lg">
                      <FileText className="text-gray-600" size={20} />
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Bio</p>
                    </div>
                  </div>
                  <p className="text-gray-700">{profile.bio}</p>
                </div>
              )}
            </div>

            {/* Actions */}
            <div className="mt-8 pt-8 border-t border-gray-200">
              <div className="flex flex-wrap gap-3">
                <button 
                  onClick={() => setIsEditing(true)}
                  className="btn btn-primary flex items-center gap-2"
                >
                  <Edit2 size={18} />
                  Modifier le Profil
                </button>
                <button 
                  onClick={() => setShowPasswordForm(!showPasswordForm)}
                  className="btn btn-secondary flex items-center gap-2"
                >
                  <Shield size={18} />
                  Changer le Mot de Passe
                </button>
              </div>
            </div>

            {/* Change Password Form */}
            {showPasswordForm && (
              <div className="mt-6 p-6 bg-gray-50 rounded-xl border border-gray-200">
                <h3 className="text-lg font-bold text-gray-900 mb-4">Changer le Mot de Passe</h3>
                <ChangePasswordForm userId={user!.id} onClose={() => setShowPasswordForm(false)} />
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

function ChangePasswordForm({ userId, onClose }: { userId: number; onClose: () => void }) {
  const { register, handleSubmit, formState: { errors }, watch } = useForm<{
    currentPassword: string
    newPassword: string
    confirmPassword: string
  }>()
  const [loading, setLoading] = useState(false)

  const newPassword = watch('newPassword')

  const onSubmit = async (data: { currentPassword: string; newPassword: string }) => {
    if (data.newPassword !== watch('confirmPassword')) {
      toast.error('Les mots de passe ne correspondent pas')
      return
    }

    setLoading(true)
    try {
      await userService.changePassword(userId, {
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      })
      toast.success('Mot de passe changé avec succès ! 🎉')
      onClose()
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Échec du changement de mot de passe')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">Mot de Passe Actuel</label>
        <input
          type="password"
          autoComplete="current-password"
          {...register('currentPassword', { required: 'Le mot de passe actuel est requis' })}
          className="input"
          placeholder="••••••••"
        />
        {errors.currentPassword && (
          <p className="mt-1 text-sm text-red-600">{errors.currentPassword.message}</p>
        )}
      </div>
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">Nouveau Mot de Passe</label>
        <input
          type="password"
          autoComplete="new-password"
          {...register('newPassword', {
            required: 'Le nouveau mot de passe est requis',
            minLength: { value: 8, message: 'Au moins 8 caractères' },
            pattern: {
              value: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$/,
              message: 'Doit contenir un chiffre, une majuscule, une minuscule et un caractère spécial'
            }
          })}
          className="input"
          placeholder="••••••••"
        />
        {errors.newPassword && (
          <p className="mt-1 text-sm text-red-600">{errors.newPassword.message}</p>
        )}
      </div>
      <div>
        <label className="block text-sm font-semibold text-gray-700 mb-2">Confirmer le Mot de Passe</label>
        <input
          type="password"
          autoComplete="new-password"
          {...register('confirmPassword', {
            required: 'Veuillez confirmer le mot de passe',
            validate: (value) => value === newPassword || 'Les mots de passe ne correspondent pas'
          })}
          className="input"
          placeholder="••••••••"
        />
        {errors.confirmPassword && (
          <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
        )}
      </div>
      <div className="flex gap-3">
        <button
          type="submit"
          disabled={loading}
          className="btn btn-primary flex-1"
        >
          {loading ? 'Changement...' : 'Changer le Mot de Passe'}
        </button>
        <button
          type="button"
          onClick={onClose}
          className="btn btn-secondary"
        >
          Annuler
        </button>
      </div>
    </form>
  )
}
