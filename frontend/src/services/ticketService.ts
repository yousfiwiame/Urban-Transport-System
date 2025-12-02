import api from '@/lib/api'
import type { PaymentMethod } from '@/types/paymentMethod'
import type { TicketStatus } from '@/types/ticketStatus'

export interface TicketResponse {
  idTicket: number
  ticketNumber: string
  idPassager: number
  idTrajet: number
  prix: number
  statut: string
  qrCode: string
  validFrom: string
  validUntil: string
  dateAchat: string
  isExpired?: boolean
  isValid?: boolean
  remainingTimeInMinutes?: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface TicketStatistics {
  totalTickets: number
  activeTickets: number
  usedTickets: number
  expiredTickets: number
  cancelledTickets: number
  totalRevenue: number
  activeRevenue: number
  usedRevenue: number
}

export interface PurchaseTicketRequest {
  idPassager: number
  idTrajet: number
  prix: number
  methodePaiement: PaymentMethod
}

export interface PurchaseTicketResponse {
  ticket: TicketResponse
  transactionId?: string
  message?: string
}

export interface CreateTicketRequest {
  idPassager: number
  idTrajet: number
  prix: number
  methodePaiement: PaymentMethod
  statut?: TicketStatus
  dateAchat?: string
  dateValidite?: string
}

export interface UpdateTicketRequest {
  idPassager?: number
  idTrajet?: number
  prix?: number
  methodePaiement?: PaymentMethod
  statut?: TicketStatus
  dateValidite?: string
  remarque?: string
}

export const ticketService = {
  // Get all tickets (admin only)
  getAllTickets: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'dateAchat',
    sortDirection: string = 'desc'
  ): Promise<PageResponse<TicketResponse>> => {
    const response = await api.get<PageResponse<TicketResponse>>('/api/tickets', {
      params: { page, size, sortBy, sortDirection },
    })
    return response.data
  },

  // Get ticket statistics (admin only)
  getStatistics: async (): Promise<TicketStatistics> => {
    const response = await api.get<TicketStatistics>('/api/tickets/statistics')
    return response.data
  },

  // Get user tickets
  getMyTickets: async (userId: number): Promise<TicketResponse[]> => {
    const response = await api.get<TicketResponse[]>(`/api/tickets/passager/${userId}`)
    return response.data
  },

  // Get ticket by ID
  getTicketById: async (id: number): Promise<TicketResponse> => {
    const response = await api.get<TicketResponse>(`/api/tickets/${id}`)
    return response.data
  },

  // Cancel ticket
  cancelTicket: async (id: number, reason?: string): Promise<TicketResponse> => {
    const response = await api.delete<TicketResponse>(`/api/tickets/${id}`, {
      params: { reason },
    })
    return response.data
  },

  // Purchase a new ticket
  purchaseTicket: async (request: PurchaseTicketRequest): Promise<PurchaseTicketResponse> => {
    const response = await api.post<PurchaseTicketResponse>('/api/tickets/purchase', request)
    return response.data
  },

  // Download ticket PDF
  downloadTicketPDF: async (ticketId: number): Promise<void> => {
    const response = await api.get(`/api/tickets/${ticketId}/download`, {
      responseType: 'blob',
    })
    
    // Create a blob from the PDF data
    const blob = new Blob([response.data], { type: 'application/pdf' })
    
    // Create a link element and trigger download
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `billet-${ticketId}.pdf`
    document.body.appendChild(link)
    link.click()
    
    // Cleanup
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  },

  // Create ticket (admin only)
  createTicketByAdmin: async (request: CreateTicketRequest): Promise<TicketResponse> => {
    const response = await api.post<TicketResponse>('/api/tickets/admin', request)
    return response.data
  },

  // Update ticket (admin only)
  updateTicketByAdmin: async (id: number, request: UpdateTicketRequest): Promise<TicketResponse> => {
    const response = await api.put<TicketResponse>(`/api/tickets/admin/${id}`, request)
    return response.data
  },
}
