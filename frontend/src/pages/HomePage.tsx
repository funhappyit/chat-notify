import { useNavigate } from 'react-router-dom'

export default function HomePage() {
  const navigate = useNavigate()
  const accessToken = localStorage.getItem('accessToken')

  function handleLogout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    navigate('/login')
  }

  if (!accessToken) {
    return (
      <div className="auth-page">
        <h1>Chat Notify</h1>
        <p>로그인이 필요합니다.</p>
        <button onClick={() => navigate('/login')}>로그인하러 가기</button>
      </div>
    )
  }

  return (
    <div className="auth-page">
      <h1>Chat Notify</h1>
      <p>로그인되었습니다.</p>
      <button onClick={handleLogout}>로그아웃</button>
    </div>
  )
}
