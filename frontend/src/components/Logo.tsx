import { Link } from 'react-router-dom'

interface LogoProps {
  size?: 'sm' | 'md' | 'lg' | 'xl'
  showText?: boolean
  showTagline?: boolean
  className?: string
  textClassName?: string
  to?: string
}

export default function Logo({ 
  size = 'md', 
  showText = true, 
  showTagline = false,
  className = '',
  textClassName = '',
  to = '/'
}: LogoProps) {
  const sizeClasses = {
    sm: 'h-8',
    md: 'h-12',
    lg: 'h-16',
    xl: 'h-24'
  }

  const textSizeClasses = {
    sm: 'text-lg',
    md: 'text-2xl',
    lg: 'text-3xl',
    xl: 'text-4xl'
  }

  const taglineSizeClasses = {
    sm: 'text-xs',
    md: 'text-sm',
    lg: 'text-base',
    xl: 'text-lg'
  }

  const content = (
    <div className={`flex items-center gap-3 ${className}`}>
      <img 
        src="/CityBus.png" 
        alt="CityBus Logo" 
        className={`${sizeClasses[size]} w-auto object-contain transition-transform duration-300 hover:scale-110`}
      />
      {showText && (
        <div className="flex flex-col">
          <h1 className={`${textSizeClasses[size]} font-bold gradient-text leading-tight ${textClassName}`}>
            CityBus
          </h1>
          {showTagline && (
            <p className={`${taglineSizeClasses[size]} text-gray-600 font-medium`}>
              Votre ville en mouvement
            </p>
          )}
        </div>
      )}
    </div>
  )

  if (to) {
    return (
      <Link to={to} className="inline-block group">
        {content}
      </Link>
    )
  }

  return content
}

