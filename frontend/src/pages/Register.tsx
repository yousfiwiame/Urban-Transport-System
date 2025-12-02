import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '@/services/authService'
import { useAuthStore } from '@/store/authStore'
import toast from 'react-hot-toast'
import { Users, Eye, EyeOff } from 'lucide-react'
import Logo from '@/components/Logo'

interface RegisterForm {
  email: string
  password: string
  confirmPassword: string
  firstName: string
  lastName: string
  phoneNumber?: string
}

export default function Register() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
  } = useForm<RegisterForm>()

  const password = watch('password')

  const onSubmit = async (data: RegisterForm) => {
    if (data.password !== data.confirmPassword) {
      toast.error('Les mots de passe ne correspondent pas')
      return
    }

    try {
      const { confirmPassword, ...registerData } = data
      const response = await authService.register(registerData)
      setAuth(response.user, response.accessToken, response.refreshToken)
      toast.success('Inscription réussie ! Bienvenue !')
      navigate('/')
    } catch (error: any) {
      console.error('Registration error:', error)
      const message = error.response?.data?.message || 'Échec de l\'inscription. Veuillez réessayer.'
      toast.error(message)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 relative">
      {/* Background Image */}
      <div
        className="absolute inset-0 z-0"
        style={{
          backgroundImage: 'url(/Casablanca.jpg)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          filter: 'blur(8px)',
        }}
      />
      <div className="absolute inset-0 bg-black/40 z-0" />

      {/* Form Container */}
      <div className="max-w-2xl w-full space-y-8 relative z-10">
        <div className="card-gradient border-2 border-primary-200">
          <div className="text-center mb-8">
            <div className="flex justify-center mb-4">
              <Logo size="lg" showText={false} to={undefined} />
            </div>
            <h2 className="text-3xl font-bold gradient-text mb-2">CityBus</h2>
            <div className="flex items-center justify-center gap-2 mb-4">
              <Users className="text-primary-600" size={20} />
              <h3 className="text-xl font-semibold text-gray-800">Inscription Passager</h3>
            </div>
            <p className="text-gray-600 font-medium">
              Votre ville en mouvement
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Informations personnelles */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-2">
                  Prénom *
                </label>
                <input
                  id="firstName"
                  type="text"
                  {...register('firstName', {
                    required: 'Le prénom est requis',
                    minLength: { value: 2, message: 'Minimum 2 caractères' },
                    maxLength: { value: 100, message: 'Maximum 100 caractères' },
                  })}
                  className={`input ${errors.firstName ? 'border-red-500' : ''}`}
                  placeholder="Jean"
                />
                {errors.firstName && (
                  <p className="mt-1 text-sm text-red-600">{errors.firstName.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-2">
                  Nom *
                </label>
                <input
                  id="lastName"
                  type="text"
                  {...register('lastName', {
                    required: 'Le nom est requis',
                    minLength: { value: 2, message: 'Minimum 2 caractères' },
                    maxLength: { value: 100, message: 'Maximum 100 caractères' },
                  })}
                  className={`input ${errors.lastName ? 'border-red-500' : ''}`}
                  placeholder="Dupont"
                />
                {errors.lastName && (
                  <p className="mt-1 text-sm text-red-600">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            {/* Contact */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                  Email *
                </label>
                <input
                  id="email"
                  type="email"
                  {...register('email', {
                    required: 'L\'email est requis',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Email invalide',
                    },
                  })}
                  className={`input ${errors.email ? 'border-red-500' : ''}`}
                  placeholder="jean.dupont@example.com"
                  autoComplete="email"
                />
                {errors.email && (
                  <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 mb-2">
                  Téléphone (optionnel)
                </label>
                <input
                  id="phoneNumber"
                  type="tel"
                  {...register('phoneNumber', {
                    pattern: {
                      value: /^\+?[1-9]\d{1,14}$/,
                      message: 'Numéro de téléphone invalide (format: +212XXXXXXXXX)',
                    },
                  })}
                  className={`input ${errors.phoneNumber ? 'border-red-500' : ''}`}
                  placeholder="+212612345678"
                  autoComplete="tel"
                />
                {errors.phoneNumber && (
                  <p className="mt-1 text-sm text-red-600">{errors.phoneNumber.message}</p>
                )}
              </div>
            </div>

            {/* Mot de passe */}
            <div className="border-t border-gray-200 pt-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                    Mot de passe *
                  </label>
                  <div className="relative">
                    <input
                      id="password"
                      type={showPassword ? 'text' : 'password'}
                      {...register('password', {
                        required: 'Le mot de passe est requis',
                        minLength: { value: 8, message: 'Minimum 8 caractères' },
                        pattern: {
                          value: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$/,
                          message: 'Doit contenir: majuscule, minuscule, chiffre et caractère spécial',
                        },
                      })}
                      className={`input pr-10 ${errors.password ? 'border-red-500' : ''}`}
                      placeholder="••••••••"
                      autoComplete="new-password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    >
                      {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                  {errors.password && (
                    <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
                    Confirmer le mot de passe *
                  </label>
                  <div className="relative">
                    <input
                      id="confirmPassword"
                      type={showConfirmPassword ? 'text' : 'password'}
                      {...register('confirmPassword', {
                        required: 'Veuillez confirmer le mot de passe',
                        validate: (value) => value === password || 'Les mots de passe ne correspondent pas',
                      })}
                      className={`input pr-10 ${errors.confirmPassword ? 'border-red-500' : ''}`}
                      placeholder="••••••••"
                      autoComplete="new-password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    >
                      {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                  {errors.confirmPassword && (
                    <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
                  )}
                </div>
              </div>
              <p className="mt-2 text-xs text-gray-500">
                Le mot de passe doit contenir au moins 8 caractères avec un chiffre, une majuscule, une minuscule et un caractère spécial
              </p>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full btn btn-primary py-3 text-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? 'Inscription en cours...' : 'S\'inscrire'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Déjà un compte ?{' '}
              <Link to="/login" className="font-medium text-primary-600 hover:text-primary-500 transition-colors">
                Se connecter
              </Link>
            </p>
            <p className="text-sm text-gray-600 mt-2">
              Vous êtes un conducteur ?{' '}
              <Link to="/driver/register" className="font-medium text-primary-600 hover:text-primary-500 transition-colors">
                Inscription conducteur
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
