import { Link } from 'react-router-dom'
import { Bus, MapPin, Clock, CreditCard, Shield, Zap, ArrowRight, Check, Star } from 'lucide-react'
import Logo from '@/components/Logo'

export default function Home() {
  const features = [
    {
      icon: Bus,
      title: 'Suivi en Temps Réel',
      description: 'Suivez votre bus en temps réel avec des mises à jour GPS en direct',
      color: 'from-primary-500 to-primary-600',
    },
    {
      icon: Clock,
      title: 'Réservation Facile',
      description: 'Réservez des billets en quelques secondes avec notre processus simplifié',
      color: 'from-primary-500 to-accent-500',
    },
    {
      icon: MapPin,
      title: 'Planification d\'Itinéraire',
      description: 'Trouvez les meilleurs itinéraires et horaires pour votre voyage',
      color: 'from-accent-500 to-accent-600',
    },
    {
      icon: CreditCard,
      title: 'Paiements Flexibles',
      description: 'Plusieurs options de paiement et plans d\'abonnement',
      color: 'from-accent-500 to-accent-600',
    },
    {
      icon: Shield,
      title: 'Sécurisé et Fiable',
      description: 'Vos données et paiements sont protégés par chiffrement',
      color: 'from-primary-600 to-primary-700',
    },
    {
      icon: Zap,
      title: 'Rapide et Fiable',
      description: 'Réservation ultra-rapide et confirmations instantanées',
      color: 'from-accent-500 to-accent-600',
    },
  ]

  const benefits = [
    'Pas de frais cachés',
    'Support client 24/7',
    'Compatible mobile',
    'Notifications instantanées',
    'Billets QR code',
    'Réductions d\'abonnement',
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-gray-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-primary-600 via-primary-700 to-accent-600 text-white">
        {/* Casablanca Background Image - Blurred */}
        <div 
          className="absolute inset-0 bg-cover bg-center bg-no-repeat opacity-20"
          style={{
            backgroundImage: 'url(/Casablanca.jpg)',
            filter: 'blur(8px)',
            transform: 'scale(1.1)'
          }}
        ></div>
        {/* Overlay gradient */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary-600/90 via-primary-700/85 to-accent-600/90"></div>
        {/* Pattern overlay */}
        <div className="absolute inset-0 opacity-30" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")` }}></div>
        <div className="absolute top-20 left-20 w-96 h-96 bg-white/10 rounded-full blur-3xl animate-bounce-subtle"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-accent-500/20 rounded-full blur-3xl animate-bounce-subtle" style={{ animationDelay: '1s' }}></div>
        
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
          <div className="text-center">
            <div className="inline-flex items-center justify-center mb-8 animate-scale-in">
              <div className="bg-white/95 backdrop-blur-sm rounded-3xl p-8 shadow-2xl">
                <Logo size="xl" showText={false} to={undefined} />
              </div>
            </div>
            <h1 className="text-5xl md:text-7xl font-bold mb-6 animate-slide-down text-white">
              CityBus
            </h1>
            <p className="text-3xl md:text-4xl font-bold mb-6 animate-slide-down">
              <span className="bg-gradient-to-r from-white via-accent-200 to-white bg-clip-text text-transparent">
                Votre ville en mouvement
              </span>
            </p>
            <p className="text-xl md:text-2xl text-primary-100 mb-12 max-w-3xl mx-auto animate-slide-up">
              Découvrez un transport urbain fluide et moderne. Réservez des billets, suivez les bus en temps réel et gérez vos déplacements en toute simplicité.
            </p>

            {/* Choix du type d'utilisateur */}
            <div className="max-w-5xl mx-auto mb-16 animate-fade-in" style={{ animationDelay: '200ms' }}>
              <h3 className="text-2xl font-semibold mb-8 text-center">Vous êtes :</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* Passager */}
                <div className="group bg-white/10 backdrop-blur-md rounded-3xl p-8 border-2 border-white/30 hover:bg-white/20 hover:border-white/50 transition-all duration-300 transform hover:scale-105">
                  <div className="flex items-center justify-center w-20 h-20 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl mx-auto mb-6 group-hover:scale-110 transition-transform">
                    <MapPin className="text-white" size={40} />
                  </div>
                  <h4 className="text-2xl font-bold text-white mb-3 text-center">Passager</h4>
                  <p className="text-primary-100 mb-6 text-center">
                    Réservez vos billets, suivez les bus en temps réel et gérez vos abonnements
                  </p>
                  <div className="flex flex-col gap-3">
                    <Link
                      to="/register"
                      className="w-full px-6 py-3 bg-white text-primary-700 rounded-xl font-bold text-center shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center gap-2"
                    >
                      S'inscrire
                      <ArrowRight size={18} />
                    </Link>
                    <Link
                      to="/login"
                      className="w-full px-6 py-3 bg-white/10 backdrop-blur-sm text-white rounded-xl font-semibold text-center border-2 border-white/30 hover:bg-white/20 transition-all duration-300"
                    >
                      Se connecter
                    </Link>
                  </div>
                </div>

                {/* Conducteur */}
                <div className="group bg-white/10 backdrop-blur-md rounded-3xl p-8 border-2 border-white/30 hover:bg-white/20 hover:border-white/50 transition-all duration-300 transform hover:scale-105">
                  <div className="flex items-center justify-center w-20 h-20 bg-gradient-to-br from-accent-500 to-accent-600 rounded-2xl mx-auto mb-6 group-hover:scale-110 transition-transform">
                    <Bus className="text-white" size={40} />
                  </div>
                  <h4 className="text-2xl font-bold text-white mb-3 text-center">Conducteur</h4>
                  <p className="text-primary-100 mb-6 text-center">
                    Accédez à votre tableau de bord professionnel et gérez vos trajets quotidiens
                  </p>
                  <div className="flex flex-col gap-3">
                    <Link
                      to="/driver/register"
                      className="w-full px-6 py-3 bg-accent-500 text-white rounded-xl font-bold text-center shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-300 flex items-center justify-center gap-2"
                    >
                      S'inscrire
                      <ArrowRight size={18} />
                    </Link>
                    <Link
                      to="/driver/login"
                      className="w-full px-6 py-3 bg-white/10 backdrop-blur-sm text-white rounded-xl font-semibold text-center border-2 border-white/30 hover:bg-white/20 transition-all duration-300"
                    >
                      Se connecter
                    </Link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
              Tout Ce Dont Vous Avez Besoin
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Des fonctionnalités puissantes conçues pour rendre vos déplacements sans effort
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => {
              const Icon = feature.icon
              return (
                <div
                  key={feature.title}
                  className="group p-8 bg-gradient-to-br from-white to-gray-50 rounded-3xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:scale-105 border border-gray-100 animate-slide-up"
                  style={{ animationDelay: `${index * 100}ms` }}
                >
                  <div className={`inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br ${feature.color} rounded-2xl mb-6 shadow-lg group-hover:scale-110 transition-transform`}>
                    <Icon className="text-white" size={32} />
                  </div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">{feature.title}</h3>
                  <p className="text-gray-600 leading-relaxed">{feature.description}</p>
                </div>
              )
            })}
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="py-24 bg-gradient-to-br from-primary-50 via-white to-accent-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6">
                Pourquoi Nous Choisir ?
              </h2>
              <p className="text-xl text-gray-600 mb-8">
                Nous nous engageons à rendre le transport urbain accessible, pratique et agréable pour tous.
              </p>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {benefits.map((benefit) => (
                  <div
                    key={benefit}
                    className="flex items-center gap-3 p-4 bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow"
                  >
                    <div className="flex-shrink-0 w-8 h-8 bg-gradient-to-br from-primary-500 to-accent-500 rounded-lg flex items-center justify-center">
                      <Check className="text-white" size={18} />
                    </div>
                    <span className="font-semibold text-gray-900">{benefit}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="relative">
              <div className="relative bg-gradient-to-br from-primary-600 to-accent-600 rounded-3xl p-12 shadow-2xl">
                <div className="absolute inset-0 opacity-20 rounded-3xl" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")` }}></div>
                <div className="relative z-10 text-white">
                  <div className="flex items-center gap-2 mb-6">
                    {[...Array(5)].map((_, i) => (
                      <Star key={i} className="fill-yellow-300 text-yellow-300" size={24} />
                    ))}
                  </div>
                  <blockquote className="text-2xl font-bold mb-6">
                    "La meilleure application de transport que j'ai jamais utilisée. Simple, rapide et fiable !"
                  </blockquote>
                  <div className="flex items-center gap-4">
                    <div className="w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
                      <Bus className="text-white" size={32} />
                    </div>
                    <div>
                      <p className="font-bold text-lg">Fatima El Amrani</p>
                      <p className="text-primary-200">Usager Quotidien</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24 bg-gradient-to-br from-primary-600 via-primary-700 to-accent-600 text-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">
            Prêt à Commencer Votre Voyage ?
          </h2>
          <p className="text-xl text-primary-100 mb-8 max-w-2xl mx-auto">
            Rejoignez des milliers d'usagers satisfaits qui nous font confiance pour leurs besoins de transport quotidiens.
          </p>
          <Link
            to="/register"
            className="inline-flex items-center gap-3 px-10 py-5 bg-white text-primary-700 rounded-2xl font-bold text-lg shadow-2xl hover:shadow-3xl transform hover:scale-105 transition-all duration-300"
          >
            Créer un Compte Gratuit
            <ArrowRight size={24} />
          </Link>
        </div>
      </section>
    </div>
  )
}

