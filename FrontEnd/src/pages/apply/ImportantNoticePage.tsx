import { useEffect, useState, type ReactNode } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useApplyExit } from './useApplyExit';
import LottieDefault from 'lottie-react';
import imgFinancial from '../../assets/notice/img_financial.png';
import imgComplete from '../../assets/notice/img_complete.png';
import step2Coins from '../../assets/notice/step2_ani1.png';
import step1ani1 from '../../assets/notice/step1_ani1.json';
import step1ani2 from '../../assets/notice/step1_ani2.json';
import step1ani3 from '../../assets/notice/step1_ani3.json';
import step2ani2 from '../../assets/notice/step2_ani2.json';
import step3ani1 from '../../assets/notice/step3_ani1.json';
import step3ani2 from '../../assets/notice/step3_ani2.json';
import step3ani3 from '../../assets/notice/step3_ani3.json';
import step4ani1 from '../../assets/notice/step4_ani1.json';
import step5ani1 from '../../assets/notice/step5_ani1.json';
import step6ani1 from '../../assets/notice/step6_ani1.json';
import '../../styles/shell.css';
import './apply.css';
import './notice.css';

// Vite 의존성 사전 번들이 lottie-react(UMD)를 네임스페이스 객체로 감싸는 경우가 있어
// default 컴포넌트를 방어적으로 추출한다(dev/build 모두 정상 동작).
const Lottie = ((LottieDefault as unknown as { default?: typeof LottieDefault })
  .default ?? LottieDefault) as typeof LottieDefault;

/* ===== 공용 소형 컴포넌트 ===== */
function WarnBox({ children }: { children: ReactNode }) {
  return (
    <div className="nwarn">
      <div className="nwarn__icon" aria-hidden="true">
        ⚠️
      </div>
      {children}
    </div>
  );
}
const Em = ({ children }: { children: ReactNode }) => (
  <span className="nem">{children}</span>
);

function NoticeLottie({ data, size = 200 }: { data: object; size?: number }) {
  return (
    <Lottie
      animationData={data}
      loop={false}
      className="nlottie"
      style={{ height: size }}
    />
  );
}

