import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'
import Login from './pages/Login'
import Register from './pages/Register'
import DriverLogin from './pages/DriverLogin'
import DriverRegister from './pages/DriverRegister'
import Home from './pages/Home'
import Dashboard from './pages/Dashboard'
import Tickets from './pages/Tickets'
import Schedules from './pages/Schedules'
import BusTracking from './pages/BusTracking'
import Subscriptions from './pages/Subscriptions'
import Profile from './pages/Profile'
import Notifications from './pages/Notifications'
import AdminDashboard from './pages/AdminDashboard'
import AdminUsers from './components/admin/AdminUsers'
import AdminBusRoutes from './components/admin/AdminBusRoutes'
import AdminSchedules from './components/admin/AdminSchedules'
import AdminSubscriptions from './components/admin/AdminSubscriptions'
import DriverDashboard from './pages/DriverDashboard'
import DriverStartService from './pages/DriverStartService'
import DriverTrack from './pages/DriverTrack'
import Layout from './components/Layout'
import { AdminRoute, DriverRoute, PassengerRoute, PassengerOrDriverRoute } from './components/RoleRoute'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore()
  return isAuthenticated ? <>{children}</> : <Navigate to="/home" />
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore()
  return !isAuthenticated ? <>{children}</> : <Navigate to="/" />
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/home" element={<PublicRoute><Home /></PublicRoute>} />
        <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
        <Route path="/driver/login" element={<PublicRoute><DriverLogin /></PublicRoute>} />
        <Route path="/driver/register" element={<PublicRoute><DriverRegister /></PublicRoute>} />
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="tickets" element={<PassengerRoute><Tickets /></PassengerRoute>} />
          <Route path="schedules" element={<PassengerOrDriverRoute><Schedules /></PassengerOrDriverRoute>} />
          <Route path="tracking" element={<PassengerOrDriverRoute><BusTracking /></PassengerOrDriverRoute>} />
          <Route path="subscriptions" element={<PassengerRoute><Subscriptions /></PassengerRoute>} />
          <Route path="profile" element={<PassengerOrDriverRoute><Profile /></PassengerOrDriverRoute>} />
          <Route path="notifications" element={<Notifications />} />

          {/* Admin Routes */}
          <Route path="admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
          <Route path="admin/users" element={<AdminRoute><AdminUsers /></AdminRoute>} />
          <Route path="admin/buses" element={<AdminRoute><AdminBusRoutes /></AdminRoute>} />
          <Route path="admin/routes" element={<AdminRoute><AdminBusRoutes /></AdminRoute>} />
          <Route path="admin/schedules" element={<AdminRoute><AdminSchedules /></AdminRoute>} />
          <Route path="admin/subscriptions" element={<AdminRoute><AdminSubscriptions /></AdminRoute>} />
          
          {/* Driver Routes */}
          <Route path="driver" element={<DriverRoute><DriverDashboard /></DriverRoute>} />
          <Route path="driver/start-service" element={<DriverRoute><DriverStartService /></DriverRoute>} />
          <Route path="driver/track" element={<DriverRoute><DriverTrack /></DriverRoute>} />
        </Route>
        <Route path="*" element={<Navigate to="/home" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

