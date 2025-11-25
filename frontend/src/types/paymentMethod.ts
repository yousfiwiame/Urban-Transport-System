/**
 * Types et constantes pour les méthodes de paiement
 * Synchronisé avec le backend PaymentMethod.java
 */

export type PaymentMethod = 
  | 'CREDIT_CARD' 
  | 'DEBIT_CARD' 
  | 'MOBILE_MONEY' 
  | 'CASH' 
  | 'WALLET'

/**
 * Labels français pour affichage dans l'interface
 */
export const PAYMENT_METHOD_LABELS: Record<PaymentMethod, string> = {
  CREDIT_CARD: 'Carte de crédit',
  DEBIT_CARD: 'Carte de débit',
  MOBILE_MONEY: 'Mobile Money',
  CASH: 'Espèces',
  WALLET: 'Portefeuille digital',
}

/**
 * Liste des méthodes de paiement disponibles
 * Utile pour générer des options de select dynamiquement
 */
export const PAYMENT_METHODS: PaymentMethod[] = [
  'CREDIT_CARD',
  'DEBIT_CARD',
  'MOBILE_MONEY',
  'CASH',
  'WALLET',
]

/**
 * Obtenir le label d'une méthode de paiement
 * @param method - La méthode de paiement
 * @returns Le label en français
 */
export const getPaymentMethodLabel = (method: PaymentMethod): string => {
  return PAYMENT_METHOD_LABELS[method] || method
}

