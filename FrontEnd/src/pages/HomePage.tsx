import { type ReactNode, useEffect, useState } from 'react';
import bnkLogo from '../assets/bnk-logo.png';
import loanHero from '../assets/img-loan-hero.png';
import homeMainImage from '../assets/home_main_image.png';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import {
  fetchMyAccount,
  fetchMyLoans,
  type LoanSummary,
  type MyAccount,
} from '../lib/account';
import '../styles/shell.css';
import './HomePage.css';

const won = (n: number) => Math.round(n).toLocaleString('ko-KR');

function Icon({ name }: { name: 'search' | 'bell' | 'menu' | 'eye' | 'send' | 'atm' | 'card' | 'bag' | 'home' | 'finance' | 'gift' | 'chat' | 'chevron' | 'loan' }) {
  const common = {
    width: 28,
    height: 28,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round' as const,
    strokeLinejoin: 'round' as const,
    'aria-hidden': true,
  };

  const paths: Record<typeof name, ReactNode> = {
    search: (
      <>
        <circle cx="11" cy="11" r="7" />
        <path d="m20 20-4-4" />
      </>
    ),
    bell: (
      <>
        <path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 7h18s-3 0-3-7" />
        <path d="M10 20a2 2 0 0 0 4 0" />
      </>
    ),
    menu: (
      <>
        <path d="M4 6h16" />
        <path d="M4 12h16" />
        <path d="M4 18h16" />
      </>
    ),
    eye: (
      <>
        <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z" />
        <circle cx="12" cy="12" r="2.5" />
      </>
    ),
    send: (
      <>
        <path d="M4 12h13" />
        <path d="m13 6 6 6-6 6" />
      </>
    ),
    atm: (
      <>
        <rect x="7" y="3" width="10" height="18" rx="2" />
        <path d="M10 7h4" />
        <path d="M10 17h4" />
      </>
    ),
    card: (
      <>
        <rect x="3" y="6" width="18" height="12" rx="2" />
        <path d="M3 10h18" />
        <path d="M15 15h3" />
      </>
    ),
    bag: (
      <>
        <path d="M6 8h12l-1 12H7L6 8Z" />
        <path d="M9 8a3 3 0 0 1 6 0" />
        <path d="m9 13 2 2 4-4" />
      </>
    ),
    home: (
      <>
        <path d="m3 11 9-7 9 7" />
        <path d="M5 10v10h14V10" />
        <path d="M9 20v-6h6v6" />
      </>
    ),
    finance: (
      <>
        <circle cx="12" cy="12" r="8" />
        <path d="M12 8v8" />
        <path d="M9.5 10.5c0-1.2 1-2 2.5-2s2.5.8 2.5 2c0 3-5 1.5-5 4 0 1.2 1 2 2.5 2s2.5-.8 2.5-2" />
      </>
    ),
    gift: (
      <>
        <rect x="4" y="9" width="16" height="11" rx="1.5" />
        <path d="M4 13h16" />
        <path d="M12 9v11" />
        <path d="M12 9H8.5A2.5 2.5 0 1 1 12 5.5" />
        <path d="M12 9h3.5A2.5 2.5 0 1 0 12 5.5" />
      </>
    ),
    chat: (
      <>
        <path d="M21 11.5a8.5 8.5 0 0 1-12.8 7.3L3 20l1.4-4.5A8.5 8.5 0 1 1 21 11.5Z" />
        <path d="M8 12h.01" />
        <path d="M12 12h.01" />
        <path d="M16 12h.01" />
      </>
    ),
    chevron: <path d="m9 6 6 6-6 6" />,
    loan: (
      <>
        <path d="M5 19V5h14v14" />
        <path d="M8 9h8" />
        <path d="M8 13h5" />
        <path d="M4 19h16" />
      </>
    ),
  };

  return <svg {...common}>{paths[name]}</svg>;
}

