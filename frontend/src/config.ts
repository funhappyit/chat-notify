// 배포 환경(Vercel)에서는 백엔드(Render)가 다른 origin이므로 절대 URL이 필요하고,
// 로컬 개발 환경에서는 vite.config.ts의 프록시를 타도록 빈 문자열(상대 경로)을 기본값으로 둔다.
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
