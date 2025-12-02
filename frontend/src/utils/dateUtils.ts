/**
 * Utility functions for date and time formatting in French
 */

export const DAYS_FR: Record<string, string> = {
  MONDAY: 'Lundi',
  TUESDAY: 'Mardi',
  WEDNESDAY: 'Mercredi',
  THURSDAY: 'Jeudi',
  FRIDAY: 'Vendredi',
  SATURDAY: 'Samedi',
  SUNDAY: 'Dimanche',
  // English versions as well
  Monday: 'Lundi',
  Tuesday: 'Mardi',
  Wednesday: 'Mercredi',
  Thursday: 'Jeudi',
  Friday: 'Vendredi',
  Saturday: 'Samedi',
  Sunday: 'Dimanche',
}

export const MONTHS_FR: Record<number, string> = {
  0: 'Janvier',
  1: 'Février',
  2: 'Mars',
  3: 'Avril',
  4: 'Mai',
  5: 'Juin',
  6: 'Juillet',
  7: 'Août',
  8: 'Septembre',
  9: 'Octobre',
  10: 'Novembre',
  11: 'Décembre',
}

export const MONTHS_SHORT_FR: Record<number, string> = {
  0: 'Jan',
  1: 'Fév',
  2: 'Mar',
  3: 'Avr',
  4: 'Mai',
  5: 'Juin',
  6: 'Juil',
  7: 'Août',
  8: 'Sep',
  9: 'Oct',
  10: 'Nov',
  11: 'Déc',
}

/**
 * Convert English day name to French
 * @param day - Day name in English (e.g., "MONDAY", "Monday")
 * @returns Day name in French (e.g., "Lundi")
 */
export const formatDayFR = (day: string): string => {
  return DAYS_FR[day] || day
}

/**
 * Format a date with French month name
 * @param date - Date object
 * @returns Formatted date string (e.g., "15 Janvier 2025")
 */
export const formatDateFR = (date: Date): string => {
  const day = date.getDate()
  const month = MONTHS_FR[date.getMonth()]
  const year = date.getFullYear()
  return `${day} ${month} ${year}`
}

/**
 * Format a date with short French month name
 * @param date - Date object
 * @returns Formatted date string (e.g., "15 Jan 2025")
 */
export const formatDateShortFR = (date: Date): string => {
  const day = date.getDate()
  const month = MONTHS_SHORT_FR[date.getMonth()]
  const year = date.getFullYear()
  return `${day} ${month} ${year}`
}

/**
 * Get current day name in French
 * @returns Current day name (e.g., "Lundi")
 */
export const getCurrentDayFR = (): string => {
  const days = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi']
  return days[new Date().getDay()]
}

/**
 * Format time in 24h format
 * @param date - Date object or time string
 * @returns Formatted time (e.g., "14:30")
 */
export const formatTime = (date: Date | string): string => {
  if (typeof date === 'string') {
    date = new Date(date)
  }
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

/**
 * Format duration in French from days
 * @param durationDays - Duration in days
 * @returns Formatted duration string
 */
export const formatDurationDays = (durationDays: number): string => {
  if (durationDays === 1) return '1 jour'
  if (durationDays < 7) return `${durationDays} jours`
  if (durationDays === 7) return '1 semaine'
  if (durationDays < 30) {
    const weeks = Math.floor(durationDays / 7)
    const remainingDays = durationDays % 7
    if (remainingDays === 0) return `${weeks} semaine${weeks > 1 ? 's' : ''}`
    return `${weeks} semaine${weeks > 1 ? 's' : ''} et ${remainingDays} jour${remainingDays > 1 ? 's' : ''}`
  }
  if (durationDays === 30) return '1 mois'
  if (durationDays < 365) {
    const months = Math.floor(durationDays / 30)
    return `${months} mois`
  }
  if (durationDays === 365) return '1 an'
  const years = Math.floor(durationDays / 365)
  const remainingMonths = Math.floor((durationDays % 365) / 30)
  if (remainingMonths === 0) return `${years} an${years > 1 ? 's' : ''}`
  return `${years} an${years > 1 ? 's' : ''} et ${remainingMonths} mois`
}

/**
 * Format duration in French from months
 * @param durationMonths - Duration in months
 * @returns Formatted duration string
 */
export const formatDuration = (durationMonths: number): string => {
  if (durationMonths < 1) {
    const days = Math.round(durationMonths * 30)
    return `${days} jour${days > 1 ? 's' : ''}`
  } else if (durationMonths === 1) {
    return '1 mois'
  } else if (durationMonths < 12) {
    return `${durationMonths} mois`
  } else if (durationMonths === 12) {
    return '1 an'
  } else {
    const years = Math.floor(durationMonths / 12)
    const remainingMonths = durationMonths % 12
    if (remainingMonths === 0) {
      return `${years} an${years > 1 ? 's' : ''}`
    }
    return `${years} an${years > 1 ? 's' : ''} et ${remainingMonths} mois`
  }
}

/**
 * Format price in MAD
 * @param price - Price amount
 * @returns Formatted price string (e.g., "200 MAD")
 */
export const formatPrice = (price: number): string => {
  return `${price.toFixed(2)} MAD`
}