export function HomePage() {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();
  const [account, setAccount] = useState<MyAccount | null>(null);
  const [loans, setLoans] = useState<LoanSummary[]>([]);

  useEffect(() => {
    if (!isLoggedIn) {
      setAccount(null);
      setLoans([]);
      return;
    }
    let alive = true;
    fetchMyAccount()
      .then((a) => alive && setAccount(a))
      .catch(() => {});
    fetchMyLoans()
      .then((l) => alive && setLoans(l))
      .catch(() => {});
    return () => {
      alive = false;
    };
  }, [isLoggedIn]);

  // 실행 완료('9')된 대출을 모두 대출계좌 카드로 노출(캐러셀에서 스와이프)
  const activeLoans = loans.filter((l) => l.statusCode === '9');

  return (
    <main className="app-shell home-page">
      <section className="home-hero" aria-label="BNK 부산은행 홈">
        <header className="home-top">
          <Link to="/" aria-label="홈으로">
            <img className="home-top__logo" src={bnkLogo} alt="BNK 부산은행" />
          </Link>
          <div className="home-top__actions" aria-label="상단 메뉴">
            <button type="button" className="home-icon-btn">
              <Icon name="search" />
              <span>검색</span>
            </button>
            <button type="button" className="home-icon-btn home-icon-btn--badge">
              <Icon name="bell" />
              <span>알림</span>
            </button>
            <button type="button" className="home-icon-btn">
              <Icon name="menu" />
              <span>전체메뉴</span>
            </button>
          </div>
        </header>

        <div className="home-greeting">
          <p>안녕하세요!</p>
          <h1>
            부산은행과 함께
            <br />
            필요한 대출을 쉽게 찾아보세요
          </h1>
        </div>

        <div className="home-skyline" aria-hidden="true">
          <img className="home-skyline__image" src={homeMainImage} alt="" />
        </div>
      </section>

      <section className="account-carousel" aria-label="계좌 정보">
        {!isLoggedIn ? (
          <article className="account-card account-login">
            <div className="account-card__head">
              <span>내 계좌</span>
            </div>
            <div className="account-login__body">
              <span className="account-login__emoji" aria-hidden="true">🎁</span>
              <p className="account-login__title">
                더 많은 혜택이
                <br />
                기다리고 있어요
              </p>
              <p className="account-login__sub">
                로그인하고 내 계좌·대출 현황을 한눈에 확인하세요
              </p>
            </div>
            <div className="account-actions">
              <button
                type="button"
                className="account-actions__primary account-actions__full"
                onClick={() => navigate('/login')}
              >
                시작하기
              </button>
            </div>
          </article>
        ) : (
          <>
        {account && (
        <article className="account-card">
          <div className="account-card__head">
            <span>내 계좌</span>
            <Icon name="chevron" />
          </div>
          <div className="account-card__row">
            <div>
              <strong>자유저축예금</strong>
              <span className="pill">대표</span>
            </div>
          </div>
          <div className="account-balance">
            <span>{won(account.balance)}원</span>
          </div>
          <p className="account-number">부산은행 {account.accountNo}</p>
          <div className="account-actions">
            <button type="button" className="account-actions__primary account-actions__full">
              이체
            </button>
          </div>
        </article>
        )}

        {activeLoans.map((loan) => (
        <article className="account-card account-card--loan" key={loan.loanAccountNo}>
          <div className="account-card__head">
            <span>대출계좌정보</span>
            <Icon name="chevron" />
          </div>
          <div className="account-card__row">
            <div>
              <strong>{loan.productName}</strong>
              <span className="pill pill--blue">{loan.statusName}</span>
            </div>
            <button type="button" className="account-card__more" aria-label="대출 더보기">
              ⋮
            </button>
          </div>
          <div className="loan-summary">
            <div>
              <span>대출금액</span>
              <strong>{won(loan.loanAmount ?? 0)}원</strong>
            </div>
            <div>
              <span>대출금리</span>
              <strong>
                {loan.finalRate != null ? `${loan.finalRate}%` : '-'}
              </strong>
            </div>
          </div>
          <p className="account-number">
            대출계좌 {loan.loanDepositAccountNo ?? '-'}
          </p>
        </article>
        ))}
          </>
        )}
      </section>

      <section className="quick-card" aria-label="빠른 메뉴">
        {[
          ['조회', 'search'],
          ['이체', 'send'],
          ['상품몰', 'bag'],
          ['카드', 'card'],
          ['모바일ATM', 'atm'],
        ].map(([label, icon]) => {
          if (label === '상품몰') {
            return (
              <Link to="/products" className="quick-item" key={label}>
                <Icon name={icon as 'search'} />
                <span>{label}</span>
              </Link>
            );
          }
          return (
            <button type="button" className="quick-item" key={label}>
              <Icon name={icon as 'search'} />
              <span>{label}</span>
            </button>
          );
        })}
        <div className="home-dots" aria-hidden="true">
          <span className="is-active" />
          <span />
          <span />
        </div>
      </section>

      <section className="loan-banner">
        <div>
          <p>부산은행 여신상품몰</p>
          <h2>
            내 조건에 맞는
            <br />
            대출상품 찾기
          </h2>
          <span>한도와 금리 정보를 한눈에</span>
        </div>
        <img src={loanHero} alt="" aria-hidden="true" />
      </section>

      <section className="home-tiles" aria-label="추천 서비스">
        <article className="home-tile home-tile--blue">
          <span>나에게 맞는</span>
          <strong>상품찾기</strong>
          <div className="home-tile__gift" aria-hidden="true" />
        </article>
        <article className="home-tile home-tile--green">
          <span>이번달</span>
          <strong>상환관리</strong>
          <div className="home-tile__chart" aria-hidden="true" />
        </article>
      </section>

      {/* <nav className="bottom-nav" aria-label="하단 탭">
        {[
          ['홈', 'home', true],
          ['금융', 'finance', false],
          ['마이', 'home', false],
          ['혜택', 'gift', false],
          ['챗봇/상담', 'chat', false],
        ].map(([label, icon, active]) => (
          <button
            type="button"
            key={label as string}
            className={`bottom-nav__item ${active ? 'is-active' : ''} ${label === '마이' ? 'bottom-nav__item--my' : ''}`}
          >
            <Icon name={icon as 'home'} />
            <span>{label}</span>
          </button>
        ))}
      </nav> */}
    </main>
  );
}
