/**
 * Utilitaires pour la gestion des dates et traductions
 */

/**
 * Traduction des jours de la semaine en français
 */
export const DAYS_FR: Record<string, string> = {
  MONDAY: 'Lundi',
  TUESDAY: 'Mardi',
  WEDNESDAY: 'Mercredi',
  THURSDAY: 'Jeudi',
  FRIDAY: 'Vendredi',
  SATURDAY: 'Samedi',
  SUNDAY: 'Dimanche',
}

/**
 * Traduction abrégée des jours
 */
export const DAYS_SHORT_FR: Record<string, string> = {
  MONDAY: 'Lun',
  TUESDAY: 'Mar',
  WEDNESDAY: 'Mer',
  THURSDAY: 'Jeu',
  FRIDAY: 'Ven',
  SATURDAY: 'Sam',
  SUNDAY: 'Dim',
}

/**
 * Traduit un jour ou une liste de jours en français
 */
export function translateDay(day: string): string {
  return DAYS_FR[day] || day
}

/**
 * Traduit un jour en version abrégée
 */
export function translateDayShort(day: string): string {
  return DAYS_SHORT_FR[day] || day
}

/**
 * Traduit une liste de jours en français
 */
export function translateDays(days: string[]): string[] {
  return days.map(day => translateDay(day))
}

/**
 * Formate une liste de jours pour l'affichage
 * Ex: ["MONDAY", "TUESDAY", "WEDNESDAY"] => "Lun, Mar, Mer..."
 */
export function formatDaysList(days: string[], maxDays: number = 3): string {
  if (!days || days.length === 0) return 'N/A'
  
  // Si tous les jours de la semaine
  if (days.length === 7) return 'Tous les jours'
  
  // Si du lundi au vendredi
  const weekdays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']
  if (days.length === 5 && weekdays.every(day => days.includes(day))) {
    return 'Lun - Ven'
  }
  
  // Si weekend uniquement
  if (days.length === 2 && days.includes('SATURDAY') && days.includes('SUNDAY')) {
    return 'Weekend'
  }
  
  // Afficher les premiers jours avec abréviation
  const displayDays = days.slice(0, maxDays).map(day => translateDayShort(day))
  const result = displayDays.join(', ')
  
  if (days.length > maxDays) {
    return `${result}...`
  }
  
  return result
}

/**
 * Formate une date en français
 */
export function formatDateFR(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  })
}

/**
 * Formate une heure
 */
export function formatTime(time: string | Date): string {
  if (typeof time === 'string' && time.includes(':')) {
    // Si c'est déjà au format HH:MM
    return time
  }
  
  const d = typeof time === 'string' ? new Date(time) : time
  return d.toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

