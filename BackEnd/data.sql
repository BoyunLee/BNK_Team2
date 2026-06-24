USE bnk3;

-- ============================================================
-- 대출 상품 3종
-- ============================================================
INSERT INTO LOAN_PRODUCT (product_name, base_rate, loan_period, status, created_at, updated_at) VALUES
('부산은행 직장인 신용대출',   4.50, '최대 5년', 'SALE', NOW(), NOW()),
('부산은행 전문직 우대대출',   3.90, '최대 7년', 'SALE', NOW(), NOW()),
('부산은행 청년 희망대출',     5.20, '최대 3년', 'SALE', NOW(), NOW());

-- ============================================================
-- 상품 설명 (PRODUCT_DESCRIPTION)
-- ============================================================

-- 상품 1: 직장인 신용대출
INSERT INTO PRODUCT_DESCRIPTION (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(1, 'LOAN_TYPE',   '신용대출',                                                         1, NOW(), NOW()),
(1, 'ELIGIBLE',    '재직기간 1년 이상 직장인 (정규직·계약직)',                          2, NOW(), NOW()),
(1, 'LOAN_LIMIT',  '최소 100만원 ~ 최대 1억원',                                        3, NOW(), NOW()),
(1, 'RATE_INFO',   '기준금리 연 4.50% (우대금리 최대 1.00%p 차감 가능)',               4, NOW(), NOW()),
(1, 'REPAYMENT',   '원리금균등상환 / 원금균등상환 / 만기일시상환 중 선택',             5, NOW(), NOW()),
(1, 'FEE',         '중도상환 수수료: 대출 후 3년 이내 중도상환 시 잔액의 1.2%',        6, NOW(), NOW()),
(1, 'CAUTION',     '대출 원리금 연체 시 신용등급 하락 및 법적 조치가 취해질 수 있습니다. 본인의 상환 능력을 충분히 고려하여 신중하게 대출을 결정하시기 바랍니다.', 7, NOW(), NOW()),
(1, 'AI_SUMMARY',  '직장인을 위한 범용 신용대출 상품으로, 재직기간 1년 이상이면 신청 가능합니다. 기준금리 4.5%에 급여이체·자동이체 등 우대금리 조건을 충족하면 최저 3.5%까지 금리 인하가 가능합니다. 최대 1억원까지 대출 가능하며 상환 방식을 자유롭게 선택할 수 있는 것이 장점입니다.', 99, NOW(), NOW());

-- 상품 2: 전문직 우대대출
INSERT INTO PRODUCT_DESCRIPTION (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(2, 'LOAN_TYPE',   '신용대출 (전문직 특화)',                                            1, NOW(), NOW()),
(2, 'ELIGIBLE',    '의사·변호사·공인회계사·약사 등 전문 자격증 소지자',                2, NOW(), NOW()),
(2, 'LOAN_LIMIT',  '최소 500만원 ~ 최대 3억원',                                        3, NOW(), NOW()),
(2, 'RATE_INFO',   '기준금리 연 3.90% (우대금리 최대 1.20%p 차감 가능)',               4, NOW(), NOW()),
(2, 'REPAYMENT',   '원리금균등상환 / 원금균등상환 / 만기일시상환 중 선택',             5, NOW(), NOW()),
(2, 'FEE',         '중도상환 수수료: 대출 후 3년 이내 중도상환 시 잔액의 0.8%',        6, NOW(), NOW()),
(2, 'CAUTION',     '전문직 자격증 및 재직 여부는 심사 과정에서 재검증됩니다. 허위 정보 제출 시 대출이 즉시 회수될 수 있습니다.', 7, NOW(), NOW()),
(2, 'AI_SUMMARY',  '의사·변호사 등 전문직 종사자를 위한 우대 신용대출 상품입니다. 일반 직장인 상품 대비 낮은 금리(3.9%)와 높은 한도(최대 3억원)를 제공하며, 병원 개업 자금, 사무소 설립 자금 등 전문직 특성에 맞는 용도로 활용하기 좋습니다.', 99, NOW(), NOW());

-- 상품 3: 청년 희망대출
INSERT INTO PRODUCT_DESCRIPTION (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(3, 'LOAN_TYPE',   '신용대출 (청년 특화)',                                              1, NOW(), NOW()),
(3, 'ELIGIBLE',    '만 19세 ~ 34세 이하 청년 (취업자·창업자 포함)',                    2, NOW(), NOW()),
(3, 'LOAN_LIMIT',  '최소 50만원 ~ 최대 3,000만원',                                     3, NOW(), NOW()),
(3, 'RATE_INFO',   '기준금리 연 5.20% (우대금리 최대 1.50%p 차감 가능)',               4, NOW(), NOW()),
(3, 'REPAYMENT',   '원리금균등상환 / 만기일시상환 중 선택',                            5, NOW(), NOW()),
(3, 'FEE',         '중도상환 수수료 면제 (청년 우대 혜택)',                             6, NOW(), NOW()),
(3, 'CAUTION',     '나이 요건(만 19~34세)은 대출 실행일 기준으로 판단합니다. 연령 초과 시 대출이 거절될 수 있습니다.', 7, NOW(), NOW()),
(3, 'AI_SUMMARY',  '사회초년생 및 청년층을 위한 소액 신용대출 상품입니다. 중도상환 수수료가 없어 여유 자금 생기면 언제든 상환 가능하고, 최대 1.5%p 우대금리로 실제 부담 금리를 낮출 수 있습니다. 취업 준비, 전세자금, 생활비 등 청년 생애 첫 대출로 적합합니다.', 99, NOW(), NOW());

-- ============================================================
-- 우대금리 항목 (PRODUCT_PREFERENTIAL_RATE)
-- ============================================================

-- 상품 1: 직장인 신용대출 우대금리
INSERT INTO PRODUCT_PREFERENTIAL_RATE (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(1, 'SALARY_TRANSFER',  '급여이체',         0.30, '부산은행 계좌로 급여 이체 실적 3개월 이상',   NOW(), NOW()),
(1, 'AUTO_TRANSFER',    '자동이체',         0.20, '부산은행 계좌 자동이체 2건 이상 등록',         NOW(), NOW()),
(1, 'CREDIT_CARD',      '카드 사용',        0.20, '부산은행 신용카드 월 30만원 이상 사용',         NOW(), NOW()),
(1, 'NEW_CUSTOMER',     '신규 고객',        0.20, '부산은행 최초 대출 고객',                       NOW(), NOW()),
(1, 'DIGITAL_BANKING',  '디지털뱅킹 가입',  0.10, '부산은행 모바일뱅킹 앱 가입 및 로그인 이력',   NOW(), NOW());

-- 상품 2: 전문직 우대대출 우대금리
INSERT INTO PRODUCT_PREFERENTIAL_RATE (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(2, 'SALARY_TRANSFER',  '급여이체',         0.30, '부산은행 계좌로 급여 이체 실적 3개월 이상',   NOW(), NOW()),
(2, 'LONG_TERM',        '장기 거래',        0.40, '부산은행 거래 기간 5년 이상',                  NOW(), NOW()),
(2, 'AUTO_TRANSFER',    '자동이체',         0.20, '부산은행 계좌 자동이체 2건 이상 등록',         NOW(), NOW()),
(2, 'NEW_CUSTOMER',     '신규 고객',        0.30, '부산은행 최초 대출 고객',                      NOW(), NOW());

-- 상품 3: 청년 희망대출 우대금리
INSERT INTO PRODUCT_PREFERENTIAL_RATE (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(3, 'SALARY_TRANSFER',  '급여이체',         0.30, '부산은행 계좌로 급여 이체 실적 1개월 이상',   NOW(), NOW()),
(3, 'AUTO_TRANSFER',    '자동이체',         0.20, '부산은행 계좌 자동이체 1건 이상 등록',         NOW(), NOW()),
(3, 'NEW_CUSTOMER',     '신규 고객',        0.50, '부산은행 최초 대출 고객 (청년 특별 우대)',      NOW(), NOW()),
(3, 'DIGITAL_BANKING',  '디지털뱅킹 가입',  0.30, '부산은행 모바일뱅킹 앱 가입 및 로그인 이력',  NOW(), NOW()),
(3, 'YOUTH_SPECIAL',    '청년 특별 우대',   0.20, '만 19~29세 청년 추가 우대',                    NOW(), NOW());

-- ============================================================
-- 약관 기본 (PRODUCT_TERMS_BASE) — 상품별 6종
-- ============================================================

-- 상품 1
INSERT INTO PRODUCT_TERMS_BASE (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(1, 'ADMIN_INFO_REQUEST',    '/terms/p1/admin_info.pdf',       'Y', NOW(), NOW()),
(1, 'PERSONAL_INFO_CONSENT', '/terms/p1/personal_info.pdf',   'Y', NOW(), NOW()),
(1, 'MOBILE_AUTH_TERMS',     '/terms/p1/mobile_auth.pdf',     'Y', NOW(), NOW()),
(1, 'PRODUCT_TERMS',         '/terms/p1/product_terms.pdf',   'Y', NOW(), NOW()),
(1, 'PRODUCT_DESCRIPTION',   '/terms/p1/product_desc.pdf',    'Y', NOW(), NOW()),
(1, 'BOND_CONTRACT',         '/terms/p1/bond_contract.pdf',   'Y', NOW(), NOW());

-- 상품 2
INSERT INTO PRODUCT_TERMS_BASE (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(2, 'ADMIN_INFO_REQUEST',    '/terms/p2/admin_info.pdf',       'Y', NOW(), NOW()),
(2, 'PERSONAL_INFO_CONSENT', '/terms/p2/personal_info.pdf',   'Y', NOW(), NOW()),
(2, 'MOBILE_AUTH_TERMS',     '/terms/p2/mobile_auth.pdf',     'Y', NOW(), NOW()),
(2, 'PRODUCT_TERMS',         '/terms/p2/product_terms.pdf',   'Y', NOW(), NOW()),
(2, 'PRODUCT_DESCRIPTION',   '/terms/p2/product_desc.pdf',    'Y', NOW(), NOW()),
(2, 'BOND_CONTRACT',         '/terms/p2/bond_contract.pdf',   'Y', NOW(), NOW());

-- 상품 3
INSERT INTO PRODUCT_TERMS_BASE (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(3, 'ADMIN_INFO_REQUEST',    '/terms/p3/admin_info.pdf',       'Y', NOW(), NOW()),
(3, 'PERSONAL_INFO_CONSENT', '/terms/p3/personal_info.pdf',   'Y', NOW(), NOW()),
(3, 'MOBILE_AUTH_TERMS',     '/terms/p3/mobile_auth.pdf',     'Y', NOW(), NOW()),
(3, 'PRODUCT_TERMS',         '/terms/p3/product_terms.pdf',   'Y', NOW(), NOW()),
(3, 'PRODUCT_DESCRIPTION',   '/terms/p3/product_desc.pdf',    'Y', NOW(), NOW()),
(3, 'BOND_CONTRACT',         '/terms/p3/bond_contract.pdf',   'Y', NOW(), NOW());

-- ============================================================
-- 약관 이력 (PRODUCT_TERMS_HISTORY) — 각 base 당 seq=1
-- ============================================================
INSERT INTO PRODUCT_TERMS_HISTORY (terms_id, terms_seq, terms_path, created_at)
SELECT terms_id, 1, terms_path, NOW() FROM PRODUCT_TERMS_BASE;
