import api from '@/lib/api'

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
  accountNonLocked?: boolean
  profileImageUrl?: string
  lastLoginAt?: string
  createdAt?: string
  updatedAt?: string
}

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  phoneNumber?: string
  dateOfBirth?: string
  gender?: string
  address?: string
  city?: string
  country?: string
  postalCode?: string
  nationality?: string
  occupation?: string
  emergencyContactName?: string
  emergencyContactPhone?: string
  bio?: string
  preferredLanguage?: string
  notificationsEnabled?: boolean
  emailNotificationsEnabled?: boolean
  smsNotificationsEnabled?: boolean
  pushNotificationsEnabled?: boolean
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export interface ProfileResponse {
  id: number
  userId: number
  dateOfBirth?: string
  gender?: string
  address?: string
  city?: string
  country?: string
  postalCode?: string
  nationality?: string
  occupation?: string
  emergencyContactName?: string
  emergencyContactPhone?: string
  bio?: string
  preferredLanguage?: string
  notificationsEnabled?: boolean
  emailNotificationsEnabled?: boolean
  smsNotificationsEnabled?: boolean
  pushNotificationsEnabled?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const userService = {
  getUserById: async (id: number): Promise<UserResponse> => {
    const response = await api.get<UserResponse>(`/api/users/${id}`)
    return response.data
  },

  getUserByEmail: async (email: string): Promise<UserResponse> => {
    const response = await api.get<UserResponse>(`/api/users/email/${email}`)
    return response.data
  },

  getAllUsers: async (page: number = 0, size: number = 10, sort: string = 'id,asc'): Promise<PageResponse<UserResponse>> => {
    const response = await api.get<PageResponse<UserResponse>>('/api/users', {
      params: { page, size, sort },
    })
    return response.data
  },

  searchUsers: async (keyword: string, page: number = 0, size: number = 10, sort: string = 'id,asc'): Promise<PageResponse<UserResponse>> => {
    const response = await api.get<PageResponse<UserResponse>>('/api/users/search', {
      params: { keyword, page, size, sort },
    })
    return response.data
  },

  updateUser: async (id: number, data: UpdateProfileRequest): Promise<UserResponse> => {
    const response = await api.put<UserResponse>(`/api/users/${id}`, data)
    return response.data
  },

  changePassword: async (id: number, data: ChangePasswordRequest): Promise<void> => {
    await api.put(`/api/users/${id}/change-password`, data)
  },

  deleteUser: async (id: number): Promise<void> => {
    await api.delete(`/api/users/${id}`)
  },

  unlockAccount: async (id: number): Promise<void> => {
    await api.post(`/api/users/${id}/unlock`)
  },

  getUserProfile: async (userId: number): Promise<ProfileResponse> => {
    const response = await api.get<ProfileResponse>(`/api/users/${userId}/profile`)
    return response.data
  },

  updateUserProfile: async (userId: number, data: UpdateProfileRequest): Promise<ProfileResponse> => {
    const response = await api.put<ProfileResponse>(`/api/users/${userId}/profile`, data)
    return response.data
  },

  addRoleToUser: async (userId: number, roleName: string): Promise<UserResponse> => {
    const response = await api.post<UserResponse>(`/api/users/${userId}/roles/${roleName}`)
    return response.data
  },

  removeRoleFromUser: async (userId: number, roleName: string): Promise<UserResponse> => {
    const response = await api.delete<UserResponse>(`/api/users/${userId}/roles/${roleName}`)
    return response.data
  },
}

