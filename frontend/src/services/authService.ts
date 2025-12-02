import api from '@/lib/api'

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  phoneNumber?: string
}

export interface DriverRegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  phoneNumber: string
  licenseNumber: string
  licenseExpirationDate: string
  additionalInfo?: string
}

export interface UserResponse {
  id: number
  email: string
  firstName: string
  lastName: string
  phoneNumber?: string
  status?: string
  roles: string[]
  emailVerified?: boolean
  phoneVerified?: boolean
  enabled?: boolean
  profileImageUrl?: string
  lastLoginAt?: string
  createdAt?: string
  updatedAt?: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserResponse
}

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/login', data)
    return response.data
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/register', data)
    return response.data
  },

  registerDriver: async (data: DriverRegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/register/driver', data)
    return response.data
  },

  logout: async (): Promise<void> => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      await api.post('/api/auth/logout', null, {
        headers: { Authorization: `Bearer ${token}` },
      })
    }
  },
}