function NoticeCard({
  title,
  desc,
  data,
  terms,
}: {
  title: string;
  desc: ReactNode;
  data: object;
  terms?: { k: string; v: string }[];
}) {
  return (
    <div className="ncard">
      <h3 className="ncard__title">{title}</h3>
      <p className="ncard__desc">{desc}</p>
      <NoticeLottie data={data} size={170} />
      {terms && (
        <ul className="ncard__terms">
          {terms.map((t) => (
            <li key={t.k}>
              <b>{t.k}</b> : {t.v}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

/* ===== 콘텐츠 슬라이드 정의 ===== */
interface Slide {
  title: string;
  sub?: string;
  body: ReactNode;
}

const SLIDES: Slide[] = [
  {
    title: '대출금리란?',
    sub: '대출금리의 종류는 크게 다음과 같이 구분 됩니다.',
    body: (
      <NoticeCard
        title="고정금리"
        desc={
          <>
            대출 실행 시 결정된 금리가
            <br />
            대출만기까지 동일하게 유지됩니다
          </>
        }
        data={step1ani1}
      />
    ),
  },
  {
    title: '대출금리란?',
    sub: '대출금리의 종류는 크게 다음과 같이 구분 됩니다.',
    body: (
      <NoticeCard
        title="변동금리"
        desc={
          <>
            대출 기준금리가 변동될 때마다 대출금리도 변동
            <br />
            변동금리 주기는 3, 6, 12개월 등 상품마다 달라요
          </>
        }
        data={step1ani2}
      />
    ),
  },
  {
    title: '대출금리란?',
    sub: '대출금리의 종류는 크게 다음과 같이 구분 됩니다.',
    body: (
      <NoticeCard
        title="혼합금리"
        desc={
          <>
            일정 기간은 고정금리가 적용된 후
            <br />
            변동금리로 전환되는 방식이에요
          </>
        }
        data={step1ani3}
      />
    ),
  },
  {
    title: '대출금리란?',
    sub: '대출금리의 종류는 크게 다음과 같이 구분 됩니다.',
    body: (
      <WarnBox>
        <p>
          대출금리는 개별약정에 의해 별도로 정한 항목
          <br />
          (기준금리, 거래실적에 따른 감면금리 등) 이외에는
          <br />
          <Em>대출 만기일까지 동일하게 적용됩니다.</Em>
        </p>
        <p>
          단, 신용등급 변동, 기한연장 등
          <br />
          대출 조건이 변경되는 경우
          <br />
          <Em>대출금리가 변경될 수 있습니다.</Em>
        </p>
      </WarnBox>
    ),
  },
  {
    title: '중도상환수수료란?',
    sub: '대출의 상환기일이 도래하기 전에 대출금을 상환할 경우 고객이 부담하는 금액입니다.',
    body: (
      <div className="ncenter">
        <img className="nimg" src={step2Coins} alt="" />
        <p className="ntext">
          최초 대출 취급일로 부터 3년까지 적용하며 상품에 따라{' '}
          <Em>면제 또는 0.07% ~ 0.71%</Em>입니다.
        </p>
        <NoticeLottie data={step2ani2} size={120} />
      </div>
    ),
  },
  {
    title: '대출상환방식이란?',
    sub: '상환방법에 따라 동일한 금리, 한도로 대출을 진행하시더라도 만기까지 부담해야하는 총 원리금 부담액이 달라질 수 있습니다.',
    body: (
      <NoticeCard
        title="원금균등상환"
        desc="대출 원금을 일정하게 나누어 갚는 방식으로 만기가 다가올수록 상환할 이자는 줄어듭니다."
        data={step3ani1}
        terms={[
          { k: '원금', v: '대출원금' },
          { k: '균등', v: '일정하게 나눠서' },
          { k: '상환', v: '갚기' },
        ]}
      />
    ),
  },
  {
    title: '대출상환방식이란?',
    sub: '상환방법에 따라 동일한 금리, 한도로 대출을 진행하시더라도 만기까지 부담해야하는 총 원리금 부담액이 달라질 수 있습니다.',
    body: (
      <NoticeCard
        title="원리금균등상환"
        desc="대출원금과 이자를 더해 나누어 갚는 방식입니다. 1회차와 마지막 차의 상환금액은 거의 동일합니다. 마지막 차로 갈 수록 이자가 줄어들기 때문에 대출원금은 올라갑니다."
        data={step3ani2}
        terms={[
          { k: '원리금', v: '대출원금+이자' },
          { k: '균등', v: '일정하게 나눠서' },
          { k: '상환', v: '갚기' },
        ]}
      />
    ),
  },
  {
    title: '대출상환방식이란?',
    sub: '상환방법에 따라 동일한 금리, 한도로 대출을 진행하시더라도 만기까지 부담해야하는 총 원리금 부담액이 달라질 수 있습니다.',
    body: (
      <NoticeCard
        title="만기일시상환"
        desc="대출기간동안 이자만 내다가 마지막 차에 대출 원금을 한번에 갚는 방식입니다."
        data={step3ani3}
        terms={[
          { k: '만기', v: '(대출원금을)마지막차에' },
          { k: '일시', v: '한번에' },
          { k: '상환', v: '갚기' },
        ]}
      />
    ),
  },
  {
    title: '담보물에 관한 사항',
    sub: '대출과 관련하여 담보물을 제공하는 경우, 은행은 채권보전을 위해 담보물에 담보권(저당권·질권 등)을 설정할 수 있습니다.',
    body: (
      <>
        <NoticeLottie data={step4ani1} size={200} />
        <WarnBox>
          <p>
            담보의 제공은 고객님의 재산상 손실을 가져올 수도 있는 중요한
            법률행위이므로 반드시 별도 작성하시는 근저당권(근질권) 설정계약서의
            내용을 상세하게 확인하시기 바랍니다.
          </p>
        </WarnBox>
      </>
    ),
  },
  {
    title: '대출 청약철회권 이란?',
    sub: '일반금융소비자는 ①계약체결일, ②계약서류를 받은날, ③대출금 수령일 중 나중에 발생한 날 로부터 14일 내에 계약에 대한 청약 철회할 수 있습니다.',
    body: (
      <>
        <NoticeLottie data={step5ani1} size={200} />
        <WarnBox>
          <p>
            영업점, 인터넷뱅킹, 스마트뱅킹 등으로 청약 철회의 의사표시를 해야
            하며, 중도상환수수료 이자, 인지세 등은 반환하여야 합니다.
          </p>
          <p>
            중도상환수수료는 면제가 되며, 5영업일 이내에 해당 대출과 관련한
            대출정보가 삭제됩니다.
          </p>
        </WarnBox>
      </>
    ),
  },
  {
    title: '금리인하 요구권이란?',
    sub: '고객님의 신용상태에 현저한 변동이 있는 경우(취업, 승진, 재산증가, 개인신용평점 상승 등) 증빙자료를 은행에 제출하고, 이를 바탕으로 재평가를 받아 금리인하를 요구할 수 있는 제도입니다.',
    body: (
      <>
        <NoticeLottie data={step6ani1} size={200} />
        <WarnBox>
          <p>
            단, 신용상태 개선이 경미하거나 신용상태가 금리에 미치지 않는
            상품(정책자금대출, 집단대출 등)의 경우는 수용되지 않을 수 있습니다.
          </p>
        </WarnBox>
      </>
    ),
  },
  {
    title: '기타유의사항',
    body: (
      <WarnBox>
        <p>
          개인(신용)정보 조회는 고객님의 개인신용평점에 영향을 주지 않으나,{' '}
          <Em>대출 계약의 체결만으로도 개인신용평점이 하락</Em> 할 수 있습니다.
        </p>
        <p>
          대출금 연체 시 연체이자 전액을 납입하기 전까지{' '}
          <Em>대출 잔액에 연체이자율이 적용됩니다.</Em>
        </p>
        <p>
          연체 정보가 등록되는 경우{' '}
          <Em>대출 및 신용카드 상품 등의 이용에 불이익이 발생</Em> 할 수 있습니다.
        </p>
        <p>
          대출약정을 위반한 경우{' '}
          <Em>대출 만기 전 기한의 이익이 상실되어 대출금을 전액 상환</Em>하셔야
          합니다.
        </p>
      </WarnBox>
    ),
  },
];

export function ImportantNoticePage() {
  const { mkpdCd } = useParams<{ mkpdCd: string }>();
  const navigate = useNavigate();
  const productCd = mkpdCd ?? '';
  // 한도조회 이후 단계 — 나가도 신청서 보존(재진입 시 이어서 진행 가능)
  const { requestExit, exitModal } = useApplyExit(productCd, { preserve: true });

  // step: 0 인트로 / 1..SLIDES.length 콘텐츠 / SLIDES.length+1 완료
  const [step, setStep] = useState(0);
  const [showSheet, setShowSheet] = useState(false);
  const lastContent = SLIDES.length;
  const doneStep = SLIDES.length + 1;

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [step]);

  const next = () => {
    if (step === lastContent) setShowSheet(true);
    else setStep((s) => s + 1);
  };

  return (
    <div className="app-shell">
      <header className="flow-head">
        <span className="flow-head__title">금융상품에 대한 중요사항 설명</span>
        <button type="button" className="flow-head__close" onClick={requestExit}>
          닫기
        </button>
      </header>

      {/* 인트로 */}
      {step === 0 && (
        <>
          <div className="flow-body">
            <h1 className="nlead">
              금융상품에 대한
              <br />
              중요사항을 확인 하실 수 있습니다.
            </h1>
            <p className="nsub">
              금융소비자보호법 제19조 제1항에서 규정하고 있는 중요사항으로 총
              6단계로 설명합니다.
            </p>
            <img className="nimg nimg--big" src={imgFinancial} alt="" />
            <p className="nnote">
              해당 화면은 대출 신청 시 1일 1회만 노출되며,
              <br />
              <strong>
                금융상품 &gt; 대출 &gt; 금융상품에 대한 중요사항 설명
              </strong>
              에서
              <br />
              언제든 다시 확인 가능합니다.
            </p>
          </div>
          <div className="flow-submit-bar">
            <button
              type="button"
              className="flow-submit"
              onClick={() => setStep(1)}
            >
              예 확인하겠습니다
            </button>
          </div>
        </>
      )}

      {/* 콘텐츠 슬라이드 */}
      {step >= 1 && step <= lastContent && (
        <>
          <div className="flow-body">
            <h1 className="ntitle">{SLIDES[step - 1].title}</h1>
            {SLIDES[step - 1].sub && (
              <p className="ntitle-sub">{SLIDES[step - 1].sub}</p>
            )}
            <div className="nbody">{SLIDES[step - 1].body}</div>
            <p className="nfootnote">
              · 해당 내용은 금융소비자보호법 제19조 제1항에서 규정하고 있는 중요한
              사항입니다.
            </p>
          </div>
          <div className="flow-2btn">
            <button
              type="button"
              className="flow-2btn__cancel"
              onClick={() => setStep((s) => s - 1)}
            >
              다시 볼게요
            </button>
            <button type="button" className="flow-2btn__ok" onClick={next}>
              정확히 이해했어요
            </button>
          </div>
        </>
      )}

      {/* 완료 */}
      {step === doneStep && (
        <>
          <div className="flow-body ncenter">
            <h1 className="nlead">
              금융상품에 대한 중요사항 설명을
              <br />
              모두 확인하셨습니다!
            </h1>
            <img className="nimg nimg--big" src={imgComplete} alt="" />
            <div className="nconsult">
              <button type="button" className="nconsult__btn">
                💬 Talk상담 <span>›</span>
              </button>
              <button type="button" className="nconsult__btn">
                📞 전화상담 <span>›</span>
              </button>
            </div>
            <div className="nendnote">
              본 내용은 금융소비자 보호를 위한 중요내용으로 최종 확인을 누르시면
              금융상품에 대해 이해한 것으로 간주되어 추후 분쟁이나, 소송에서
              불리하게 적용될 수 있습니다. 만약 추가 설명이 필요한 경우 처음으로
              가서 설명을 다시 보시거나, 전화상담(Talk상담)을 이용해 주세요.
              <br />· 상담운영시간 평일 09:00 ~ 18:00
            </div>
          </div>
          <div className="flow-2btn">
            <button
              type="button"
              className="flow-2btn__cancel"
              onClick={() => setStep(0)}
            >
              처음으로
            </button>
            <button
              type="button"
              className="flow-2btn__ok"
              onClick={() =>
                navigate(`/apply/${encodeURIComponent(productCd)}/form`)
              }
            >
              확인
            </button>
          </div>
        </>
      )}

      {/* 꼭! 확인하세요 시트 */}
      {showSheet && (
        <div className="notice-sheet" role="dialog" aria-label="꼭 확인하세요">
          <div
            className="notice-sheet__dim"
            onClick={() => setShowSheet(false)}
          />
          <div className="notice-sheet__panel">
            <div className="notice-sheet__head">
              <h3>꼭! 확인하세요</h3>
              <button
                type="button"
                className="notice-sheet__close"
                onClick={() => setShowSheet(false)}
                aria-label="닫기"
              >
                ×
              </button>
            </div>
            <p className="notice-sheet__text">
              대출 신청 시 각 상품별 약관, 상품설명서, 약정 서류 등을 다시 한번
              확인하여 나에게 맞는 조건을 꼭! 확인하세요
            </p>
            <img className="nimg" src={imgComplete} alt="" />
            <button
              type="button"
              className="flow-submit"
              onClick={() => {
                setShowSheet(false);
                setStep(doneStep);
              }}
            >
              예 확인하겠습니다
            </button>
          </div>
        </div>
      )}
      {exitModal}
    </div>
  );
}
