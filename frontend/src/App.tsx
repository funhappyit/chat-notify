import { Route, Routes } from 'react-router-dom'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import RoomListPage from './pages/RoomListPage'
import ChatRoomPage from './pages/ChatRoomPage'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/rooms" element={<RoomListPage />} />
      <Route path="/rooms/:id" element={<ChatRoomPage />} />
    </Routes>
  )
}

export default App
