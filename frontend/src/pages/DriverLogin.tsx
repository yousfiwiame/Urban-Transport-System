import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '@/services/authService'
import { useAuthStore } from '@/store/authStore'
import toast from 'react-hot-toast'
import { Bus, Eye, EyeOff } from 'lucide-react'
import { isDriver } from '@/utils/roles'
import Logo from '@/components/Logo'

interface LoginForm {
  email: string
  password: string
}

export default function DriverLogin() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [showPassword, setShowPassword] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>()

  const onSubmit = async (data: LoginForm) => {
    try {
      const response = await authService.login(data)
      
      // Vérifier que l'utilisateur a le rôle DRIVER
      if (!isDriver(response.user.roles)) {
        toast.error('Ce compte n\'est pas un compte conducteur. Veuillez utiliser la connexion passager.')
        return
      }

      setAuth(response.user, response.accessToken, response.refreshToken)
      toast.success('Connexion réussie ! Bienvenue !')
      navigate('/driver')
    } catch (error: any) {
      console.error('Login error:', error)
      const message = error.response?.data?.message || 'Échec de la connexion. Veuillez réessayer.'
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
      <div className="max-w-md w-full space-y-8 relative z-10">
        <div className="card-gradient border-2 border-primary-200">
          <div className="text-center mb-8">
            <div className="flex justify-center mb-4">
              <Logo size="lg" showText={false} to={undefined} />
            </div>
            <h2 className="text-3xl font-bold gradient-text mb-2">CityBus</h2>
            <div className="flex items-center justify-center gap-2 mb-4">
              <Bus className="text-primary-600" size={20} />
              <h3 className="text-xl font-semibold text-gray-800">Espace Conducteur</h3>
            </div>
            <p className="text-gray-600 font-medium">
              Votre ville en mouvement
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email
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
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Mot de passe
              </label>
              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  {...register('password', {
                    required: 'Le mot de passe est requis',
                  })}
                  className={`input pr-10 ${errors.password ? 'border-red-500' : ''}`}
                  placeholder="••••••••"
                  autoComplete="current-password"
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

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full btn btn-primary py-3 text-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? 'Connexion...' : 'Se connecter'}
            </button>
          </form>

          <div className="mt-6 text-center space-y-3">
            <p className="text-sm text-gray-600">
              Pas encore de compte conducteur ?{' '}
              <Link
                to="/driver/register"
                className="font-medium text-primary-600 hover:text-primary-500 transition-colors"
              >
                S'inscrire
              </Link>
            </p>
            <div className="border-t border-gray-200 pt-3">
              <p className="text-sm text-gray-600">
                Vous êtes un passager ?{' '}
                <Link
                  to="/login"
                  className="font-medium text-primary-600 hover:text-primary-500 transition-colors"
                >
                  Connexion passager
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

