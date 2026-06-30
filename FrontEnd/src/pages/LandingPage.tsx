import { AuthBadge } from '../components/AuthBadge';
import bnkLogo from '../assets/bnk-logo.png';
import './LandingPage.css';
import './auth/auth.css';

export function LandingPage() {
  return (
    <main className="landing-page">
      <div className="landing-page__card">
        <img className="landing-page__logo" src={bnkLogo} alt="BNK 부산은행" />
        <div className="landing-page__auth">
          <AuthBadge />
        </div>
      </div>
    </main>
  );
}
