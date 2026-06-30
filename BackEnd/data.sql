-- ============================================================
-- 부산은행 대출상품 시드 (FE public/data 19종 자동 변환)
-- 생성: scripts/gen-be-seed.mjs — 직접 수정하지 말 것
-- ============================================================
USE bnk3;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_terms_history;
TRUNCATE TABLE product_terms_base;
TRUNCATE TABLE product_preferential_rate;
TRUNCATE TABLE product_description;
TRUNCATE TABLE loan_product;
SET FOREIGN_KEY_CHECKS = 1;

-- [1] 모바일 전월세보증금 대출 (담보대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(1, '모바일 전월세보증금 대출', 4.9, '6개월 ~ 36개월', 'SALE', '담보대출', '0300000140', '전월세보증금 대출도 모바일에서 간편하게', '4.3', '4.9', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(1, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(1, 'LOAN_LIMIT', '222백만원', 5, NOW(), NOW()),
(1, 'LOAN_TERM', '6개월 ~ 36개월', 6, NOW(), NOW()),
(1, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(1, 'OPT_RATE_TYPE', '신규취급액기준 COFIX', 9, NOW(), NOW()),
(1, 'OPT_RATE_CYCLES', '6', 10, NOW(), NOW()),
(1, 'OPT_TERMS', '6개월,1년,2년,3년', 11, NOW(), NOW()),
(1, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":12,"maxM":36,"minIncl":true}]', 12, NOW(), NOW()),
(1, 'INFO:상품개요', '<p class="ssp-editor-p">서민의&nbsp;주거비용&nbsp;부담&nbsp;완화를&nbsp;위한&nbsp;비대면&nbsp;전월세보증금&nbsp;대출</p>', 100, NOW(), NOW()),
(1, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>아래&nbsp;요건을&nbsp;모두&nbsp;충족하는&nbsp;고객<ul class="info-list type-li-dash-ed"><li>부동산&nbsp;중개업소를&nbsp;통해&nbsp;개인인&nbsp;임대인과&nbsp;전세계약을&nbsp;체결하고&nbsp;임차보증금의&nbsp;5%&nbsp;이상을&nbsp;지급한&nbsp;세대주<br>※&nbsp;세대주&nbsp;인정&nbsp;범위<br>&nbsp; 가. 배우자<br>&nbsp; 나. 직계존비속&nbsp;및&nbsp;그의&nbsp;배우자<br>&nbsp; 다. 신청인&nbsp;및&nbsp;배우자의&nbsp;형제자매<br>&nbsp; 라. 배우자의&nbsp;직계존비속&nbsp;및&nbsp;그의&nbsp;배우자</li><li><span>근로소득자(현 직장 1년 이상 재직) 또는 사업소득자로&nbsp;주택금융공사&nbsp;CSS&nbsp;1~6등급&nbsp;이내인&nbsp;자</span></li><li>본인과&nbsp;배우자&nbsp;현재&nbsp;무주택인&nbsp;자</li></ul></li><li><span>임차주택은 아래의 요건을 모두 충족하여야 함</span><ul class="info-list type-li-dash-ed"><li>임차보증금:&nbsp;수도권&nbsp;7억원 이하,&nbsp;지방&nbsp;5억원 이하</li><li>임차목적물: 아래의 어느 하나에 해당하는 경우<ul class="info-list type-li-gt"><li>KB 부동산시세가 있는 아파트</li><li>KB 부동산 시세가 있는 주거용 오피스텔</li></ul></li><li><span>공부상 건물 소유권에 권리침해가 없을 것</span></li></ul></li></ul>', 101, NOW(), NOW()),
(1, 'INFO:대출한도', '<p class="ssp-editor-p">한국주택금융공사 보증한도 범위내에서 임차보증금의 80%이내(특례보증의 경우 100% 이내)로서 최고 222백만원 이내</p>', 102, NOW(), NOW()),
(1, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>일시상환방식 : 12개월 이상 ~ 36개월 이내로서 당해 임대차 계약 만료일<br><span>※ 대출이동시스템으로 접수된 경우 6개월 이상 ~ 36개월 이내</span></li><li>기한연장의 경우 1회 2년 이내로 연장하되 최초 대출일 포함 10년까지 연장 가능</li><li>이자부과시기 : 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(1, 'INFO:금리변동주기', '<p class="ssp-editor-p">6개월</p>', 104, NOW(), NOW()),
(1, 'INFO:기준금리', '신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(1, 'INFO:기본금리', '<p class="ssp-editor-p">신규취급액기준&nbsp;COFIX&nbsp;+ 2.00%</p>', 106, NOW(), NOW()),
(1, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>기한연기 기간별 가산금리(※대출 만기 연장시 적용)<ul class="info-list type-li-dash-ed"><li><span>6개월 미만 1.50%</span></li><li><span>6개월 이상 1년 미만 1.00%</span></li><li><span>1년 이상 1년 6개월 미만 0.50%</span></li></ul></li></ul>', 107, NOW(), NOW()),
(1, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>상품별대출&nbsp;거래실적연동&nbsp;옵션&nbsp;감면금리&nbsp;(최대&nbsp;0.50%)<br>*&nbsp;급여 및&nbsp;가맹점 결제대금 자동이체&nbsp;0.50%</li></ul><ul class="info-list type-li-dot"><li>신용평점별&nbsp;우대금리 (최대 0.10%)<br>*&nbsp;신용평점&nbsp;NICE 875점 이상,&nbsp;KCB 905점 이상 모두 충족하는 경우&nbsp;0.10%</li></ul>', 108, NOW(), NOW()),
(1, 'INFO:최종금리', '최저 연 3.92% ~ 최고 연 4.52% ( 2025.11.13 신규취급액기준(COFIX) : 2.52%)
(최저금리는 최대 우대금리 0.60% 모두 적용하는 경우)', 109, NOW(), NOW()),
(1, 'INFO:담보조건', '<p class="ssp-editor-p">한국주택금융공사 보증서</p>', 110, NOW(), NOW()),
(1, 'INFO:가입방법', '<p class="ssp-editor-p">모바일뱅킹</p>', 111, NOW(), NOW()),
(1, 'INFO:수수료(부대비용)', '<p class="info-list type-li-dot ssp-editor-p"><span style="color: rgb(255, 0, 0)">※ </span>수입인지대금 : 5천 만원 초과시 대출금액별 수입인지비용 차등 부과(50% 균등부담)</p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" scope="" ><b>대출금액</b></th><th colspan="2" scope="" ><b>인지세액</b></th></tr><tr><th scope="" ><b>고객</b></th><th scope="" ><b>은행</b></th></tr></thead><tbody><tr><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">5천만원 초과 1억원 이하</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">3만5천원</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">3만5천원</p></td></tr><tr><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">1억원 초과 10억원 이하</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">7만5천원</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">7만5천원</p></td></tr><tr><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">10억원 초과</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">17만5천원</p></td><td scope="" ><p class="info-list type-li-dot ssp-editor-p" style="text-align: center">17만5천원</p></td></tr></tbody></table></div><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p"><b style="color: rgb(255, 0, 0)">※&nbsp;</b>보증료(고객부담)</p><p class="ssp-editor-p">&nbsp; - 한국주택금융공사 보증료 0.02% ~ 0.20%</p><p class="ssp-editor-p"><span style="font-size: 100%">&nbsp; - 보증료는 보증서를 발급하여 전세대출을 실행함에 따라 징수하는 금액으로 대상금액에 요율과 기간을 곱한 후 이를 365(윤년 366)으로 나누어&nbsp;산정</span></p><p class="ssp-editor-p"><span style="font-size: 100%">&nbsp; - 한국주택금융공사의 ''보증료 등의 운용규정''에 따라 결정되며, 보증진행상태에 따라 최종 보증료율은 변동될 수 있음</span></p><p class="ssp-editor-p"><span style="font-size: 100%">&nbsp; - 한국주택금융공사 보증료는 대출실행 시 통장에서 자동출금되며, 대출실행 시 사전에 지정한 이자납입계좌에 비용을 납입하기 위한 잔액이 없으면 대출실행이 불가 할 수 있음</span></p>', 112, NOW(), NOW()),
(1, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 113, NOW(), NOW()),
(1, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li><span>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</span></li><li><span>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</span></li><li><span>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br></span><span>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</span></li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li><span>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</span></li><li><span>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</span></li><li><span>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</span></li><li><span>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</span></li><li><span>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</span></li><li><span>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</span></li><li><span>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.&nbsp;</span></li></ul>', 114, NOW(), NOW()),
(1, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>확정일자부 임대차 계약서(사진첨부 제출필요)</li><li>계약금 지급영수증(사진첨부 제출필요)</li><li>국민건강보험공단 자격득실확인서 및 보험료 납부확인서</li><li>혼인관계증명서(기혼인 경우)</li><li>소득금액증명원</li><li>재직확인서류(필요시)</li><li>주민등록등본, 가족관계증명서</li><li>사업소득자의 경우 사업자등록증 또는 사업자등록증명원</li></ul><p class="ssp-editor-p">※ 대출 진행 과정에서 추가 서류가 필요할 수 있습니다.</p>', 115, NOW(), NOW()),
(1, 'INFO:마이너스통장(종합통장대출)가능여부', '<p class="ssp-editor-p">부</p>', 116, NOW(), NOW()),
(1, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(1, 'RATE:기본금리', '<p class="ssp-editor-p">신규취급액기준&nbsp;COFIX&nbsp;+ 2.00%</p>', 301, NOW(), NOW()),
(1, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>기한연기 기간별 가산금리(※대출 만기 연장시 적용)<ul class="info-list type-li-dash-ed"><li><span>6개월 미만 1.50%</span></li><li><span>6개월 이상 1년 미만 1.00%</span></li><li><span>1년 이상 1년 6개월 미만 0.50%</span></li></ul></li></ul>', 302, NOW(), NOW()),
(1, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>상품별대출&nbsp;거래실적연동&nbsp;옵션&nbsp;감면금리&nbsp;(최대&nbsp;0.50%)<br>*&nbsp;급여 및&nbsp;가맹점 결제대금 자동이체&nbsp;0.50%</li></ul><ul class="info-list type-li-dot"><li>신용평점별&nbsp;우대금리 (최대 0.10%)<br>*&nbsp;신용평점&nbsp;NICE 875점 이상,&nbsp;KCB 905점 이상 모두 충족하는 경우&nbsp;0.10%</li></ul>', 303, NOW(), NOW()),
(1, 'RATE:최종금리', '최저 연 3.92% ~ 최고 연 4.52% ( 2025.11.13 신규취급액기준(COFIX) : 2.52%)<br>(최저금리는 최대 우대금리 0.60% 모두 적용하는 경우)', 304, NOW(), NOW()),
(1, 'DOC:여신거래 기본약관', '/terms/p1/0.pdf', 500, NOW(), NOW()),
(1, 'DOC:전세자금대출 상품설명서', '/terms/p1/1.pdf', 501, NOW(), NOW()),
(1, 'DOC:상품공시자료', '/terms/p1/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(1, 'PREF_1_1', '급여·가맹점 자동이체', 0.5, '급여 및 가맹점 결제대금 자동이체 시', NOW(), NOW());

-- [2] BNK신탁자산(주택청약저축)담보대출 (담보대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(2, 'BNK신탁자산(주택청약저축)담보대출', 5.16, '~ 5년(상환방식에 따라 다름)', 'SALE', '담보대출', '0300000822', '급하게 여유자금이 필요할 때 신탁자산 해지 없이', '4.16', '5.16', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(2, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(2, 'LOAN_LIMIT', '5천만원(상품별 담보비율의 90~100%까지)', 5, NOW(), NOW()),
(2, 'LOAN_TERM', '~ 5년(상환방식에 따라 다름)', 6, NOW(), NOW()),
(2, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(2, 'OPT_RATE_TYPE', '신규취급액기준 COFIX', 9, NOW(), NOW()),
(2, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(2, 'OPT_TERMS', '5년', 11, NOW(), NOW()),
(2, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":60,"minIncl":true},{"method":"원리금균등상환","minM":12,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(2, 'INFO:상품개요', '<ul class="info-list type-li-dot"><li>본인 명의의 부산은행 특정금전신탁(기준가신탁포함), 주택청약저축을 담보로 한 대출</li><li>신탁 상품별로 대출한도를 차별화한 저금리 가계, 기업 공통대출</li><li>대면 채널(창구)과 비대면 채널(인터넷뱅킹, 모바일뱅킹) 모두 취급 가능</li></ul>', 100, NOW(), NOW()),
(2, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">만 19세 이상 본인 명의의 부산은행 특정금전신탁 및 연금신탁(신 개인연금신탁 포함) 또는 주택청약저축을 담보로 제공하는 개인 또는 기업고객</p><p class="ssp-editor-p">단, 비대면 채널은 개인 인터넷뱅킹 가입 고객에 한함</p>', 101, NOW(), NOW()),
(2, 'INFO:대출한도', '<p class="ssp-editor-p">신탁 기준가격<sup>주)</sup>에서 회수인정비율(특정금전신탁 100%, 연금신탁 90%, 신 개인연금신탁 90%, 주택청약저축 95%) 범위 내 금액<br>(단, 최고 대출한도는 건당 50백만원 이내)<br>주) 신탁상품별 담보비율 적용</p>', 102, NOW(), NOW()),
(2, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>일시상환:&nbsp; 1년 이내 (최장 5년간 기한연기 가능)</li><li>원금균등 또는 원리금균등 분할상환: 5년 이내(거치기간 없음)</li></ul><span style="font-size: 100%">* 단, 대출 최장기간은 신탁 만기일 이내로 하며 비대면 채널 취급은 일시상환(종합통장대출 미포함)방식만 가능함</span><br><ul class="info-list type-li-dot"><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(2, 'INFO:금리변동주기', '<p class="ssp-editor-p">3개월, 6개월</p>', 104, NOW(), NOW()),
(2, 'INFO:기준금리', '신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(2, 'INFO:기본금리', '<p>기준금리(신규취급액기준 (COFIX) ) + 1.76%</p>', 106, NOW(), NOW()),
(2, 'INFO:가산금리', '<p class="ssp-editor-p">종합통장대출 0.50%</p>', 107, NOW(), NOW()),
(2, 'INFO:우대금리', '<p class="ssp-editor-p">인터넷 대출 0.50%</p>', 108, NOW(), NOW()),
(2, 'INFO:최종금리', '최저 연 4.07% ~ 최고 연 5.07%(2025.12.17현재 신규COFIX : 2.81%)
(최저금리는 가산금리 미적용 및 우대금리 0.50%를 모두 적용하는 경우, 최고금리는 우대금리 미적용 및 가산금리 적용한 금리)
', 109, NOW(), NOW()),
(2, 'INFO:담보조건', '<p class="ssp-editor-p">부산은행 특정금전신탁 / 기준가 신탁(연금신탁, 신개인연금신탁) / 주택청약저축</p>', 110, NOW(), NOW()),
(2, 'INFO:가입방법', '<ul class="info-list type-li-dot"><li>대면 채널: 영업점</li><li>비대면 채널: 인터넷 혹은 모바일 앱 신청</li></ul>', 111, NOW(), NOW()),
(2, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">- 「인지세법」에 따라 대출약정시 납부하는 세금으로 대출금액에따라 세액이 차등적용되며, 은행과 고객이 각각 50% 부담</p><div class="table-box"><table class="tbl-matrix" style="height: 140px"><tbody><tr><td ><p class="ssp-editor-p">대출금액</p></td><td ><p class="ssp-editor-p">5천만원 이하</p></td><td ><p class="ssp-editor-p">5천만원 초과</p><p class="ssp-editor-p">~ 1억원 이하</p></td><td ><p class="ssp-editor-p">1억원 초과</p><p class="ssp-editor-p">~ 10억원 이하</p></td><td ><p class="ssp-editor-p">10억원 초과</p></td></tr><tr><td ><p class="ssp-editor-p">인지세액</p></td><td ><p class="ssp-editor-p">비과세</p></td><td ><p class="ssp-editor-p">7만원</p><p class="ssp-editor-p">(각각 3만5천원)</p></td><td ><p class="ssp-editor-p">15만원</p><p class="ssp-editor-p">(각각 7만5천원)</p></td><td ><p class="ssp-editor-p">35만원</p><p class="ssp-editor-p">(각각 17만5천원)</p></td></tr></tbody></table></div>', 112, NOW(), NOW()),
(2, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 113, NOW(), NOW()),
(2, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(2, 'INFO:필요서류', '<p>없음</p>', 115, NOW(), NOW()),
(2, 'INFO:마이너스통장(종합통장대출)가능여부', '<p class="ssp-editor-p">비대면 채널인 경우 불가능</p>', 116, NOW(), NOW()),
(2, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(2, 'RATE:기본금리', '<p>기준금리(신규취급액기준 (COFIX) ) + 1.76%</p>', 301, NOW(), NOW()),
(2, 'RATE:가산금리', '<p class="ssp-editor-p">종합통장대출 0.50%</p>', 302, NOW(), NOW()),
(2, 'RATE:우대금리', '<p class="ssp-editor-p">인터넷 대출 0.50%</p>', 303, NOW(), NOW()),
(2, 'RATE:최종금리', '최저 연 4.07% ~ 최고 연 5.07%(2025.12.17현재 신규COFIX : 2.81%)<br>(최저금리는 가산금리 미적용 및 우대금리 0.50%를 모두 적용하는 경우, 최고금리는 우대금리 미적용 및 가산금리 적용한 금리)', 304, NOW(), NOW()),
(2, 'DOC:여신거래 기본약관', '/terms/p2/0.pdf', 500, NOW(), NOW()),
(2, 'DOC:가계대출 상품설명서', '/terms/p2/1.pdf', 501, NOW(), NOW()),
(2, 'DOC:상품공시자료', '/terms/p2/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(2, 'PREF_2_1', '비대면(인터넷) 신청', 0.5, '인터넷·모바일 비대면 신청 시', NOW(), NOW());

-- [3] BNK인터넷예적금담보대출 (담보대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(3, 'BNK인터넷예적금담보대출', 5.00, '담보 상품 만기일까지', 'SALE', '담보대출', '0300000382', '긴급자금이 필요할 때 적립식/거치식 예금 해지없이', '수신금리+1.30', NULL, NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(3, 'LOAN_LIMIT', '1억원(최저 10만원, 계좌 잔액 95%까지)', 5, NOW(), NOW()),
(3, 'LOAN_TERM', '담보 상품 만기일까지', 6, NOW(), NOW()),
(3, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(3, 'OPT_RATE_TYPE', '고정금리', 9, NOW(), NOW()),
(3, 'OPT_TERMS', '6개월,1년,2년,3년,5년', 11, NOW(), NOW()),
(3, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(3, 'INFO:상품개요', '<p class="ssp-editor-p">본인 명의의 예금, 적금, 장부가 방식신탁을 담보로 영업점 방문 없이 인터넷상에서 필요할 때 즉시 대출을 받을 수 있는 인터넷 전용상품</p>', 100, NOW(), NOW()),
(3, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">본인 명의의 부산은행 정기예금, 부금, 적금, 신탁(장부가 방식)상품을 담보로 제공하고 대출을 받고자 하는 고객</p>', 101, NOW(), NOW()),
(3, 'INFO:대출한도', '예금(적금)불입액의 95%(단, 지수연동정기예금인경우 원금의 90%)이내로 대출 건별 최고 1억원이내
장부가방식 개인연금신탁 담보인 경우 중도해지금액의 90%이내로 대출 건별 최고 5천만원 이내
※ 대출신청금액은 최저 10만원, 최고 1억원 이내이며, 10만원 단위로 신청가능', 102, NOW(), NOW()),
(3, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>일시상환 : 담보 예&#x2022;적금 / 장부가 방식 신탁의 만기일</li><li>이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(3, 'INFO:금리변동주기', '<p class="ssp-editor-p">해당사항없음</p>', 104, NOW(), NOW()),
(3, 'INFO:기본금리', '<ul class="info-list type-li-dot"><li>예·적금:&nbsp; 수신금리 + 1.30%</li><li>지수연동정기예금 : 모집기간이율 + 1.30%</li></ul><ul class="info-list type-li-dot"><li>장부가 방식신탁:&nbsp; 수탁이율 + 1.00%</li></ul>', 105, NOW(), NOW()),
(3, 'INFO:최종금리', '예.적금: 수신금리 + 1.30%
지수연동정기예금 : 모집기간이율 + 1.30%
장부가 방식신탁: 수탁이율 + 1.00%', 106, NOW(), NOW()),
(3, 'INFO:담보조건', '<p class="ssp-editor-p">부산은행 적립식 또는 거치식 예금, 장부가 방식 신탁</p>', 107, NOW(), NOW()),
(3, 'INFO:가입방법', '<p class="ssp-editor-p">인터넷 신청</p>', 108, NOW(), NOW()),
(3, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">□ 인지세</p><p class="ssp-editor-p">- 「인지세법」에 따라 대출약정시 납부하는 세금으로 대출금액에 따라 세액이 차등적용되며, 은행과 고객이 각각 50% 부담</p><div class="table-box"><table class="tbl-matrix" ><tbody><tr><td ><p class="ssp-editor-p">대출금액</p></td><td ><p class="ssp-editor-p">5천만원 이하</p></td><td ><p class="ssp-editor-p">5천만원 초과</p><p class="ssp-editor-p">~ 1억원 이하</p></td><td ><p class="ssp-editor-p">1억원 초과</p><p class="ssp-editor-p">~ 10억원 이하</p></td><td ><p class="ssp-editor-p">10억원 초과</p></td></tr><tr><td ><p class="ssp-editor-p">인지세액</p></td><td ><p class="ssp-editor-p">비과세</p></td><td ><p class="ssp-editor-p">7만원</p><p class="ssp-editor-p">(각각 3만5천원)</p></td><td ><p class="ssp-editor-p">15만원</p><p class="ssp-editor-p">(각각 7만5천원)</p></td><td ><p class="ssp-editor-p">35만원</p><p class="ssp-editor-p">(각각 17만5천원)</p></td></tr></tbody></table></div>', 109, NOW(), NOW()),
(3, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 110, NOW(), NOW()),
(3, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출장액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생이로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 111, NOW(), NOW()),
(3, 'INFO:필요서류', '<p class="ssp-editor-p">없음</p>', 112, NOW(), NOW()),
(3, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>불가능</p>', 113, NOW(), NOW()),
(3, 'RATE:기본금리', '<ul class="info-list type-li-dot"><li>예·적금:&nbsp; 수신금리 + 1.30%</li><li>지수연동정기예금 : 모집기간이율 + 1.30%</li></ul><ul class="info-list type-li-dot"><li>장부가 방식신탁:&nbsp; 수탁이율 + 1.00%</li></ul>', 300, NOW(), NOW()),
(3, 'RATE:최종금리', '예.적금: 수신금리 + 1.30%<br>지수연동정기예금 : 모집기간이율 + 1.30%<br>장부가 방식신탁: 수탁이율 + 1.00%', 301, NOW(), NOW()),
(3, 'DOC:여신거래 기본약관', '/terms/p3/0.pdf', 500, NOW(), NOW()),
(3, 'DOC:가계대출 상품설명서', '/terms/p3/1.pdf', 501, NOW(), NOW()),
(3, 'DOC:상품공시자료', '/terms/p3/2.pdf', 502, NOW(), NOW());

-- [4] BNK인터넷펀드담보대출 (담보대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(4, 'BNK인터넷펀드담보대출', 6.5, '~ 5년(상환방식에 따라 다름)', 'SALE', '담보대출', '0300000831', '급하게 여유자금이 필요할 때 펀드 해지 없이', '5.8', '6.5', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(4, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(4, 'LOAN_LIMIT', '5천만원(최저 10만원, 대출 당일 평가액의 50~90%까지)', 5, NOW(), NOW()),
(4, 'LOAN_TERM', '~ 5년(상환방식에 따라 다름)', 6, NOW(), NOW()),
(4, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(4, 'OPT_RATE_TYPE', '신규취급액기준 COFIX', 9, NOW(), NOW()),
(4, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(4, 'OPT_TERMS', '5년', 11, NOW(), NOW()),
(4, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":60,"minIncl":true},{"method":"원리금균등상환","minM":12,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(4, 'INFO:상품개요', '<p>본인명의의 수익증권(펀드)를 담보로 영업점 방문 없이 인터넷상에서 필요할 때 즉시 대출을 받을 수 있는 인터넷 전용상품<br></p>', 100, NOW(), NOW()),
(4, 'INFO:대출조건(자격)', '<p>부산은행에서 본인명의의 수익증권(펀드)상품을 담보로 제공하고 대출을 받고자 하는 고객<br>(단, 미성년자, 외국인 및 법인은 제외)<br></p>', 101, NOW(), NOW()),
(4, 'INFO:대출한도', '<ul class="info-list type-li-dot"><li>수익증권(펀드)의 대출 당일 평가금액의 최대 90%까지 가능합니다.</li><li>대출신청금액은 최저 10만원, 최고 5천만 원까지 이며, 10만원 단위로 신청 가능합니다.</li></ul>', 102, NOW(), NOW()),
(4, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>일시상환:&nbsp; 1년 이내 (마이너스통장대출 포함) </li><li>원금균등 또는원리금균등 분할상환:&nbsp; 5년 이내</li><li>이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능<br></p>', 103, NOW(), NOW()),
(4, 'INFO:금리변동주기', '<p>3개월, 6개월<br></p>', 104, NOW(), NOW()),
(4, 'INFO:기준금리', '신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(4, 'INFO:기본금리', '<p>신규취급액기준 COFIX + 3.10%</p>', 106, NOW(), NOW()),
(4, 'INFO:가산금리', '<p>종합통장대출 0.50%<br></p>', 107, NOW(), NOW()),
(4, 'INFO:우대금리', '<p>인터넷대출 0.20%<br></p>', 108, NOW(), NOW()),
(4, 'INFO:최종금리', '최저 연 5.71% ~ 최대 연 6.41% (신규취급액기준 COFIX : 2.81%, 2025.12.17 현재)
(최저금리는 가산금리 미적용 및 우대금리 0.20%를 모두 적용하는 경우, 최고금리는 우대금리 미적용 및 가산금리 적용한 금리)
', 109, NOW(), NOW()),
(4, 'INFO:담보조건', '<p>부산은행 수익증권(펀드)<br></p>', 110, NOW(), NOW()),
(4, 'INFO:가입방법', '<p>인터넷 신청<br></p>', 111, NOW(), NOW()),
(4, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">- 「인지세법」에 따라 대출약정시 납부하는 세금으로 대출금액에따라 세액이 차등적용되며, 은행과 고객이 각각 50% 부담</p><div class="table-box"><table class="tbl-matrix" ><tbody><tr><td ><p class="ssp-editor-p">대출금액</p></td><td ><p class="ssp-editor-p">5천만원 이하</p></td><td ><p class="ssp-editor-p">5천만원 초과</p><p class="ssp-editor-p">~ 1억원 이하</p></td><td ><p class="ssp-editor-p">1억원 초과</p><p class="ssp-editor-p">~ 10억원 이하</p></td><td ><p class="ssp-editor-p">10억원 초과</p></td></tr><tr><td ><p class="ssp-editor-p">인지세액</p></td><td ><p class="ssp-editor-p">비과세</p></td><td ><p class="ssp-editor-p">7만원</p><p class="ssp-editor-p">(각각 3만5천원)</p></td><td ><p class="ssp-editor-p">15만원</p><p class="ssp-editor-p">(각각 7만5천원)</p></td><td ><p class="ssp-editor-p">35만원</p><p class="ssp-editor-p">(각각 17만5천원)</p></td></tr></tbody></table></div>', 112, NOW(), NOW()),
(4, 'INFO:중도상환수수료', '<p>면제</p>', 113, NOW(), NOW()),
(4, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다.&nbsp;단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지&nbsp;아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(4, 'INFO:필요서류', '<p>없음</p>', 115, NOW(), NOW()),
(4, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>가능</p>', 116, NOW(), NOW()),
(4, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(4, 'RATE:기본금리', '<p>신규취급액기준 COFIX + 3.10%</p>', 301, NOW(), NOW()),
(4, 'RATE:가산금리', '<p>종합통장대출 0.50%<br></p>', 302, NOW(), NOW()),
(4, 'RATE:우대금리', '<p>인터넷대출 0.20%<br></p>', 303, NOW(), NOW()),
(4, 'RATE:최종금리', '최저 연 5.71% ~ 최대 연 6.41% (신규취급액기준 COFIX : 2.81%, 2025.12.17 현재)<br>(최저금리는 가산금리 미적용 및 우대금리 0.20%를 모두 적용하는 경우, 최고금리는 우대금리 미적용 및 가산금리 적용한 금리)', 304, NOW(), NOW()),
(4, 'DOC:여신거래 기본약관', '/terms/p4/0.pdf', 500, NOW(), NOW()),
(4, 'DOC:가계대출 상품설명서', '/terms/p4/1.pdf', 501, NOW(), NOW()),
(4, 'DOC:상품공시자료', '/terms/p4/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(4, 'PREF_4_1', '비대면(인터넷) 신청', 0.2, '인터넷·모바일 비대면 신청 시', NOW(), NOW());

-- [5] ONE주택담보대출 (담보대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(5, 'ONE주택담보대출', 5.78, '10년 ~ 40년', 'SALE', '담보대출', '0300010400', '내가 주도하는 금융 생활 고정금리기간 자율적으로 선택 가능한 주택담보대출', '3.11', '5.78', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(5, 'BASE_RATE_RAW', '4.15', 4, NOW(), NOW()),
(5, 'LOAN_LIMIT', '담보인정비율 80%', 5, NOW(), NOW()),
(5, 'LOAN_TERM', '10년 ~ 40년', 6, NOW(), NOW()),
(5, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(5, 'OPT_RATE_TYPE', '금융채', 9, NOW(), NOW()),
(5, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(5, 'OPT_TERMS', '10년,15년,20년,30년,40년', 11, NOW(), NOW()),
(5, 'OPT_REPAYMENTS', '[{"method":"원금균등상환","minM":120,"maxM":480,"minIncl":true},{"method":"원리금균등상환","minM":120,"maxM":480,"minIncl":true}]', 12, NOW(), NOW()),
(5, 'INFO:상품개요', '(대면) 일정기간(3년 ~ 10년) 고정금리 적용 후 남은 기간은 변동금리로 전환되어 중·단기 금리상승에 대한 위험을 회피할 수 있는
         금리 혼합형 (고정→변동) 주택담보대출 상품
(비대면) 상품대출 옵션(거래실적연동)이 없는 단순한 금리 체계를 적용한 금리 혼합형 (고정→변동) 주택담보대출 상품
', 100, NOW(), NOW()),
(5, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">(대면)</p><p class="ssp-editor-p">1.공공마이데이터,KCB 부동산 정보 등을 활용하여 대출 심사가 가능한 자</p><p class="ssp-editor-p">2.단,상기에도 불구하고 아래에 해당하는 경우 취급 불가</p><p class="ssp-editor-p">&nbsp; &nbsp; 1) 기존주택을담보로 신규 주택을 구입하는 경우</p><p class="ssp-editor-p">&nbsp; &nbsp; 2) KB시세, 공동주택가격자문확인서이외 방법(정식감정, 공시가격등)로담보물 평가하는 경우</p><p class="ssp-editor-p">3.본인(공동명의 포함)또는 배우자 개인 명의의 주택을담보로 대출 받고자 하는 순수 개인(외국인, 외국국적동포, 미성년자제외)</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">(비대면)</p><p class="ssp-editor-p">1.본인(배우자 공동명의 포함)또는 배우자 개인 명의의 주택을 담보로 대출을 받고자 하는 순수 개인 </p><p class="ssp-editor-p">2. NICE신용평점 350점이상&nbsp;</p>', 101, NOW(), NOW()),
(5, 'INFO:대출한도', '담보물건 소재지역, 주택담보대출 보유여부, 서민실수요자 여부에 따라 담보인정비율 최대 80% 적용가능 
- 대출한도: (담보물감정금액 X 담보인정비율) - 선순위채권 - 소액보증금 + 보증서 또는 보험금액 
- 최소 대출금액: 1백만원 이상(단, 비대면 신청건인 경우 3천만원 이상)
- 다만 상기에도 불구하고 수도권 및 규제지역에 소재한 주택 구입 시 최대 6억원까지 가능 ', 102, NOW(), NOW()),
(5, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>원금균등 / 원리금균등 분할상환방식 10년 이상 ~ 40년이내</li></ul><p class="ssp-editor-p">※ 거치기간 적용 불가</p><ul class="info-list type-li-dot"><li>이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능</p><p class="ssp-editor-p">※ 다만 상기에도 불구하고 담보물이 수도권 및 규제지역에 소재시 대출기간 30년 이내</p>', 103, NOW(), NOW()),
(5, 'INFO:금리변동주기', ' 3개월, 6개월 (변동금리 적용시에만 적용)', 104, NOW(), NOW()),
(5, 'INFO:기준금리', '금융채 : KIS채권평가㈜와 한국자산평가㈜에서 고시하는 기간별 『금융채Ⅰ(은행채) 무보증AAA등급』의 직전 영업일 단순평균 시가평가기준수익률에 신규시 결정된 가산이율을 더하여 적용.
신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(5, 'INFO:기본금리', '<p class="ssp-editor-p"></p><p class="ssp-editor-p">○先고정금리적용시</p><p class="ssp-editor-p">&nbsp; &nbsp; 대면 :기간별(3년 ~10년)금융채 +1.60%</p><p class="ssp-editor-p">&nbsp; &nbsp; 비대면: 기간별(3년,5년,7년)금융채 +1.23%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시</p><p class="ssp-editor-p">&nbsp; &nbsp; &nbsp; 대면 : 기간별(3개월,6개월)금융채 +2.00%</p><p class="ssp-editor-p">&nbsp; &nbsp; &nbsp; 비대면 : 신잔액기준COFIX+ 1.91%</p><p></p>', 106, NOW(), NOW()),
(5, 'INFO:가산금리', '<p class="ssp-editor-p">○先 고정금리(3년 ~10년)적용시(대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액249백만원초과 ~ 498백만원이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액498백만원초과 0.20%</p><p class="ssp-editor-p">-외부감정이용(공동주택가격자문확인서 및 정식감정) 0.10%</p><p class="ssp-editor-p">-LTV 60%초과 또는 주택담보대출 3건이상보유0.10%</p><p class="ssp-editor-p">-5년초과 7년이내 고정금리 0.20%</p><p class="ssp-editor-p">-7년초과 10년이내 고정금리 0.40%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○後 변동금리 적용시(대면적용금리) </p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 249백만원 초과 ~498백만원 이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 498백만원 초과 0.20%</p><p class="ssp-editor-p">-외부감정이용(공동주택가격자문확인서 및 정식감정) 0.10%</p><p class="ssp-editor-p">-LTV 60%초과 또는 주택담보대출 3건이상보유0.10%</p><p class="ssp-editor-p">-5년초과 7년이내 고정금리 0.20%</p><p class="ssp-editor-p">- 7년초과10년이내고정금리 0.40%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○先 고정금리(3,5,7년)적용시(비대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액249백만원초과 ~ 498백만원이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액498백만원초과 0.20%</p><p class="ssp-editor-p">-LTV 60% 초과0.05%</p><p class="ssp-editor-p">-LTV 70% 초과 0.10%</p><p class="ssp-editor-p">-주택담보대출 보유건수3건 이상0.10%</p><p class="ssp-editor-p">- 신용평가등급별가산금리 AS 9등급또는 NICE 674점이하</p><p class="ssp-editor-p">&nbsp;  또는 KCB 624점이하 0.20%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(비대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 249백만원 초과 ~498백만원 이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 498백만원 초과 0.20%</p><p class="ssp-editor-p">-LTV 60% 초과 0.05%</p><p class="ssp-editor-p">-LTV 70% 초과 0.10%</p><p class="ssp-editor-p">-주택담보대출 보유건수3건 이상0.10%</p><p class="ssp-editor-p">- 신용평가등급별가산금리 AS 9등급또는 NICE 674점이하</p><p class="ssp-editor-p">&nbsp;  또는 KCB 624점이하 0.20%</p>', 107, NOW(), NOW()),
(5, 'INFO:우대금리', '<p class="ssp-editor-p">○先 고정금리 적용시(대면 적용금리)(최대1.30%)</p><p class="ssp-editor-p">-거래실적에 따른 감면금리(최대0.60%)</p><p class="ssp-editor-p">&nbsp;①급여(연금) 및가맹점 결제대금(요양급여포함) 자동이체(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;②예금평잔 기준(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;③매3개월간신용카드사용금액 1백만원이상0.10%</p><p class="ssp-editor-p">&nbsp;④매3개월간신용카드사용금액 2백만원이상0.20%</p><p class="ssp-editor-p">-기타감면금리(최대 0.70%)</p><p class="ssp-editor-p">&nbsp; ①부동산 전자계약(구입목적限) 0.20%</p><p class="ssp-editor-p">&nbsp; ②구입목적 외 대출 0.20%</p><p class="ssp-editor-p">&nbsp; ③ 영업점장 우대금리 최대 0.30%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(대면 적용금리)(최대0.60%)</p><p class="ssp-editor-p">&nbsp;  ①급여(연금) 및가맹점 결제대금(요양급여포함)&nbsp;<span style="font-size: 14px">자동이체</span><span style="font-size: 14px">(</span><span style="font-size: 14px">배우자미포함</span><span style="font-size: 14px">) 0.20%</span></p><p class="ssp-editor-p">&nbsp;  ②예금평잔 기준(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;  ③매3개월간신용카드사용금액 1백만원이상0.10%</p><p class="ssp-editor-p">&nbsp;  ④ 매3개월간 신용카드사용금액 2백만원이상0.20%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○先 고정금리 적용시(비대면 적용금리)(최대 0.73%)</p><p class="ssp-editor-p">-부동산 전자계약 우대금리 0.20%</p><p class="ssp-editor-p">-마케팅 활용 동의 고객 0.03%</p><p class="ssp-editor-p">-비대면 신청 고객 최대 0.50%</p><p class="ssp-editor-p">&nbsp;  &gt; 구입목적인경우 0.20% 적용</p><p class="ssp-editor-p">&nbsp; &gt; 구입목적외(대환, 생활안정자금, 전(월)세보증금 반환)인&nbsp;<span style="font-size: 14px">경우 </span><span style="font-size: 14px">0.50%</span><span style="font-size: 14px">적용</span></p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(비대면 적용금리)(최대 0.43%)</p><p class="ssp-editor-p">-부동산 전자계약 우대금리 0.20%</p><p class="ssp-editor-p">-마케팅 활용 동의 고객 0.03%</p><p class="ssp-editor-p">-비대면신청 고객 0.20%</p>', 108, NOW(), NOW()),
(5, 'INFO:최종금리', '○ 先 고정금리 적용시(대면 적용금리)
-고정금리 3년적용시 최저 연 3.96% ~ 최고 연5.86% (기준금리 3.66%, 2026.04.22 현재)
-고정금리 4년적용시 최저 연 4.04% ~ 최고 연5.94% (기준금리 3.74%, 2026.04.22 현재)
-고정금리 5년적용시 최저 연 4.12% ~ 최고 연6.02% (기준금리 3.82%, 2026.04.22 현재)
-고정금리 6년적용시 최저 연 4.15% ~ 최고 연6.05% (기준금리 3.85%, 2026.04.22 현재)
-고정금리 7년적용시 최저 연 4.17% ~ 최고 연6.07% (기준금리 3.87%, 2026.04.22 현재)
-고정금리 8년적용시 최저 연 4.25% ~ 최고 연6.15% (기준금리 3.95%, 2026.04.22 현재)
-고정금리 9년적용시 최저 연 4.33% ~ 최고 연6.23% (기준금리 4.03%, 2026.04.22 현재)
-고정금리 10년적용시 최저 연 4.41% ~최고 연6.31% (기준금리 4.11%, 2026.04.22 현재)
(최저금리는 가산금리 미적용 및 우대금리 1.30%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 외부감정이용 등을 적용한 금리)
○ 後 변동금리 적용시(대면 적용금리)
- 변동금리 3개월적용시 최저 연 4.17% ~ 최고 연5.37% (기준금리 2.77%, 2026.04.22 현재)
- 변동금리 6개월적용시 최저 연 4.24% ~ 최고 연5.44% (기준금리 2.84%, 2026.04.22 현재)
(최저금리는 가산금리 미적용 및 우대금리 0.60%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 외부감정이용 등을 적용한 금리)

○ 先 고정금리 적용시(비대면 적용금리)
-고정금리 3년적용시 최저 연 4.16% ~ 최고 연 5.29% (기준금리 3.66%, 2026.04.22 현재)
-고정금리 5년적용시 최저 연 4.32% ~ 최고 연 5.45% (기준금리 3.82%, 2026.04.22 현재)
-고정금리 7년적용시 최저 연 4.37% ~ 최고 연 5.50% (기준금리 3.87%, 2026.04.22 현재)
(최저금리는 가산금리 미적용 및 우대금리 0.73%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 LTV 70% 초과 등 가산금리 모두 적용한 금리)
○ 後 변동금리 적용시(비대면 적용금리)
- 신잔액COFIX: 2.45%(2026.04.22현재)
- 최저 연 3.93% ~ 최고 연 4.76% (2026.04.22 현재)', 109, NOW(), NOW()),
(5, 'INFO:담보조건', '- 대상주택 (대지포함) 근저당권 설정
- 소액보증금보증제도(소액임차보증금 차감액만큼 대출한도를 증가시킬수 있는 보험증권 또는 보증서를 말함)
  ※ 서울보증보험 MCI: 보증료 은행부담
  ※ 한국주택금융공사 구입자금보증: 보증료 은행부담(단, 비대면대출 취급 시 가입 불가)
', 110, NOW(), NOW()),
(5, 'INFO:가입방법', ' 영업점, 모바일뱅킹(개인)', 111, NOW(), NOW()),
(5, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li><b>인지세</b></li></ul><p class="ssp-editor-p">- 「인지세법」에 따라 대출약정시 납부하는 세금으로 대출금액에 따라 세액이 차등적용되며, 은행과 고객이 각각 50% 부담&nbsp;</p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" ><b>대출금액</b></th><th colspan="2" ><b>인지세액</b></th></tr><tr><th ><b>고객</b></th><th ><b>은행</b></th></tr></thead><tbody><tr><td ><p class="ssp-editor-p" style="text-align: center">5천만원 초과1억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">1억원 초과10억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">10억원 초과</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td></tr></tbody></table></div><ul class="info-list type-li-dot"><li><b>담보취득비용</b></li></ul><p>&nbsp; &nbsp; - (근저당권 설정 시) 국민주택채권매입 관련 할인 비용은 고객이 부담<br></p><p>&nbsp; &nbsp; - 다만, 정확한 비용은 대출 실행일에 확정<br></p><ul class="info-list type-li-dot"><li><b>근저당권 채권최고액 감액 및 근저당권 말소 비용은 고객이 부담</b></li><li>기타비용으로 부담주체가 불명확한 경우(은행과 채무자가 50% 균등 부담)</li></ul>', 112, NOW(), NOW()),
(5, 'INFO:중도상환수수료', '- 중도상환수수료: 중도상환금액 X 아래 수수료율 X 대출잔여일수/대출기간
- 취급후 3년 이내 1.00%', 113, NOW(), NOW()),
(5, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(5, 'INFO:마이너스통장(종합통장대출)가능여부', '부', 115, NOW(), NOW()),
(5, 'INFO:기타', '<ul class="info-list type-li-dot"><li></li><li>본 상품은 고정금리 및 변동금리의 장점을 동시에 활용 가능하나 금리상승시 고정금리 상품보다 금리하락시 변동금리 상품보다 불리할 수도 있습니다.</li><li>고객님의 신용상태 및 저희 은행의 심사기준에 따라서 대출한도와 대출금리가 차등 적용되며 대출취급이 불가능 할 수 있습니다.</li><li>대출이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야 하며, 금융거래 제약 등 불이익을 받으실 수 있으니 유의하시기 바랍니다.</li></ul>', 116, NOW(), NOW()),
(5, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>금융채 : 4.15%(2026-06-23현재)</li><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>금융채 : KIS채권평가㈜와 한국자산평가㈜에서 고시하는 기간별 『금융채Ⅰ(은행채) 무보증AAA등급』의 직전 영업일 단순평균 시가평가기준수익률에 신규시 결정된 가산이율을 더하여 적용.<br>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(5, 'RATE:기본금리', '<p class="ssp-editor-p"></p><p class="ssp-editor-p">○先고정금리적용시</p><p class="ssp-editor-p">&nbsp; &nbsp; 대면 :기간별(3년 ~10년)금융채 +1.60%</p><p class="ssp-editor-p">&nbsp; &nbsp; 비대면: 기간별(3년,5년,7년)금융채 +1.23%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시</p><p class="ssp-editor-p">&nbsp; &nbsp; &nbsp; 대면 : 기간별(3개월,6개월)금융채 +2.00%</p><p class="ssp-editor-p">&nbsp; &nbsp; &nbsp; 비대면 : 신잔액기준COFIX+ 1.91%</p><p></p>', 301, NOW(), NOW()),
(5, 'RATE:가산금리', '<p class="ssp-editor-p">○先 고정금리(3년 ~10년)적용시(대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액249백만원초과 ~ 498백만원이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액498백만원초과 0.20%</p><p class="ssp-editor-p">-외부감정이용(공동주택가격자문확인서 및 정식감정) 0.10%</p><p class="ssp-editor-p">-LTV 60%초과 또는 주택담보대출 3건이상보유0.10%</p><p class="ssp-editor-p">-5년초과 7년이내 고정금리 0.20%</p><p class="ssp-editor-p">-7년초과 10년이내 고정금리 0.40%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○後 변동금리 적용시(대면적용금리) </p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 249백만원 초과 ~498백만원 이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 498백만원 초과 0.20%</p><p class="ssp-editor-p">-외부감정이용(공동주택가격자문확인서 및 정식감정) 0.10%</p><p class="ssp-editor-p">-LTV 60%초과 또는 주택담보대출 3건이상보유0.10%</p><p class="ssp-editor-p">-5년초과 7년이내 고정금리 0.20%</p><p class="ssp-editor-p">- 7년초과10년이내고정금리 0.40%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○先 고정금리(3,5,7년)적용시(비대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액249백만원초과 ~ 498백만원이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액498백만원초과 0.20%</p><p class="ssp-editor-p">-LTV 60% 초과0.05%</p><p class="ssp-editor-p">-LTV 70% 초과 0.10%</p><p class="ssp-editor-p">-주택담보대출 보유건수3건 이상0.10%</p><p class="ssp-editor-p">- 신용평가등급별가산금리 AS 9등급또는 NICE 674점이하</p><p class="ssp-editor-p">&nbsp;  또는 KCB 624점이하 0.20%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(비대면 적용금리)</p><p class="ssp-editor-p">-주택신보출연대상</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원이하 0.01%</p><p class="ssp-editor-p">&nbsp; &gt; 대출금액124.5백만원초과 ~ 249백만원이하 0.03%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 249백만원 초과 ~498백만원 이하 0.17%</p><p class="ssp-editor-p">&nbsp; &gt; &nbsp;대출금액 498백만원 초과 0.20%</p><p class="ssp-editor-p">-LTV 60% 초과 0.05%</p><p class="ssp-editor-p">-LTV 70% 초과 0.10%</p><p class="ssp-editor-p">-주택담보대출 보유건수3건 이상0.10%</p><p class="ssp-editor-p">- 신용평가등급별가산금리 AS 9등급또는 NICE 674점이하</p><p class="ssp-editor-p">&nbsp;  또는 KCB 624점이하 0.20%</p>', 302, NOW(), NOW()),
(5, 'RATE:우대금리', '<p class="ssp-editor-p">○先 고정금리 적용시(대면 적용금리)(최대1.30%)</p><p class="ssp-editor-p">-거래실적에 따른 감면금리(최대0.60%)</p><p class="ssp-editor-p">&nbsp;①급여(연금) 및가맹점 결제대금(요양급여포함) 자동이체(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;②예금평잔 기준(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;③매3개월간신용카드사용금액 1백만원이상0.10%</p><p class="ssp-editor-p">&nbsp;④매3개월간신용카드사용금액 2백만원이상0.20%</p><p class="ssp-editor-p">-기타감면금리(최대 0.70%)</p><p class="ssp-editor-p">&nbsp; ①부동산 전자계약(구입목적限) 0.20%</p><p class="ssp-editor-p">&nbsp; ②구입목적 외 대출 0.20%</p><p class="ssp-editor-p">&nbsp; ③ 영업점장 우대금리 최대 0.30%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(대면 적용금리)(최대0.60%)</p><p class="ssp-editor-p">&nbsp;  ①급여(연금) 및가맹점 결제대금(요양급여포함)&nbsp;<span style="font-size: 14px">자동이체</span><span style="font-size: 14px">(</span><span style="font-size: 14px">배우자미포함</span><span style="font-size: 14px">) 0.20%</span></p><p class="ssp-editor-p">&nbsp;  ②예금평잔 기준(배우자미포함) 0.20%</p><p class="ssp-editor-p">&nbsp;  ③매3개월간신용카드사용금액 1백만원이상0.10%</p><p class="ssp-editor-p">&nbsp;  ④ 매3개월간 신용카드사용금액 2백만원이상0.20%</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○先 고정금리 적용시(비대면 적용금리)(최대 0.73%)</p><p class="ssp-editor-p">-부동산 전자계약 우대금리 0.20%</p><p class="ssp-editor-p">-마케팅 활용 동의 고객 0.03%</p><p class="ssp-editor-p">-비대면 신청 고객 최대 0.50%</p><p class="ssp-editor-p">&nbsp;  &gt; 구입목적인경우 0.20% 적용</p><p class="ssp-editor-p">&nbsp; &gt; 구입목적외(대환, 생활안정자금, 전(월)세보증금 반환)인&nbsp;<span style="font-size: 14px">경우 </span><span style="font-size: 14px">0.50%</span><span style="font-size: 14px">적용</span></p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">○ 後 변동금리 적용시(비대면 적용금리)(최대 0.43%)</p><p class="ssp-editor-p">-부동산 전자계약 우대금리 0.20%</p><p class="ssp-editor-p">-마케팅 활용 동의 고객 0.03%</p><p class="ssp-editor-p">-비대면신청 고객 0.20%</p>', 303, NOW(), NOW()),
(5, 'RATE:최종금리', '○ 先 고정금리 적용시(대면 적용금리)<br>-고정금리 3년적용시 최저 연 3.96% ~ 최고 연5.86% (기준금리 3.66%, 2026.04.22 현재)<br>-고정금리 4년적용시 최저 연 4.04% ~ 최고 연5.94% (기준금리 3.74%, 2026.04.22 현재)<br>-고정금리 5년적용시 최저 연 4.12% ~ 최고 연6.02% (기준금리 3.82%, 2026.04.22 현재)<br>-고정금리 6년적용시 최저 연 4.15% ~ 최고 연6.05% (기준금리 3.85%, 2026.04.22 현재)<br>-고정금리 7년적용시 최저 연 4.17% ~ 최고 연6.07% (기준금리 3.87%, 2026.04.22 현재)<br>-고정금리 8년적용시 최저 연 4.25% ~ 최고 연6.15% (기준금리 3.95%, 2026.04.22 현재)<br>-고정금리 9년적용시 최저 연 4.33% ~ 최고 연6.23% (기준금리 4.03%, 2026.04.22 현재)<br>-고정금리 10년적용시 최저 연 4.41% ~최고 연6.31% (기준금리 4.11%, 2026.04.22 현재)<br>(최저금리는 가산금리 미적용 및 우대금리 1.30%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 외부감정이용 등을 적용한 금리)<br>○ 後 변동금리 적용시(대면 적용금리)<br>- 변동금리 3개월적용시 최저 연 4.17% ~ 최고 연5.37% (기준금리 2.77%, 2026.04.22 현재)<br>- 변동금리 6개월적용시 최저 연 4.24% ~ 최고 연5.44% (기준금리 2.84%, 2026.04.22 현재)<br>(최저금리는 가산금리 미적용 및 우대금리 0.60%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 외부감정이용 등을 적용한 금리)<br><br>○ 先 고정금리 적용시(비대면 적용금리)<br>-고정금리 3년적용시 최저 연 4.16% ~ 최고 연 5.29% (기준금리 3.66%, 2026.04.22 현재)<br>-고정금리 5년적용시 최저 연 4.32% ~ 최고 연 5.45% (기준금리 3.82%, 2026.04.22 현재)<br>-고정금리 7년적용시 최저 연 4.37% ~ 최고 연 5.50% (기준금리 3.87%, 2026.04.22 현재)<br>(최저금리는 가산금리 미적용 및 우대금리 0.73%를 모두 적용하는 경우, 최고금리는 출연료 미적용 및 LTV 70% 초과 등 가산금리 모두 적용한 금리)<br>○ 後 변동금리 적용시(비대면 적용금리)<br>- 신잔액COFIX: 2.45%(2026.04.22현재)<br>- 최저 연 3.93% ~ 최고 연 4.76% (2026.04.22 현재)', 304, NOW(), NOW()),
(5, 'DOC:여신거래 기본약관', '/terms/p5/0.pdf', 500, NOW(), NOW()),
(5, 'DOC:주택담보대출 상품설명서', '/terms/p5/1.pdf', 501, NOW(), NOW()),
(5, 'DOC:ONE주택담보대출 공시자료', '/terms/p5/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(5, 'PREF_5_1', '급여(연금)·가맹점 자동이체', 0.2, '급여(연금) 및 가맹점 결제대금 자동이체 시', NOW(), NOW()),
(5, 'PREF_5_2', '예금 평균잔액', 0.2, '예금평잔 기준 충족 시', NOW(), NOW()),
(5, 'PREF_5_3', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW()),
(5, 'PREF_5_4', '부동산 전자계약(구입목적)', 0.2, '부동산 전자계약 이용(구입목적) 시', NOW(), NOW()),
(5, 'PREF_5_5', '비대면 신청', 0.2, '비대면 신청 고객', NOW(), NOW()),
(5, 'PREF_5_6', '마케팅 활용 동의', 0.03, '마케팅 활용 동의 고객', NOW(), NOW());

-- [6] 부산시 소상공인 3無 희망잇기 마이너스대출 (보증서대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(6, '부산시 소상공인 3無 희망잇기 마이너스대출', 4.7, '1년(최초 대출기간 포함 최장 4년 기한연장)', 'SALE', '보증서대출', '0300010832', '부산시 소상공인께 마이너스대출 한도를 드리는', '4.7', '4.7', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(6, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(6, 'LOAN_LIMIT', '5백만원 또는 1천만원(고정)', 5, NOW(), NOW()),
(6, 'LOAN_TERM', '1년(최초 대출기간 포함 최장 4년 기한연장)', 6, NOW(), NOW()),
(6, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(6, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(6, 'OPT_RATE_CYCLES', '6', 10, NOW(), NOW()),
(6, 'OPT_TERMS', '1년,2년,3년', 11, NOW(), NOW()),
(6, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":12,"maxM":48,"minIncl":true}]', 12, NOW(), NOW()),
(6, 'INFO:상품개요', '부산광역시 소상공인의 경영안정과 자금부담 완화를 위한 상생금융의 일환으로 마이너스대출 지원', 100, NOW(), NOW()),
(6, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">1. 부산시소재&nbsp; 소상공인으로서 사업자 등록&nbsp; 후 가동(영업)중인&nbsp;개인기업( 법인, 외국인 제외)<br>2. 업력 6개월 이상,&nbsp; 대표자 개인 신용평점 595점이상<br>3. 당기매출액 12백만 원이상 또는 최근 3개월매출액 3백만원&nbsp;이상 발생</p><p class="ssp-editor-p">&nbsp;※공동 대표로 운영 중인 개인기업의&nbsp; 경우, 비대면 보증 진행 불가(사업장관할 신용보증재단 방문 시 대면 진행 가능)</p><p class="ssp-editor-p">&nbsp;<span style="font-family: Pretendard; font-size: 100%">※</span><span style="font-size: 100%">단</span><span style="font-size: 100%">,(</span><span style="font-size: 100%">재</span><span style="font-size: 100%">)</span><span style="font-size: 100%">보증제한업종</span><span style="font-size: 100%">(</span><span style="font-size: 100%">재단의보증금지기업 및 보증제한기업 포함</span><span style="font-size: 100%">) </span><span style="font-size: 100%">지원 </span><span style="font-size: 100%">제외</span></p>', 101, NOW(), NOW()),
(6, 'INFO:대출한도', '5백만원 또는 1천만원(고정) ※단, 본건 포함 동일 기업당 재단 보증금액 2억원 초과시 취급불가', 102, NOW(), NOW()),
(6, 'INFO:상환방법 및 대출기간', '만기일시상환 : 1년이내((최초 대출기간 포함 최장 4년 기한연장))', 103, NOW(), NOW()),
(6, 'INFO:금리변동주기', '  6개월', 104, NOW(), NOW()),
(6, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(6, 'INFO:기본금리', '<p>신잔액기준 COFIX + 2.20%</p>', 106, NOW(), NOW()),
(6, 'INFO:최저금리', '4.65', 107, NOW(), NOW()),
(6, 'INFO:최고금리', '4.65', 108, NOW(), NOW()),
(6, 'INFO:최종금리', '최저 연 4.65 % ~최고 연 4.65 % (2026-04-16 신잔액기준 (COFIX) : 2.45 %)', 109, NOW(), NOW()),
(6, 'INFO:담보조건', '부산신용보증재단 신용보증서(보증비율 95%)', 110, NOW(), NOW()),
(6, 'INFO:가입방법', '영업점,모바일뱅킹(개인)', 111, NOW(), NOW()),
(6, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">1. 인지세 : 해당사항없음<br>2. 보증료율 : 면제(연간 0.8% 전액 은행 부담, 최장 4년)<br>3. 한도약정수수료, 약정한도미사용수수료 : 면제<br></p>', 112, NOW(), NOW()),
(6, 'INFO:중도상환수수료', '면제', 113, NOW(), NOW()),
(6, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(기업용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(6, 'INFO:마이너스통장(종합통장대출)가능여부', '여', 115, NOW(), NOW()),
(6, 'INFO:기타', '<p>※채널별 신청 가능&nbsp;금액</p><p class="ssp-editor-p">영업점 : 5백만원, 1천만원 신청 가능</p><p class="ssp-editor-p">모바일뱅킹(APP) : 1천만원 신청 가능</p>', 116, NOW(), NOW()),
(6, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(6, 'RATE:기본금리', '<p>신잔액기준 COFIX + 2.20%</p>', 301, NOW(), NOW()),
(6, 'RATE:최종금리', '최저 연 4.65 % ~최고 연 4.65 % (2026-04-16 신잔액기준 (COFIX) : 2.45 %)', 302, NOW(), NOW()),
(6, 'DOC:여신거래 기본약관', '/terms/p6/0.pdf', 500, NOW(), NOW()),
(6, 'DOC:기업대출 상품설명서', '/terms/p6/1.pdf', 501, NOW(), NOW()),
(6, 'DOC:(상품설명서)부산시소상공인3무희망잇기마이너스대출', '/terms/p6/2.pdf', 502, NOW(), NOW());

-- [7] 새희망홀씨Ⅱ (서민금융)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(7, '새희망홀씨Ⅱ', 10.5, '6개월 ~ 7년(상환방식에 따라 다름)', 'SALE', '서민금융', '0300000161', '새로운 희망을 드리는 착한금융상품', '3.7', '10.5', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(7, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(7, 'LOAN_LIMIT', '35백만원(최소 1백만원)', 5, NOW(), NOW()),
(7, 'LOAN_TERM', '6개월 ~ 7년(상환방식에 따라 다름)', 6, NOW(), NOW()),
(7, 'TARGET', '급여소득자, 개인사업자, 기타연금소득자', 7, NOW(), NOW()),
(7, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(7, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(7, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(7, 'OPT_TERMS', '6개월,1년,2년,3년,5년,7년', 11, NOW(), NOW()),
(7, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":84,"minIncl":false},{"method":"원리금균등상환","minM":6,"maxM":84,"minIncl":true}]', 12, NOW(), NOW()),
(7, 'INFO:상품개요', '<p class="ssp-editor-p">저소득자, 저신용자 등 금융 거래 취약층을 위한 서민금융지원 상품</p><p class="ssp-editor-p">&nbsp;</p>', 100, NOW(), NOW()),
(7, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>소득구분별 상세 자격요건은 다음과 같음</li></ul><table class="tbl-matrix" ><colgroup><col><col><col><col></colgroup><tbody><tr><th rowspan="2" scope="" >구분</th><th colspan="3" scope="" >소득구분</th></tr><tr><th class="" ><p class="ssp-editor-p" style="text-align: center">급여소득자</p></th><th class="" ><p class="ssp-editor-p" style="text-align: center">개인사업자</p></th><th class="" ><p class="ssp-editor-p" style="text-align: center">연금소득자<sup>주)</sup></p></th></tr><tr><th scope="" ><p class="ssp-editor-p">재직(사업)기간</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">연금 1회 이상 수령</p></td></tr><tr><th scope="" ><p class="ssp-editor-p">연소득 및 CB평점</p></th><td colspan="3" scope="" >1) 연소득 40백만원 이하인자 또는<br>2) 연소득 40백만원 초과 ~ 50백만원 이하인 자 중 신용평점 NICE 749점 이하 또는&nbsp;KCB 700점 이하</td></tr></tbody></table><p><span style="font-size: 100%">주) 공적연금(국민연금, 공무원연금, 군인연금, 사학연금)만 가능하며, 기초연금은 불가. 단, 비대면 채널은 국민연금 限</span></p><p style="font-family: Pretendard; font-size: 12pt; line-height: 1.2; margin-top: 0px; margin-bottom: 0px"><span style="font-family: 굴림; color: rgb(0, 0, 0); font-size: 12pt"><span style="font-family: 돋움; color: rgb(102, 102, 102)"><span style="font-family: 굴림; color: rgb(0, 0, 0); font-size: 12pt"><span style="font-family: 돋움; color: rgb(102, 102, 102)"><span style="font-family: 굴림; color: rgb(0, 0, 0); font-size: 12pt">
 </span></span></span></span></span></p>', 101, NOW(), NOW()),
(7, 'INFO:대출한도', '<p class="ssp-editor-p">심사결과에 따라 최소 1백만원 ~ 최대 35백만원</p>', 102, NOW(), NOW()),
(7, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식: 6개월 이상 ~ 1년</li><li>원금균등 상환방식: 1년 초과 ~ 7년</li><li>원리금균등 상환방식: 6개읠 이상 ~ 7년</li></ul><p><span style="font-size: 100%">※ 65세이상의 경우 대출만기는 3년으로 제한</span><br></p><ul class="info-list type-li-dot"><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p><span style="font-size: 100%">※ 휴일 대출원금 또는 이자 상환 가능</span></p><p class="ssp-editor-p">※ 일시상환방식 대출은 대출기간 중 최소 대출금액의 10%이상 상환하여야 대출만기 시 기한연기 가능</p>', 103, NOW(), NOW()),
(7, 'INFO:금리변동주기', '<p class="ssp-editor-p">3, 6개월 중 선택</p>', 104, NOW(), NOW()),
(7, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(7, 'INFO:기본금리', '<p>기준금리 + 8.00%</p>', 106, NOW(), NOW()),
(7, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 연 1.80%</li><li>당행 내부등급에 따라 최대 연 1.20%</li></ul>', 107, NOW(), NOW()),
(7, 'INFO:우대금리', '<p>1) 기본 감면금리</p><p class="ssp-editor-p">- 당행 급여이체 0.30%</p><p class="ssp-editor-p">- 새희망홀씨 서민금융상담행사 신청고객(비대면 채널 적용 불가) 0.50%</p><p class="ssp-editor-p">- 신용평점 하위10% 이하 또는 연소득 3천만원 이하 0.50%</p><p class="ssp-editor-p">2) 신용평점에 따라 최대 4.00%</p><p class="ssp-editor-p">3) 특별 감면금리(비대면 채널 적용 불가) : 최대 1.00%</p><p class="ssp-editor-p">- 미성년자 2자녀 이상 가구 0.20%</p><p class="ssp-editor-p">- 기초생활수급권자 0.20%</p><p class="ssp-editor-p">- 한부모 가정 0.20%</p><p class="ssp-editor-p">- 만 60세 이상 부모 부양자 0.20%</p><p class="ssp-editor-p">- 다문화 가정 0.20%</p><p class="ssp-editor-p">- 청년층(만 34세 이하) 0.20%</p><p class="ssp-editor-p">- 고령자(만 65세 이상) 0.20%</p><p class="ssp-editor-p">- 장애인 0.20%</p><p class="ssp-editor-p">- 금융교육 이수자 0.20%</p><p class="ssp-editor-p">4) 성실상환고객 감면금리 : 최대 1.00% (1회 감면금리, 최초 대출취급 후 1년간 연체가 없는 경우 적용하며 최대 2회까지 적용)</p><p class="ssp-editor-p">5) 포용특별우대금리 0.50%</p><p class="ssp-editor-p">단,포용 특별우대금리는 2026년 12월 31일까지 신규 실행건에 한하여 적용 가능하며, 감면적용계좌 기한연기 시 적용 불가함.</p>', 108, NOW(), NOW()),
(7, 'INFO:최종금리', '최저 연 3.65% ~ 최고 연 10.50% (2026.04.29 신잔액기준 COFIX : 2.45%)
(최저금리는 새희망홀씨 서민금융상담행사 신청 대상자(단. 대면 가입 한정) 및 특별 감면금리 대상자(단, 대면 가입 한정)이고, 신잔액 COFIX 변동금리 적용, 최대 우대금리 모두 적용 및 가산금리 미적용하는 경우. 단, 성실상환고객 감면금리 미적용)
※ 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.
※ 상기 적용금리에도 불구하고 신규 취급 시 대출금리는 상품별 최고금리 연 10.50%를 초과할 수 없습니다.', 109, NOW(), NOW()),
(7, 'INFO:담보조건', '<p class="ssp-editor-p">신용</p>', 110, NOW(), NOW()),
(7, 'INFO:가입방법', '<p class="ssp-editor-p">영업점, 모바일</p>', 111, NOW(), NOW()),
(7, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li>인지세<ul class="info-list type-li-dash-ed"><li><span>「인지세법」에 따라 대출약정 시 납부하는 세금으로 대출금액에 따라 세액이 차등 적용되며, 은행과 고객이 각각 50% 부담</span><div class="table-box"><table class="tbl-matrix" ><tbody><tr><th scope="" ><p class="ssp-editor-p"><b>대출금액</b></p></th><th scope="" ><p class="ssp-editor-p"><b>5</b><b>천만원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>5</b><b>천만원 초과</b></p><p class="ssp-editor-p"><b>~ 1</b><b>억원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>1</b><b>억원 초과</b></p><p class="ssp-editor-p"><b>~ 10</b><b>억원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>10</b><b>억원 초과</b></p></th></tr><tr><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>인지세액</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>비과세</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>7</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각3만5천원)</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>15</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각7만5천원)</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>35</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각17만5천원)</b></p></td></tr></tbody></table></div></li></ul></li></ul>', 112, NOW(), NOW()),
(7, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 113, NOW(), NOW()),
(7, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li><span>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</span></li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다. </li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며, 금융거래 제약 등 불이익을 받으실 수 있습니다. 단 ''개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률''에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출장액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생이로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(7, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>공통 : 신분증(주민등록증, 운전면허증등), 주민등록등본, 재직증명서(개인사업자인경우, 사업자등록증사본)</li><li>급여소득자(택일)</li></ul>- 최근연도 연말정산용 근로소득 원천징수영수증<br>- 최근3개월월 급여 명세서 및 최근 3개월 월 급여 입금내역 통장 사본 등<br><ul class="info-list type-li-dot"><li>사업소득자(택일)</li></ul>- 최근 3개월 이상 건강?장기 요양 보험 보험료 납부확인서<br>- 종합소득세과세표준 확정신고 및 납부계산서(세무사 확인분)등<div><ul class="info-list type-li-dot"><li>연금소득자</li></ul>- 연금수급권자 확인서 또는 연금수령 확인이 가능한 지급기관의 증명서</div><div>(단,연금수령액이 표기되지 않는 경우 연금수령 내역 통장사본 포함)</div><div><br><p class="info-list type-li-dot ssp-editor-p">※ 조건에 따라 추가 서류가 필요할 수 있으며, 모바일대출심사 시 공공마이데이터 또는 스크래핑을 통해 모바일 서류 제출이 가능합니다.</p></div>', 115, NOW(), NOW()),
(7, 'INFO:마이너스통장(종합통장대출)가능여부', '<p class="ssp-editor-p">부</p>', 116, NOW(), NOW()),
(7, 'INFO:기타', '<p class="ssp-editor-p">※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p><ul class="info-list type-li-dot"><li>비대면채널 제한사항&nbsp;<ul class="info-list type-li-dash-ed"><li>비대면 채널 신용대출 신청 시 1일 총 7회, 30일 내 총 20회까지 신청 가능합니다.</li><li>비대면 채널 신용대출 실행 시 1일 최대 1건, 1인 최대 7건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출 진행이 불가합니다.</li></ul></li></ul>', 117, NOW(), NOW()),
(7, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.<br>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(7, 'RATE:기본금리', '<p>기준금리 + 8.00%</p>', 301, NOW(), NOW()),
(7, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 연 1.80%</li><li>당행 내부등급에 따라 최대 연 1.20%</li></ul>', 302, NOW(), NOW()),
(7, 'RATE:우대금리', '<p>1) 기본 감면금리</p><p class="ssp-editor-p">- 당행 급여이체 0.30%</p><p class="ssp-editor-p">- 새희망홀씨 서민금융상담행사 신청고객(비대면 채널 적용 불가) 0.50%</p><p class="ssp-editor-p">- 신용평점 하위10% 이하 또는 연소득 3천만원 이하 0.50%</p><p class="ssp-editor-p">2) 신용평점에 따라 최대 4.00%</p><p class="ssp-editor-p">3) 특별 감면금리(비대면 채널 적용 불가) : 최대 1.00%</p><p class="ssp-editor-p">- 미성년자 2자녀 이상 가구 0.20%</p><p class="ssp-editor-p">- 기초생활수급권자 0.20%</p><p class="ssp-editor-p">- 한부모 가정 0.20%</p><p class="ssp-editor-p">- 만 60세 이상 부모 부양자 0.20%</p><p class="ssp-editor-p">- 다문화 가정 0.20%</p><p class="ssp-editor-p">- 청년층(만 34세 이하) 0.20%</p><p class="ssp-editor-p">- 고령자(만 65세 이상) 0.20%</p><p class="ssp-editor-p">- 장애인 0.20%</p><p class="ssp-editor-p">- 금융교육 이수자 0.20%</p><p class="ssp-editor-p">4) 성실상환고객 감면금리 : 최대 1.00% (1회 감면금리, 최초 대출취급 후 1년간 연체가 없는 경우 적용하며 최대 2회까지 적용)</p><p class="ssp-editor-p">5) 포용특별우대금리 0.50%</p><p class="ssp-editor-p">단,포용 특별우대금리는 2026년 12월 31일까지 신규 실행건에 한하여 적용 가능하며, 감면적용계좌 기한연기 시 적용 불가함.</p>', 303, NOW(), NOW()),
(7, 'RATE:최종금리', '최저 연 3.65% ~ 최고 연 10.50% (2026.04.29 신잔액기준 COFIX : 2.45%)<br>(최저금리는 새희망홀씨 서민금융상담행사 신청 대상자(단. 대면 가입 한정) 및 특별 감면금리 대상자(단, 대면 가입 한정)이고, 신잔액 COFIX 변동금리 적용, 최대 우대금리 모두 적용 및 가산금리 미적용하는 경우. 단, 성실상환고객 감면금리 미적용)<br>※ 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.<br>※ 상기 적용금리에도 불구하고 신규 취급 시 대출금리는 상품별 최고금리 연 10.50%를 초과할 수 없습니다.', 304, NOW(), NOW()),
(7, 'DOC:여신거래 기본약관', '/terms/p7/0.pdf', 500, NOW(), NOW()),
(7, 'DOC:가계대출 상품설명서', '/terms/p7/1.pdf', 501, NOW(), NOW()),
(7, 'DOC:상품공시자료', '/terms/p7/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(7, 'PREF_7_1', '급여이체', 0.3, '당행 급여이체 시', NOW(), NOW());

-- [8] 햇살론 특례보증 (서민금융)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(8, '햇살론 특례보증', 6, '3년 ~ 6년(거치기간 최대 1년)', 'SALE', '서민금융', '0300013809', '저신용자를 위한 포용금융 서민금융진흥원 보증서 대출상품', '6', '6', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(8, 'BASE_RATE_RAW', '6', 4, NOW(), NOW()),
(8, 'LOAN_LIMIT', '10백만원(최소 50만원, 보증금액 이하)', 5, NOW(), NOW()),
(8, 'LOAN_TERM', '3년 ~ 6년(거치기간 최대 1년)', 6, NOW(), NOW()),
(8, 'TARGET', '서민금융진흥원 보증서 발급 가능 고객', 7, NOW(), NOW()),
(8, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(8, 'OPT_RATE_TYPE', '고정금리', 9, NOW(), NOW()),
(8, 'OPT_TERMS', '1년,2년,3년,5년', 11, NOW(), NOW()),
(8, 'OPT_REPAYMENTS', '[{"method":"원리금균등상환","minM":36,"maxM":72,"minIncl":true}]', 12, NOW(), NOW()),
(8, 'INFO:상품개요', '소득 증빙이 어렵거나 신용등급이 상대적으로 낮아 햇살론 일반보증을 이용하기 어려운 차주를 폭넓게 지원하기 위한 서민금융진흥원 보증부 정책 서민금융상품', 100, NOW(), NOW()),
(8, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">&#x2022;&nbsp;서민금융진흥원으로부터&nbsp;신용보증 결정 통보를 받은 자로써 소득증빙이 가능하며 아래 요건을 모두 충족하는 자</p><p class="ssp-editor-p">-소득 구분별 상세 요건</p><div class="table-box"><table class="tbl-matrix" ><colgroup><col><col><col><col></colgroup><tbody><tr><th ><p class="ssp-editor-p">구분</p></th><th ><p class="ssp-editor-p">근로소득</p></th><th ><p class="ssp-editor-p">사업소득</p></th><th class="" ><p class="ssp-editor-p">연금소득</p></th></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">소득금액</p></td><td colspan="3" ><p class="ssp-editor-p" style="text-align: center">신용평점 NICE 749점 이하 또는&nbsp;KCB 700점 이하로 연소득 35백만원인 자</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">재직(사업)기간</p></td><td ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td class="" ><p class="ssp-editor-p" style="text-align: center">1회 이상 수령</p></td></tr></tbody></table></div><p class="ssp-editor-p">&#x2022;&nbsp;요건 (아래 요건 모두 충족)</p><p class="ssp-editor-p">-&nbsp;민법상 성년(만 19세 이하)</p><p class="ssp-editor-p">-&nbsp;국내에 거주하는 대한민국 국민(외국인, 재외국민, 해외이주신고자 등은 제외)</p><p class="ssp-editor-p">- 최근 6개월 내 연체 횟수 6회 미만 또는 최근 6개월간&nbsp;대출 최장 연체일이 60일을 초과한 경험이 없고, 현재 연체중이지 않은자</p>', 101, NOW(), NOW()),
(8, 'INFO:대출한도', '최소 50만원 ~ 최대 1,000만원 이내 (10만원 단위)
* 직접 보증방식 및 추정소득 활용자인 경우 최대 3백만원', 102, NOW(), NOW()),
(8, 'INFO:상환방법 및 대출기간', '원리금균등상환 : 3년이상 ~ 6년이하
* 원리금균등 분할상환방식 : 매월 정해진 날짜에 주기적으로 원금과 이자를 균등하게 상환
* 이자부과시기 : 매월 후취(대출 해당일 또는 응당일 부과)
* 거치기간 최대 1년 선택 가능
EX) 거치기간 1년, 할부상환기간 5년 = 총 대출기간 6년
※ 휴일 대출원금 또는 이자 상환 가능', 103, NOW(), NOW()),
(8, 'INFO:금리변동주기', '  해당사항없음', 104, NOW(), NOW()),
(8, 'INFO:기준금리', '고정금리', 105, NOW(), NOW()),
(8, 'INFO:기본금리', '<p>대출금리는 서민금융진흥원에서 정한 금리에 따름</p><p class="ssp-editor-p"><span style="font-size: 100%">· 채무자 부담이율 : 대출금리 + 보증료율</span></p><p class="ssp-editor-p">· 대출금리 : 연 6.00% 고정금리 적용 (2025.12.23 현재)<br></p><p class="ssp-editor-p">· 보증료율 : 최대 연 6.50%</p>', 106, NOW(), NOW()),
(8, 'INFO:최종금리', '· 대출금리 : 연 6.00% 고정금리 적용 (2026.04.07 현재)
· 보증료율 : 최저 연 3.90% ~ 최고 연 6.50% 적용 (2026.04.07 현재)
· 최종 고객 부담이율 : 최저 연 9.90% ~ 최고 연 12.50% (2026.04.07 기준)
* 최종 고객 부담이율은 대출금리에 보증료율을 가산하여 산정합니다.', 107, NOW(), NOW()),
(8, 'INFO:담보조건', '서민금융진흥원 보증서(보증비율 : 100%)', 108, NOW(), NOW()),
(8, 'INFO:가입방법', ' 영업점, 모바일뱅킹(개인)', 109, NOW(), NOW()),
(8, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p"><span>※ 수입인지대금 : 5천 만원 초과시 대출금액별 수입인지비용 차등 부과(50% 균등부담)</span></p><p class="ssp-editor-p">&nbsp;</p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" ><b>대출금액</b></th><th colspan="2" ><b>인지세액</b></th></tr><tr><th ><b>고객</b></th><th ><b>은행</b></th></tr></thead><tbody><tr><td ><p class="ssp-editor-p" style="text-align: center">5천만원 초과1억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">1억원 초과10억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">10억원 초과</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td></tr></tbody></table></div>', 110, NOW(), NOW()),
(8, 'INFO:중도상환수수료', '면제', 111, NOW(), NOW()),
(8, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 112, NOW(), NOW()),
(8, 'INFO:필요서류', '<p class="ssp-editor-p">&nbsp;</p><table class="tbl-matrix" cellspacing="0" cellpadding="0"><colgroup><col><col><col></colgroup><tbody><tr><th ><p class="ssp-editor-p">구분</p></th><th ><p class="ssp-editor-p">재직, 사업증빙 (택 1)</p></th><th ><p class="ssp-editor-p">소득증빙 (택 1)</p></th></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">근로소득자</p></td><td ><p class="ssp-editor-p">① 재직증명서 (사업자등록증첨부),공무원증 등 재직확인 서류</p><p class="ssp-editor-p">② 국민연금 가입자 증명서</p><p class="ssp-editor-p">③ 건강보험 자격득실확인서</p></td><td ><p class="ssp-editor-p">① 전년도 근로소득원천징수영수증(연말정산용)</p><p class="ssp-editor-p">② 소득금액증명원(세무서 발급)</p><p class="ssp-editor-p">③ 국민연금 ‘연금산정용 가입내역 확인서’</p><p class="ssp-editor-p">④ 국민건강보험 ‘건강·장기요양보험 보험료 납부 확인서’</p><p class="ssp-editor-p">⑤ 급여내역이 포함된 확인서* 및 최근 3개월간의 월급여 입금통장</p><p class="ssp-editor-p">⑥ 보험사 보험료 납부내역 등 보험정보 활용 (보험업권만 해당)</p><p class="ssp-editor-p">*재직회사가 날인한 급여명세표,임금대장,갑근세원천징수확인서 등</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">사업소득자</p><p class="ssp-editor-p" style="text-align: center">(등록사업자)</p></td><td ><p class="ssp-editor-p">① 사업자등록증(사본)</p><p class="ssp-editor-p">② 사업자등록 증명원</p><p class="ssp-editor-p"> ※ 사업자번호로 국세청 홈페이지 조회 필수(미등록, 휴·폐업인 경우 인정불가)</p></td><td ><p class="ssp-editor-p">① 전년도 소득금액증명원(세무서발급)</p><p class="ssp-editor-p">② 금년도 종합소득세 과세표준 확정 신고 및 납부계산서(세무사 확인분)</p><p class="ssp-editor-p">③ 최근 3개월 국민건강보험 ‘건강·장기요양보험 보험료 납부 확인서’</p><p class="ssp-editor-p">④ 최근 3개월 국민연금 ‘연금산정용 가입내역 확인서’</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">사업소득자</p><p class="ssp-editor-p" style="text-align: center">(미등록사업자)</p></td><td ><p class="ssp-editor-p">현 직장에서 발급한 재직사실확인서*와 재직회사의 사업자등록증</p><p class="ssp-editor-p"> * 고용계약서, 위촉증명서, 재직증명서 등</p></td><td ><p class="ssp-editor-p">① 전년도 소득금액증명원(세무서발급)</p><p class="ssp-editor-p">② 전년도 사업소득원천징수영수증(연말정산용)및 최근 3개월간의 월급여 입금통장 </p><p class="ssp-editor-p">③ 전년도 거주자의 사업소득원천징수 영수증 및  최근 3개월간의 월급여 입금 통장</p><p class="ssp-editor-p">④ 금년도 종합소득세 과세표준 확정 신고 및 납부계산서(세무사 확인분)</p><p class="ssp-editor-p">⑤ 최근 3개월 국민건강보험 ‘건강·장기 요양보험 보험료 납부 확인서’</p><p class="ssp-editor-p">⑥ 최근 3개월 국민연금 ‘연금산정용 가입내역 확인서’</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">연금소득자</p></td><td ><p class="ssp-editor-p">① 연금수급증서<br> (국민/공무원/군인/사학연금)</p><p class="ssp-editor-p">② 연금수급권자 확인서</p><p class="ssp-editor-p">③ 연금기관에서 발급한 지급내역서 등 확인서</p><p class="ssp-editor-p">※ 공적연금(국민연금, 공무원연금, 군인연금, 사학연금)에 한함</p></td><td ><p class="ssp-editor-p">① 최근 1개월 이상 연금기관에서 발급한 지급내역서 등 확인서류</p><p class="ssp-editor-p">② 최근 1개월 이상 연금수령통장</p><p class="ssp-editor-p"> ※ 연금 지급내역서 등 확인서류에 연금수령액이  표기되지 않는 경우 연금수령통장 사본 징구 필수</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">농·축·임·어업 종사자</p></td><td ><p class="ssp-editor-p">&#x2022; 재직증명(고용사실) 확인서 및 급여지급사실확인서</p><p class="ssp-editor-p"> ※ 사업체 및 경영주의 증빙서류* 첨부</p><p class="ssp-editor-p"> * 사업체 : 사업자등록증 또는 고유번호증 사본</p><p class="ssp-editor-p">&nbsp;&nbsp; 경영주 : 조합원확인서, 농지대장 등</p></td><td ><p class="ssp-editor-p"> &#x2022; 급여지급사실확인서</p><p class="ssp-editor-p"> * 확인서에 작성된 월평균 급여액 X 12</p></td></tr></tbody></table><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">· <span style="font-size: 100%">인터넷으로 발급한 소득금액증명원이나 보험료납부확인서 등의 진위여부 확인 후 원본으로 인정</span></p><p class="ssp-editor-p"><span style="font-size: 100%">· </span><span style="font-size: 100%">통장 사본의 경우 인터넷뱅킹 출력본 미인정</span><span style="font-size: 100%">, </span><span style="font-size: 100%">통장거래내역서</span><span style="font-size: 100%">(</span><span style="font-size: 100%">은행직인필</span><span style="font-size: 100%">) </span><span style="font-size: 100%">또는 은행에서 직접 팩스 송부본 인정</span></p>', 113, NOW(), NOW()),
(8, 'INFO:마이너스통장(종합통장대출)가능여부', '부', 114, NOW(), NOW()),
(8, 'INFO:기타', '<p class="ssp-editor-p" style="text-align: left">· 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p>', 115, NOW(), NOW()),
(8, 'RATE:기준금리', '고정금리', 300, NOW(), NOW()),
(8, 'RATE:기본금리', '<p>대출금리는 서민금융진흥원에서 정한 금리에 따름</p><p class="ssp-editor-p"><span style="font-size: 100%">· 채무자 부담이율 : 대출금리 + 보증료율</span></p><p class="ssp-editor-p">· 대출금리 : 연 6.00% 고정금리 적용 (2025.12.23 현재)<br></p><p class="ssp-editor-p">· 보증료율 : 최대 연 6.50%</p>', 301, NOW(), NOW()),
(8, 'RATE:최종금리', '· 대출금리 : 연 6.00% 고정금리 적용 (2026.04.07 현재)<br>· 보증료율 : 최저 연 3.90% ~ 최고 연 6.50% 적용 (2026.04.07 현재)<br>· 최종 고객 부담이율 : 최저 연 9.90% ~ 최고 연 12.50% (2026.04.07 기준)<br>* 최종 고객 부담이율은 대출금리에 보증료율을 가산하여 산정합니다.', 302, NOW(), NOW()),
(8, 'DOC:여신거래 기본약관', '/terms/p8/0.pdf', 500, NOW(), NOW()),
(8, 'DOC:가계대출 상품설명서', '/terms/p8/1.pdf', 501, NOW(), NOW()),
(8, 'DOC:햇살론 특례보증 공시자료', '/terms/p8/2.pdf', 502, NOW(), NOW());

-- [9] BNK 사잇돌 중금리대출 (서민금융)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(9, 'BNK 사잇돌 중금리대출', 9.5, '~5년', 'SALE', '서민금융', '0300000129', '급여소득자, 개인사업자를 위한 SGI보증서 대출', '5.8', '9.5', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(9, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(9, 'LOAN_LIMIT', '2천만원', 5, NOW(), NOW()),
(9, 'LOAN_TERM', '~5년', 6, NOW(), NOW()),
(9, 'TARGET', '서울보증보험 보험증권, 발급 가능 고객', 7, NOW(), NOW()),
(9, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(9, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(9, 'OPT_RATE_CYCLES', '3,6,12', 10, NOW(), NOW()),
(9, 'OPT_TERMS', '5년', 11, NOW(), NOW()),
(9, 'OPT_REPAYMENTS', '[{"method":"원금균등상환","minM":12,"maxM":60,"minIncl":true},{"method":"원리금균등상환","minM":12,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(9, 'INFO:상품개요', '<p class="ssp-editor-p">중신용자의 실질적 상환능력에 따라 적정 필요자금을 지원 및 고금리 대출을 받은 고객에게 낮은 금리로 전환을 위해 은행권 공동 출시하는 SGI서울보증보험 보증보험담보 대출</p>', 100, NOW(), NOW()),
(9, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>서울보증보험 보험증권 발급 가능 고객으로 부산은행의 심사기준을 충족한 고객<ul class="info-list type-li-dash-ed"><li><span>근로소득자: 재직기간 3개월 이상 및 연소득 15백만원 이상</span></li><li><span>사업소득자: 사업영위기간 6개월 이상 및 연소득 10백만원 이상</span></li><li><span>연금소득자: 연금을 1회 이상 수령 및 연소득 10백만원 이상</span></li></ul></li></ul>', 101, NOW(), NOW()),
(9, 'INFO:대출한도', '<p class="ssp-editor-p">신용평가결과에 따라 최고 2천만원이내 (최저 대출금액 1백만원 이상)</p>', 102, NOW(), NOW()),
(9, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>원금균등,원리금균등 상환방식대출 : 5년 이내 </li><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능<br></p>', 103, NOW(), NOW()),
(9, 'INFO:금리변동주기', '<ul class="info-list type-li-dot"><li>대면 : 3개월, 6개월, 12개월</li><li><span>비대면 : 6개월</span></li></ul>', 104, NOW(), NOW()),
(9, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(9, 'INFO:기본금리', '<p>기준금리 + 5.00%</p>', 106, NOW(), NOW()),
(9, 'INFO:가산금리', '<p class="ssp-editor-p">신용평점에 따라 최대 연 2.00%</p>', 107, NOW(), NOW()),
(9, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 1.50%</li><li>비대면 가입고객 우대금리 최대 0.20%</li><li>금융자산정보 조회 동의(대면채널에 한함) 최대 0.10%</li><li>성실상환고객<sup>주) </sup>감면금리: 최대 0.60%<br>(연 1회 0.30%이며,최대 2회까지 최대 0.60%p 범위내 감면 가능)</li></ul><div>주) 대출 취급 후 해당 기간동안 연체 없는 고객</div>', 108, NOW(), NOW()),
(9, 'INFO:최종금리', '- 최저 연 5.75% ~ 최고 연 9.45% (2026.04.16 신잔액기준 COFIX :2.45%)
(최저금리는 신잔액 COFIX 변동금리 적용, 가산금리 미적용 및 최대 우대금리 1.70% 모두 적용하는 경우. 단, 성실상환 고객 감면금리 미적용)
(최고금리는 신잔액 COFIX 변동금리 적용, 가산금리 적용 및 우대금리 미적용 하는 경우)
* 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.', 109, NOW(), NOW()),
(9, 'INFO:담보조건', '<p class="ssp-editor-p">SGI서울보증보험 보험증권</p>', 110, NOW(), NOW()),
(9, 'INFO:가입방법', ' 영업점, 모바일뱅킹(개인)', 111, NOW(), NOW()),
(9, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li>보험료&nbsp;-&nbsp;은행부담</li><li><span>인지세</span><span>&nbsp;-&nbsp;해당사항&nbsp;없음</span></li></ul>', 112, NOW(), NOW()),
(9, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 113, NOW(), NOW()),
(9, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li><span>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</span></li><li><span>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</span></li><li><span>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다.&nbsp;단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지&nbsp;아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br></span><span>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</span></li><li><span>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</span></li><li><span>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</span></li><li><span>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</span></li><li><span>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</span></li><li><span>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</span></li><li><span>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</span></li><li><span>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</span></li><li><span>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</span></li></ul>', 114, NOW(), NOW()),
(9, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>본인 확인서류(주민등록증 또는 운전면허증 등)</li><li>재직 및 소득증빙 서류<ul class="info-list type-li-dash-ed"><li>&nbsp;급여소득자<ul class="info-list type-li-gt"><li>재직자료: 재직증명서, 건강보험 자격득실 확인서, 국민연금 가입증명 등</li><li>소득자료: 근로소득원천징수영수증, 소득금액증명원, 국민연금 가입내역 확인서, 건강,장기요양보험료 납부확인서 등</li></ul></li><li>사업소득자<ul class="info-list type-li-gt"><li>재직자료: 사업자등록증(확인원), 위(탁)촉계약서 등 재직사실 확인서</li><li>소득자료: 소득금액증명원,&nbsp; 국민연금 가입내역확인서, 건강,장기요양보험료납부확인서 등</li></ul></li><li>연금소득자<ul class="info-list type-li-gt"><li>재직자료: 연금수급증명서</li><li>소득자료: 연금수급권자 확인서 또는 연금통장 사본 등<br>(단, 연금수령액이 표기되지 않는 경우 연금수령 내역통장 사본 포함)</li></ul></li></ul></li></ul>', 115, NOW(), NOW()),
(9, 'INFO:마이너스통장(종합통장대출)가능여부', '<p class="ssp-editor-p">부</p>', 116, NOW(), NOW()),
(9, 'INFO:기타', '<ul class="info-list type-li-dot"><li>비대면대출 제한사항<ul class="info-list type-li-dash-ed"><li>동일상품으로 1일 5회까지 신청 가능하며, 통합하여 1일 7회, 1개월 20회까지 신청 가능합니다.</li><li>동일상품으로 1인 최대 3건까지 실행 가능하며, 통합하여 최대 5건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우<span>&nbsp;대출진행이 불가합니다.</span><span>&nbsp;</span><span>&nbsp;</span></li></ul></li></ul><p class="ssp-editor-p"><b>※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</b></p>', 117, NOW(), NOW()),
(9, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(9, 'RATE:기본금리', '<p>기준금리 + 5.00%</p>', 301, NOW(), NOW()),
(9, 'RATE:가산금리', '<p class="ssp-editor-p">신용평점에 따라 최대 연 2.00%</p>', 302, NOW(), NOW()),
(9, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 1.50%</li><li>비대면 가입고객 우대금리 최대 0.20%</li><li>금융자산정보 조회 동의(대면채널에 한함) 최대 0.10%</li><li>성실상환고객<sup>주) </sup>감면금리: 최대 0.60%<br>(연 1회 0.30%이며,최대 2회까지 최대 0.60%p 범위내 감면 가능)</li></ul><div>주) 대출 취급 후 해당 기간동안 연체 없는 고객</div>', 303, NOW(), NOW()),
(9, 'RATE:최종금리', '- 최저 연 5.75% ~ 최고 연 9.45% (2026.04.16 신잔액기준 COFIX :2.45%)<br>(최저금리는 신잔액 COFIX 변동금리 적용, 가산금리 미적용 및 최대 우대금리 1.70% 모두 적용하는 경우. 단, 성실상환 고객 감면금리 미적용)<br>(최고금리는 신잔액 COFIX 변동금리 적용, 가산금리 적용 및 우대금리 미적용 하는 경우)<br>* 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.', 304, NOW(), NOW()),
(9, 'DOC:여신거래 기본약관', '/terms/p9/0.pdf', 500, NOW(), NOW()),
(9, 'DOC:가계대출 상품설명서', '/terms/p9/1.pdf', 501, NOW(), NOW()),
(9, 'DOC:상품공시자료', '/terms/p9/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(9, 'PREF_9_1', '비대면 가입', 0.2, '비대면(인터넷·모바일) 가입 고객', NOW(), NOW());

-- [10] BNK 징검다리론 (서민금융)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(10, 'BNK 징검다리론', 9, '6개월 ~ 5년(거치 최대 1년)', 'SALE', '서민금융', '0300018407', '정책서민금융상품 성실상환자 맞춤대출', '6.2', '9', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(10, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(10, 'LOAN_LIMIT', '30백만원', 5, NOW(), NOW()),
(10, 'LOAN_TERM', '6개월 ~ 5년(거치 최대 1년)', 6, NOW(), NOW()),
(10, 'TARGET', '정책 서민금융상품 이용고객 중 성실 상환한 자', 7, NOW(), NOW()),
(10, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(10, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(10, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(10, 'OPT_TERMS', '6개월,1년,2년,3년,5년', 11, NOW(), NOW()),
(10, 'OPT_REPAYMENTS', '[{"method":"원리금균등상환","minM":6,"maxM":60,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":60,"minIncl":false}]', 12, NOW(), NOW()),
(10, 'INFO:상품개요', '금융당국의 서민금융 지원강화 방안에 따라 정책서민금융상품을 성실상환한 고객이 자금지원의 공백 없이 은행권 신용대출을 이용할 수 있도록 은행권 공동으로 출시한 서민금융 정책상품', 100, NOW(), NOW()),
(10, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">정책서민금융상품<sup>주1)</sup>을 6개월 이상 이용 후 최근 3년 내 완제 또는 2년 이상 거래중이며, 서민금융진흥원의 서민특화 신용평가모형 심사를 통해 선별된 자에 한함.&nbsp;소득구분별상세 자격요건은 다음과 같음</p><table class="tbl-matrix" cellspacing="0" cellpadding="0" ><tbody><tr><th rowspan="2" ><p class="ssp-editor-p">구분</p></th><th  colspan="3"><p class="ssp-editor-p">소득구분</p></th></tr><tr><th class="" ><p class="ssp-editor-p">급여소득자</p></th><th class="" ><p class="ssp-editor-p">개인사업자</p></th><th class="" ><p class="ssp-editor-p">연금소득자<sup>주2)</sup></p></th></tr><tr><th ><p class="ssp-editor-p">재직(사업)기간</p></th><td ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td ><p class="ssp-editor-p" style="text-align: center">3개월 이상</p></td><td ><p class="ssp-editor-p" style="text-align: center">연금 1회 이상 수령</p></td></tr><tr><th ><p class="ssp-editor-p">연소득</p></th><td  colspan="3"><p class="ssp-editor-p" style="text-align: center">연소득 50백만원 이하인 자</p></td></tr></tbody></table><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">주1) 새희망홀씨, 근로자햇살론, 햇살론유스·뱅크·15·17, 최저신용자 특례보증, 미소금융</p><p class="ssp-editor-p"><span style="font-size: 100%">주</span><span style="font-family: Pretendard; font-size: 100%">2)&nbsp;</span><span style="font-size: 100%">비대면 채널 전용 상품으로 국민연금 수령자 限</span><span style="font-size: 100%">&nbsp;</span></p>', 101, NOW(), NOW()),
(10, 'INFO:대출한도', '심사결과에 따라 최소 1백만원 ~ 최대 30백만원', 102, NOW(), NOW()),
(10, 'INFO:상환방법 및 대출기간', '<p>원리금균등상환 : 6개월 이상 ~ 5년 이하<br>원금균등상환 : 1년 초과 ~ 5년 이하<br>※ 대출기간 1년 초과 시 거치기간 선택 가능하며, 대출기간의 1/3 범위 내에서 최대 1년까지 거치기간 운용 가능<br>※ 이자부과시기 : 매월 후취(대출 해당일 또는 응당일 부과)<br>※ 휴일 대출 원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(10, 'INFO:금리변동주기', '  3개월,  6개월', 104, NOW(), NOW()),
(10, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 105, NOW(), NOW()),
(10, 'INFO:기본금리', '<p>기준금리 + 6.50%</p>', 106, NOW(), NOW()),
(10, 'INFO:가산금리', '<p><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>신용평점에 따라 최대 1.00%</p><p class="ssp-editor-p"><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>당행 내부등급에 따라 최대 1.00%</p>', 107, NOW(), NOW()),
(10, 'INFO:우대금리', '<p>* 기본 감면금리</p><p class="ssp-editor-p">&nbsp;&nbsp; 당행 급여이체 0.30%</p><p class="ssp-editor-p">&nbsp; &nbsp;연소득 3천만원 이하 0.50%</p><p class="ssp-editor-p">* 신용평점에 따라 최대 1.50%</p><p class="ssp-editor-p">* 당행 내부등급에 따라 최대 0.50%</p><p class="ssp-editor-p"><span style="font-size: 100%">* 성실상환고객 감면금리 : 최대 1.00%( 1회 감면금리, 최초 대출취급 후 1년간 연체가 없는 경우 적용하며 최대 2회까지 적용)</span></p>', 108, NOW(), NOW()),
(10, 'INFO:최종금리', '최저 연 6.19 % ~최고 연 9.00 % (2026-06-04 신 잔액기준 (COFIX) : 2.49 %)
(최저금리는 신잔액 COFIX 변동금리 적용, 최대 우대금리 모두 적용 및 가산금리 미적용하는 경우이며, 최고금리는 상품별 최고금리 연 9.00% 초과 불가. 단, 성실상환고객 감면금리 미적용)
※ 최종금리는 기본금리에 가산금리 및 감면금리를 가감하여 산정됩니다.
※ 상기 적용금리에도 불구하고 신규 취급 시 대출금리는 최고 연 9.00%를 초과할 수 없습니다.', 109, NOW(), NOW()),
(10, 'INFO:담보조건', '무보증 신용대출', 110, NOW(), NOW()),
(10, 'INFO:가입방법', '모바일뱅킹(개인)', 111, NOW(), NOW()),
(10, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p"><span>※ 수입인지대금 : 5천 만원 초과시 대출금액별 수입인지비용 차등 부과(50% 균등부담)</span></p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" ><b>대출금액</b></th><th colspan="2" ><b>인지세액</b></th></tr><tr><th ><b>고객</b></th><th ><b>은행</b></th></tr></thead><tbody><tr><td ><p class="ssp-editor-p" style="text-align: center">5천만원 초과1억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">3만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">1억원 초과10억원 이하</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">7만5천원</p></td></tr><tr><td ><p class="ssp-editor-p" style="text-align: center">10억원 초과</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td><td ><p class="ssp-editor-p" style="text-align: center">17만5천원</p></td></tr></tbody></table></div>', 112, NOW(), NOW()),
(10, 'INFO:중도상환수수료', '면제', 113, NOW(), NOW()),
(10, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(10, 'INFO:필요서류', '<p class="ssp-editor-p">공통: 본인확인서류(주민등록증, 운전면허증 등), 주민등록등본 , 건강보험자격득실확인서(개인사업자인 경우 사업자등록증 사본)</p><p class="ssp-editor-p"><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span><span style="font-size: 100%">급여소득자 </span><span style="font-size: 100%">(</span><span style="font-size: 100%">택일</span><span style="font-size: 100%">)</span></p><p class="ssp-editor-p">&nbsp; &nbsp;최근년도 연말정산용 근로소득 원천징수영수증</p><p class="ssp-editor-p"><span style="font-size: 100%">&nbsp; &nbsp;세무서</span><span style="font-size: 100%">(</span><span style="font-size: 100%">홈택스</span><span style="font-size: 100%">) </span><span style="font-size: 100%">발급</span><span style="font-size: 100%"> </span><span style="font-size: 100%">소득금액증명원</span><span style="font-size: 100%"> </span><span style="font-size: 100%">등</span></p><p class="ssp-editor-p"><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>개인사업자 (택일)</p><p class="ssp-editor-p">&nbsp; &nbsp;세무서(홈택스)발급 소득금액증명원</p><p class="ssp-editor-p">&nbsp; &nbsp;최근년도 연말정산용 사업소득원천징수 영수증 등</p><p class="ssp-editor-p"><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>연금소득자 (택일)</p><p class="ssp-editor-p">&nbsp; &nbsp;국민연금공단 발급 연금정산용가입내역확인서</p><p class="ssp-editor-p">&nbsp; &nbsp;국민연금공단 발급 국민연금 지급내역서 등</p><p>※ 조건에 따라 추가 서류가 필요할 수 있습니다.</p><p class="ssp-editor-p">※ 모바일 대출심사 시 공인인증서가 있는 스마트폰에서 공인인증서 비밀번호 입력으로 자동 제출이 가능합니다.</p>', 115, NOW(), NOW()),
(10, 'INFO:마이너스통장(종합통장대출)가능여부', '부', 116, NOW(), NOW()),
(10, 'INFO:기타', '<p class="ssp-editor-p">※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p><ul class="info-list type-li-dot"><li>비대면채널 제한사항&nbsp;<ul class="info-list type-li-dash-ed"><li>비대면 채널 신용대출 신청 시 1일 총 7회, 30일 내 총 20회까지 신청 가능합니다.</li><li>비대면 채널 신용대출 실행 시 1일 최대 1건, 1인 최대 7건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출 진행이 불가합니다.</li></ul></li></ul>', 117, NOW(), NOW()),
(10, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.<br>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(10, 'RATE:기본금리', '<p>기준금리 + 6.50%</p>', 301, NOW(), NOW()),
(10, 'RATE:가산금리', '<p><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>신용평점에 따라 최대 1.00%</p><p class="ssp-editor-p"><span style="font-family: Pretendard; font-size: 100%">*&nbsp;</span>당행 내부등급에 따라 최대 1.00%</p>', 302, NOW(), NOW()),
(10, 'RATE:우대금리', '<p>* 기본 감면금리</p><p class="ssp-editor-p">&nbsp;&nbsp; 당행 급여이체 0.30%</p><p class="ssp-editor-p">&nbsp; &nbsp;연소득 3천만원 이하 0.50%</p><p class="ssp-editor-p">* 신용평점에 따라 최대 1.50%</p><p class="ssp-editor-p">* 당행 내부등급에 따라 최대 0.50%</p><p class="ssp-editor-p"><span style="font-size: 100%">* 성실상환고객 감면금리 : 최대 1.00%( 1회 감면금리, 최초 대출취급 후 1년간 연체가 없는 경우 적용하며 최대 2회까지 적용)</span></p>', 303, NOW(), NOW()),
(10, 'RATE:최종금리', '최저 연 6.19 % ~최고 연 9.00 % (2026-06-04 신 잔액기준 (COFIX) : 2.49 %)<br>(최저금리는 신잔액 COFIX 변동금리 적용, 최대 우대금리 모두 적용 및 가산금리 미적용하는 경우이며, 최고금리는 상품별 최고금리 연 9.00% 초과 불가. 단, 성실상환고객 감면금리 미적용)<br>※ 최종금리는 기본금리에 가산금리 및 감면금리를 가감하여 산정됩니다.<br>※ 상기 적용금리에도 불구하고 신규 취급 시 대출금리는 최고 연 9.00%를 초과할 수 없습니다.', 304, NOW(), NOW()),
(10, 'DOC:여신거래 기본약관', '/terms/p10/0.pdf', 500, NOW(), NOW()),
(10, 'DOC:가계대출 상품설명서', '/terms/p10/1.pdf', 501, NOW(), NOW()),
(10, 'DOC:상품공시자료', '/terms/p10/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(10, 'PREF_10_1', '급여이체', 0.3, '당행 급여이체 시', NOW(), NOW());

-- [11] 돌아와요 부산항에 청년 신용대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(11, '돌아와요 부산항에 청년 신용대출', 2.65, '6개월 ~ 12개월', 'SALE', '신용대출', '0300013204', '수도권에서 부산·울산·경남으로 전입 &취업한 청년급여소득자 전용신용대출', '2.65', '2.65', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(11, 'BASE_RATE_RAW', '2.65', 4, NOW(), NOW()),
(11, 'LOAN_LIMIT', '10백만원', 5, NOW(), NOW()),
(11, 'LOAN_TERM', '6개월 ~ 12개월', 6, NOW(), NOW()),
(11, 'TARGET', '대출 신청일 기준 최근 1년 내 수도권에서 거주하다 부산·울산·경남으로 전입 및 취업 완료 한 청년(만 19세 ~ 만 45세) 급여소득자 中 아래 요건을 충족하는 자 1) 연소득 기준 : 60백만원 이하
2) 재직 기간 : 1개월 이상
3) CB 평점 : NICE 745점 및 KCB 690점 이상', 7, NOW(), NOW()),
(11, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(11, 'OPT_RATE_TYPE', '고정금리', 9, NOW(), NOW()),
(11, 'OPT_TERMS', '6개월,1년', 11, NOW(), NOW()),
(11, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true}]', 12, NOW(), NOW()),
(11, 'INFO:상품개요', '수도권에서 거주하다 부산·울산·경남지역으로 전입 및 취업 완료한 청년 급여소득자 대상 저금리 포용금융 상품', 100, NOW(), NOW()),
(11, 'INFO:대출조건(자격)', '<p>대출 신청일 기준 최근 1년&nbsp;내 수도권에서 거주하다 부산·울산·경남지역으로 전입 및 취업 완료 한 청년(만 19세 ~ 만 45세) 급여소득자 中 아래 요건을 충족하는 자<br><br>1) 연소득 기준 : 60백만원 이하<br>2) 재직 기간 : 1개월 이상<br>3) CB 평점 : NICE 745점 및 KCB 690점 이상</p>', 101, NOW(), NOW()),
(11, 'INFO:대출한도', '최대 10백만원', 102, NOW(), NOW()),
(11, 'INFO:상환방법 및 대출기간', '만기일시상환 : 6개월이상 ~ 12개월이하
이자부과시기 : 매월 후취(대출 해당일 또는 응당일 부과)
※ 휴일 대출원금 또는 이자상환 가능', 103, NOW(), NOW()),
(11, 'INFO:기준금리', '고정금리', 104, NOW(), NOW()),
(11, 'INFO:최종금리', '최저 연 2.65 % ~최고 연 2.65 % (2026.04.14 현재)

※ 단, 최초 취급후 3년 경과 후 산출금리(변동) 적용
1) 기준금리 : 신잔액 COFIX
2) 금리변동주기 : 3개월, 6개월, 12개월
3) 본부 금리 감면 불가', 105, NOW(), NOW()),
(11, 'INFO:담보조건', '무보증 신용대출', 106, NOW(), NOW()),
(11, 'INFO:가입방법', ' 모바일뱅킹(개인)', 107, NOW(), NOW()),
(11, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p"><span>※ 수입인지대금 : 5천 만원 초과시 대출금액별 수입인지비용 차등 부과(50% 균등부담)</span></p><p class="ssp-editor-p">&nbsp;</p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" ><b>대출금액</b></th><th colspan="2" ><b>인지세액</b></th></tr><tr><th ><b>고객</b></th><th ><b>은행</b></th></tr></thead><tbody><tr><td ><p class="ssp-editor-p">5천만원 초과1억원 이하</p></td><td ><p class="ssp-editor-p">3만5천원</p></td><td ><p class="ssp-editor-p">3만5천원</p></td></tr><tr><td ><p class="ssp-editor-p">1억원 초과10억원 이하</p></td><td ><p class="ssp-editor-p">7만5천원</p></td><td ><p class="ssp-editor-p">7만5천원</p></td></tr><tr><td ><p class="ssp-editor-p">10억원 초과</p></td><td ><p class="ssp-editor-p">17만5천원</p></td><td ><p class="ssp-editor-p">17만5천원</p></td></tr></tbody></table></div>', 108, NOW(), NOW()),
(11, 'INFO:중도상환수수료', '면제', 109, NOW(), NOW()),
(11, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 110, NOW(), NOW()),
(11, 'INFO:필요서류', '<p class="ssp-editor-p">1.&nbsp; 본인 확인서류 : 신분증 (주민등록증, 운전면허증 등)</p><p class="ssp-editor-p">2. 재직 확인서류 : 재직증명서(회사 날인)</p><p class="ssp-editor-p">3. 소득 증빙서류 :&nbsp;<span style="font-family: Pretendard; font-size: 100%">급여명세서</span><span style="font-family: Pretendard; font-size: 100%">(</span><span style="font-family: Pretendard; font-size: 100%">회사날인</span><span style="font-family: Pretendard; font-size: 100%">), </span><span style="font-family: Pretendard; font-size: 100%">갑근세원천징수증명서</span><span style="font-family: Pretendard; font-size: 100%">(</span><span style="font-family: Pretendard; font-size: 100%">회사날인</span><span style="font-family: Pretendard; font-size: 100%">), </span><span style="font-family: Pretendard; font-size: 100%">근로소득원천징수영수증</span><span style="font-family: Pretendard; font-size: 100%">(</span><span style="font-family: Pretendard; font-size: 100%">연말정산용</span><span style="font-family: Pretendard; font-size: 100%">), </span><span style="font-family: Pretendard; font-size: 100%">소득금액증명원</span><span style="font-family: Pretendard; font-size: 100%">(</span><span style="font-family: Pretendard; font-size: 100%">세무서발급</span><span style="font-family: Pretendard; font-size: 100%">) </span><span style="font-family: Pretendard; font-size: 100%">등</span></p><p class="ssp-editor-p">4. 거주 확인서류 : 주민등록초본 (전체 주소변동이력 포함)</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※&nbsp;<span style="font-size: 100%">심사 시 대상자격 요건 확인을 위해 재직확인 및 소득 증빙서류는고객님께서 사진촬영을 통해 직접 업로드 해주셔야 하며</span><span style="font-size: 100%">,</span></p><p class="ssp-editor-p">거주확인 서류는 공공마이데이터 또는 스크래핑을 통해 모바일서류제출이 가능합니다.</p>', 111, NOW(), NOW()),
(11, 'INFO:마이너스통장(종합통장대출)가능여부', '부', 112, NOW(), NOW()),
(11, 'INFO:유의사항', '<p class="ssp-editor-p">1.&nbsp; 본 상품은 취급 후 3년간 고정금리 적용되며, 3년 경과 후 기한연기 시 변동금리로 전환되므로 이 점 유의하시기 바랍니다.</p><p class="ssp-editor-p">2. [선택] 개인정보 제3자 제공 동의서(돌아와요 부산항에 청년 신용대출 대상자 설문조사 기초자료 활용) 징구에 동의하신 고객님께서는 대출 후 3개월 내에 (재)청년재단에서 대출 효과 분석에 따른 설문조사 진행이&nbsp;있는 바 이 점 참고하시기 바랍니다.</p>', 113, NOW(), NOW()),
(11, 'RATE:기준금리', '고정금리', 300, NOW(), NOW()),
(11, 'RATE:최종금리', '최저 연 2.65 % ~최고 연 2.65 % (2026.04.14 현재)<br><br>※ 단, 최초 취급후 3년 경과 후 산출금리(변동) 적용<br>1) 기준금리 : 신잔액 COFIX<br>2) 금리변동주기 : 3개월, 6개월, 12개월<br>3) 본부 금리 감면 불가', 301, NOW(), NOW()),
(11, 'DOC:여신거래 기본약관', '/terms/p11/0.pdf', 500, NOW(), NOW()),
(11, 'DOC:가계대출 상품설명서', '/terms/p11/1.pdf', 501, NOW(), NOW()),
(11, 'DOC:돌아와요 부산항에 청년 신용대출 공시자료', '/terms/p11/2.pdf', 502, NOW(), NOW());

-- [12] 비상금 동백론 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(12, '비상금 동백론', 9.8, '6개월~3년', 'SALE', '신용대출', '0300000744', '동백전 이용 고객을 위한 비상금 대출', '4.9', '9.8', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(12, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(12, 'LOAN_LIMIT', '3백만원', 5, NOW(), NOW()),
(12, 'LOAN_TERM', '6개월~3년', 6, NOW(), NOW()),
(12, 'TARGET', '가처분소득 1천만원 이상, 고위험업권 대출1건 이하', 7, NOW(), NOW()),
(12, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(12, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(12, 'OPT_RATE_CYCLES', '3,6,12', 10, NOW(), NOW()),
(12, 'OPT_TERMS', '6개월,1년,2년,3년', 11, NOW(), NOW()),
(12, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":36,"minIncl":false},{"method":"원리금균등상환","minM":6,"maxM":36,"minIncl":true}]', 12, NOW(), NOW()),
(12, 'INFO:상품개요', '<p>동백전 회원을 위한 사전승인 무보증 신용대출 상품<br></p>', 100, NOW(), NOW()),
(12, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>아래 요건을 모두 충족하는 경우<ul class="info-list type-li-dash-ed"><li>나이 19세 이상</li><li>가처분소득 1천만원 이상</li><li>NICE 745점 이상, KCB 690점 이상 모두 충족</li><li>고위험업권(저축은행, 캐피탈, 대부업) 신용대출 1건 이하 보유</li></ul></li></ul>', 101, NOW(), NOW()),
(12, 'INFO:대출한도', '<p>사전승인 심사기준에 따라 최대 3백만원 이내<br></p>', 102, NOW(), NOW()),
(12, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식(종합통장대출(마이너스통장) 포함)<ul class="info-list type-li-dash-ed"><li>6개월 이상~1년 이내</li></ul></li><li>원금균등 상환방식<ul class="info-list type-li-dash-ed"><li>1년 초과 ~ 3년 이내</li></ul></li><li>원리금균등 상환방식<ul class="info-list type-li-dash-ed"><li>6개월 이상 ~ 3년 이내</li></ul></li><li>이자부과시기<ul class="info-list type-li-dash-ed"><li>매월 후취(대출 해당일 또는 응당일 부과)</li></ul></li></ul><p><span style="font-size: 100%">※ 휴일 대출원금 또는 이자 상환 가능</span></p>', 103, NOW(), NOW()),
(12, 'INFO:금리변동주기', '<p>3개월, 6개월, 12개월<br></p>', 104, NOW(), NOW()),
(12, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(12, 'INFO:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 5.50%</p>', 106, NOW(), NOW()),
(12, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>신용평가회사(CB사) 신용등급에 따라 최대 0.80%p</li><li>마이너스통장대출 가산금리 0.50%p</li><li>고위험업권(캐피탈, 저축은행, 대부업) 1건 보유 0.50%p</li></ul>', 107, NOW(), NOW()),
(12, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 0.80%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.50%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>거래실적연동 옵션 감면금리 최대 0.80%<ul class="info-list type-li-dash-ed"><li>급여(연금) 및 가맹점 결제대금(요양급여 포함) 자동이체: 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체(아파트관리비, 공과금, 통신요금) 건수: 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준: 0.20%<ul class="info-list type-li-gt"><li>매3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우: 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우: 0.20%</li></ul></li><li>주택청약종합저축 자동이체: 0.10% <ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우<br></li></ul></li></ul></li><li>우수고객 우대금리 최대 0.40%<ul class="info-list type-li-dash-ed"><li>수신 거래: 0.30%</li><li>기타 거래: 0.10%</li><li>사전승인 재이용 고객: 0.10% </li></ul></li><li>동백전 거래실적 우대금리 최대 0.60%<ul class="info-list type-li-dash-ed"><li>동백패스 이용: 0.30%</li><li>동백전카드 유실적: 0.20%</li><li>웰컴동백(대출 신규 고객): 0.10%</li></ul></li></ul><p><span style="font-size: 100%">※ 취급 시 대출금리 10% 이상 적용 불가</span></p>', 108, NOW(), NOW()),
(12, 'INFO:최종금리', '최저 연 4.94 % ~최고 연 9.84 % (2025-09-24 신 잔액기준 (COFIX) : 2.54 %)', 109, NOW(), NOW()),
(12, 'INFO:담보조건', '<p>무보증 신용</p>', 110, NOW(), NOW()),
(12, 'INFO:가입방법', '<p>영업점, 모바일<br></p>', 111, NOW(), NOW()),
(12, 'INFO:수수료(부대비용)', '<p>해당사항없음</p>', 112, NOW(), NOW()),
(12, 'INFO:중도상환수수료', '<p>면제</p>', 113, NOW(), NOW()),
(12, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다.&nbsp;단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지&nbsp;아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(12, 'INFO:필요서류', '<p>본인 확인서류(주민등록증, 운전면허증 등)<br></p>', 115, NOW(), NOW()),
(12, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>여</p>', 116, NOW(), NOW()),
(12, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(12, 'RATE:기본금리', '기준금리(신 잔액기준 (COFIX) ) + 5.50%', 301, NOW(), NOW()),
(12, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>신용평가회사(CB사) 신용등급에 따라 최대 0.80%p</li><li>마이너스통장대출 가산금리 0.50%p</li><li>고위험업권(캐피탈, 저축은행, 대부업) 1건 보유 0.50%p</li></ul>', 302, NOW(), NOW()),
(12, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라 최대 0.80%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.50%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>거래실적연동 옵션 감면금리 최대 0.80%<ul class="info-list type-li-dash-ed"><li>급여(연금) 및 가맹점 결제대금(요양급여 포함) 자동이체: 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체(아파트관리비, 공과금, 통신요금) 건수: 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준: 0.20%<ul class="info-list type-li-gt"><li>매3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우: 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우: 0.20%</li></ul></li><li>주택청약종합저축 자동이체: 0.10% <ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우<br></li></ul></li></ul></li><li>우수고객 우대금리 최대 0.40%<ul class="info-list type-li-dash-ed"><li>수신 거래: 0.30%</li><li>기타 거래: 0.10%</li><li>사전승인 재이용 고객: 0.10% </li></ul></li><li>동백전 거래실적 우대금리 최대 0.60%<ul class="info-list type-li-dash-ed"><li>동백패스 이용: 0.30%</li><li>동백전카드 유실적: 0.20%</li><li>웰컴동백(대출 신규 고객): 0.10%</li></ul></li></ul><p><span style="font-size: 100%">※ 취급 시 대출금리 10% 이상 적용 불가</span></p>', 303, NOW(), NOW()),
(12, 'RATE:최종금리', '최저 연 4.94 % ~최고 연 9.84 % (2025-09-24 신 잔액기준 (COFIX) : 2.54 %)', 304, NOW(), NOW()),
(12, 'DOC:여신거래 기본약관', '/terms/p12/0.pdf', 500, NOW(), NOW()),
(12, 'DOC:가계대출 상품설명서', '/terms/p12/1.pdf', 501, NOW(), NOW()),
(12, 'DOC:상품공시자료', '/terms/p12/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(12, 'PREF_12_1', '급여(연금)·가맹점 자동이체', 0.3, '매 3개월간 2회 이상 건당 50만원 이상 급여 입금 시', NOW(), NOW()),
(12, 'PREF_12_2', '공과금 자동이체(8건)', 0.1, '매 3개월간 8건 이상 자동이체 시', NOW(), NOW()),
(12, 'PREF_12_3', '예금 평균잔액', 0.2, '매 3개월간 예금평잔 150만원(요구불 100만원) 이상 시', NOW(), NOW()),
(12, 'PREF_12_4', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW()),
(12, 'PREF_12_5', '주택청약종합저축 자동이체', 0.1, '매 3개월간 2회 이상 건당 10만원 이상 자동이체 시', NOW(), NOW()),
(12, 'PREF_12_6', '동백패스 이용', 0.3, '동백패스 이용 시', NOW(), NOW()),
(12, 'PREF_12_7', '동백전카드 유실적', 0.2, '동백전카드 사용실적 보유 시', NOW(), NOW()),
(12, 'PREF_12_8', '웰컴동백(대출 신규)', 0.1, '대출 신규 고객', NOW(), NOW());

-- [13] BNK Welcome Global 대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(13, 'BNK Welcome Global 대출', 15, '3개월 ~ 3년(상환방식에 따라 다름)', 'SALE', '신용대출', '0300000184', '외국인 근로자 전용 신용대출', '7.9', '15', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(13, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(13, 'LOAN_LIMIT', '3천만원(최소 5백만원)', 5, NOW(), NOW()),
(13, 'LOAN_TERM', '3개월 ~ 3년(상환방식에 따라 다름)', 6, NOW(), NOW()),
(13, 'TARGET', '외국인 급여소득자', 7, NOW(), NOW()),
(13, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(13, 'OPT_RATE_TYPE', '신규취급액기준 COFIX', 9, NOW(), NOW()),
(13, 'OPT_RATE_CYCLES', '3,6,12', 10, NOW(), NOW()),
(13, 'OPT_TERMS', '6개월,1년,2년,3년', 11, NOW(), NOW()),
(13, 'OPT_REPAYMENTS', '[{"method":"원금균등상환","minM":12,"maxM":36,"minIncl":false},{"method":"원리금균등상환","minM":3,"maxM":36,"minIncl":true}]', 12, NOW(), NOW()),
(13, 'INFO:상품개요', '<p class="ssp-editor-p">외국인 근로자 전용 신용대출 상품</p>', 100, NOW(), NOW()),
(13, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">대출 신청일 현재 체류자격 E-9(비전문취업), E-7(특정활동), F-2(거주), F-4(재외동포), F-5(영주), F-6(결혼이민)으로 국내에 거주 중인 외국인급여소득자로 상세자격기준은 다음과 같음&nbsp;</p><div class="table-box"><table class="tbl-matrix"><colgroup><col><col></colgroup><tbody><tr><th scope="" style="border-left: 0px none rgb(0, 0, 0); border-bottom: 2px solid rgb(240, 240, 240); background-color: rgb(247, 247, 247)"><p class="ssp-editor-p">구분</p></th><td scope="" style="border-right: 2px solid rgb(97, 97, 97); border-bottom: 2px solid rgb(240, 240, 240); background-color: rgb(247, 247, 247)"><p class="ssp-editor-p" style="text-align: center">내용</p></td></tr><tr><th scope="" style="border-left: 0px none rgb(0, 0, 0); border-right: 2px solid rgb(240, 240, 240); border-top: 0px none rgb(0, 0, 0); background-color: rgb(255, 255, 255)"><p class="ssp-editor-p">국적</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">베트남, 인도네시아, 우즈베키스탄, 필리핀,&nbsp;</p><p class="ssp-editor-p" style="text-align: center">스리랑카, 태국, 미얀마, 캄보디아, 네팔,&nbsp;</p><p class="ssp-editor-p" style="text-align: center">몽골, 중국, 카자흐스탄</p></td></tr><tr><th scope="" style="border-left: 0px none rgb(0, 0, 0); border-right: 2px solid rgb(240, 240, 240); border-bottom: 2px solid rgb(240, 240, 240); background-color: rgb(255, 255, 255)"><p class="ssp-editor-p">재직기간</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center"><span>現 직장기준 재직기간 3개월 이상</span></p></td></tr><tr><th class="" style="border-right: 2px solid rgb(240, 240, 240); border-bottom: 2px solid rgb(240, 240, 240); border-left: 0px none rgb(0, 0, 0); background-color: rgb(255, 255, 255)"><p class="ssp-editor-p">연소득</p></th><td class="" ><p class="ssp-editor-p" style="text-align: center"><span><span>18백만원 이상</span><br></span></p></td></tr><tr><th class="" style="border-right: 2px solid rgb(240, 240, 240); border-bottom: 2px solid rgb(240, 240, 240); border-left: 0px none rgb(0, 0, 0); background-color: rgb(255, 255, 255)"><p class="ssp-editor-p">잔여 체류기간</p></th><td class="" ><p class="ssp-editor-p" style="text-align: center"><span>3개월 이상</span></p></td></tr></tbody></table></div>', 101, NOW(), NOW()),
(13, 'INFO:대출한도', '<p class="ssp-editor-p">심사결과에 따라 최소 5백만원 ~ 최대 30백만원</p>', 102, NOW(), NOW()),
(13, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>원금균등 상환방식대출: 1년 초과 ~ 3년 이내</li><li>원리금균등 상환방식대출: 3개월 이상 ~ 3년 이내</li><li>이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p><span style="font-size: 100%">※ 휴일 대출원금 또는 이자 상환 가능</span></p>', 103, NOW(), NOW()),
(13, 'INFO:금리변동주기', '<p class="ssp-editor-p">3, 6, 12개월 중 선택</p>', 104, NOW(), NOW()),
(13, 'INFO:기준금리', '신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(13, 'INFO:기본금리', '<p>기준금리 + 12.00%</p>', 106, NOW(), NOW()),
(13, 'INFO:가산금리', '<p class="ssp-editor-p">신용등급에 따라 최대 연 1.50%</p>', 107, NOW(), NOW()),
(13, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>거래실적 우대금리 최대 연 1.00%<ul class="info-list type-li-dash-ed"><li><span>급여 입금: 0.50%</span></li><li><span>외환 환전송금 실적: 0.50%</span></li></ul></li><li><span>신용등급에 따라 최대 연 1.50%</span></li><li><span>타 금융기관 대환신청 우대금리 연 2.00%</span></li><li><span>MGM(소개마케팅) 우대금리 최대 연 1.50%</span></li><li>협약기업체 우대금리 최대 연 1.00%</li></ul>', 108, NOW(), NOW()),
(13, 'INFO:최종금리', '최저 연 7.81 % ~최고 연 15.00 % (2026.04.27 기준 신규취급액기준 COFIX : 2.81 %)
(최저금리는 최대 우대금리 적용 및 가산금리 미적용하는 경우)
※ 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.
※ 최종금리는 연 15% 초과 적용 불가합니다.', 109, NOW(), NOW()),
(13, 'INFO:담보조건', '<p class="ssp-editor-p">신용</p>', 110, NOW(), NOW()),
(13, 'INFO:가입방법', '영업점,모바일뱅킹(개인)', 111, NOW(), NOW()),
(13, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li>인지세<ul class="info-list type-li-dash-ed"><li><span>「인지세법」에 따라 대출약정 시 납부하는 세금으로 대출금액에 따라 세액이 차등 적용되며, 은행과 고객이 각각 50% 부담</span><div class="table-box"><table class="tbl-matrix" ><tbody><tr><th scope="" ><p class="ssp-editor-p">대출금액</p></th><th scope="" ><p class="ssp-editor-p">5천만원 이하</p></th><th scope="" ><p class="ssp-editor-p">5천만원 초과</p><p class="ssp-editor-p">~ 1억원 이하</p></th><th scope="" ><p class="ssp-editor-p">1억원 초과</p><p class="ssp-editor-p">~ 10억원 이하</p></th><th scope="" ><p class="ssp-editor-p">10억원 초과</p></th></tr><tr><td scope="" ><p class="ssp-editor-p" style="text-align: center">인지세액</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">비과세</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">7만원</p><p class="ssp-editor-p" style="text-align: center">(각각3만5천원)</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">15만원</p><p class="ssp-editor-p" style="text-align: center">(각각7만5천원)</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">35만원</p><p class="ssp-editor-p" style="text-align: center">(각각17만5천원)</p></td></tr></tbody></table></div></li></ul></li></ul>', 112, NOW(), NOW()),
(13, 'INFO:중도상환수수료', '대출기간 중 대출금의 전부 또는 일부를 상환하는 경우 은행에서 정한 기준에 따라 중도상환수수료를 부담하셔야 합니다.
* 중도상환금액 X 수수료율(0.33%) X 대출잔여일수/대출기간', 113, NOW(), NOW()),
(13, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다.&nbsp;단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지&nbsp;아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.&nbsp;</li></ul>', 114, NOW(), NOW()),
(13, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>본인 확인서류(외국인등록증)</li><li><span>재직 확인서류(건강보험자격득실확인서, 재직증명서 등)</span></li><li><span>소득 확인서류(근로소득원천징수영수증, 소득금액증명원, 급여명세표 등)</span></li><li><span>기타 확인서류(표준금로계약서, 외국인 고용허가서, 금융거래확인서 등)</span></li></ul>', 115, NOW(), NOW()),
(13, 'INFO:마이너스통장(종합통장대출)가능여부', '<p class="ssp-editor-p">부</p>', 116, NOW(), NOW()),
(13, 'INFO:기타', '<ul class="info-list type-li-dot"><li>비대면 채널 신용대출 신청 시 1일 총 7회, 30일 내 총 20회까지 신청 가능합니다.</li><li>비대면 채널 신용대출 실행 시 1일 최대 1건, 1인 최대 7건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출 진행이 불가합니다.</li></ul><p class="ssp-editor-p">※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p>', 117, NOW(), NOW()),
(13, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(13, 'RATE:기본금리', '<p>기준금리 + 12.00%</p>', 301, NOW(), NOW()),
(13, 'RATE:가산금리', '<p class="ssp-editor-p">신용등급에 따라 최대 연 1.50%</p>', 302, NOW(), NOW()),
(13, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>거래실적 우대금리 최대 연 1.00%<ul class="info-list type-li-dash-ed"><li><span>급여 입금: 0.50%</span></li><li><span>외환 환전송금 실적: 0.50%</span></li></ul></li><li><span>신용등급에 따라 최대 연 1.50%</span></li><li><span>타 금융기관 대환신청 우대금리 연 2.00%</span></li><li><span>MGM(소개마케팅) 우대금리 최대 연 1.50%</span></li><li>협약기업체 우대금리 최대 연 1.00%</li></ul>', 303, NOW(), NOW()),
(13, 'RATE:최종금리', '최저 연 7.81 % ~최고 연 15.00 % (2026.04.27 기준 신규취급액기준 COFIX : 2.81 %)<br>(최저금리는 최대 우대금리 적용 및 가산금리 미적용하는 경우)<br>※ 최종금리는 기본금리에 가산금리 및 우대금리를 가감하여 산정됩니다.<br>※ 최종금리는 연 15% 초과 적용 불가합니다.', 304, NOW(), NOW()),
(13, 'DOC:여신거래 기본약관', '/terms/p13/0.pdf', 500, NOW(), NOW()),
(13, 'DOC:가계대출 상품설명서', '/terms/p13/1.pdf', 501, NOW(), NOW()),
(13, 'DOC:상품공시자료', '/terms/p13/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(13, 'PREF_13_1', '급여 입금', 0.5, '급여 입금 시', NOW(), NOW()),
(13, 'PREF_13_2', '환전·송금 실적', 0.5, '외환 환전·송금 실적 보유 시', NOW(), NOW()),
(13, 'PREF_13_3', '타행 대환 신청', 2, '타 금융기관 대환 신청 시', NOW(), NOW()),
(13, 'PREF_13_4', 'MGM 소개', 1.5, '소개마케팅(MGM) 우대', NOW(), NOW());

-- [14] BNK공무원가계자금대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(14, 'BNK공무원가계자금대출', 5, '6개월 ~ 10년', 'SALE', '신용대출', '0300000359', '공무원연금관리공단 협약대출상품', '3.5', '5', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(14, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(14, 'LOAN_LIMIT', '5천만원', 5, NOW(), NOW()),
(14, 'LOAN_TERM', '6개월 ~ 10년', 6, NOW(), NOW()),
(14, 'TARGET', '공무원연금법의 적용을 받는 재직공무원
(사립학교 교원, 군인은 제외)으로서
공무원 가계자금 융자추천서를 발급한 고객', 7, NOW(), NOW()),
(14, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(14, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(14, 'OPT_RATE_CYCLES', '3,6,12', 10, NOW(), NOW()),
(14, 'OPT_TERMS', '6개월,1년,2년,3년,5년,7년,10년', 11, NOW(), NOW()),
(14, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":120,"minIncl":false},{"method":"원리금균등상환","minM":12,"maxM":120,"minIncl":false}]', 12, NOW(), NOW()),
(14, 'INFO:상품개요', '공무원연금관리공단과 협약에 의해 차주 퇴직시 퇴직사실 통지조건부로 퇴직금의 1/2 범위내에서 생활자금을 지원하는 공무원 특화 대출 상품
', 100, NOW(), NOW()),
(14, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">공무원연금법의 적용을 받는 재직공무원(사립학교 교원, 군인은 제외)으로서 공무원 가계자금 융자추천서를 발급한 고객<br><span style="color: rgb(255, 0, 0)">※&nbsp;</span>공무원 가계자금 융자추천서 발급일로 부터 3영업일 이내까지 유효</p>', 101, NOW(), NOW()),
(14, 'INFO:대출한도', '동일인 기준 최대 50백만원 범위 내에서 다음과 같이 산정
가계자금신용대출 :퇴직금예상액의 1/2범위내에서 최고 50백만원, 융자추천서상의 추천금액 중 적은금액
단기재직자 가계자금신용대출 :재직기간 1년이상~3년미만 5백만원, 3년이상 10백만원, 융자추천서상의 추천금액 중 적은 금액', 102, NOW(), NOW()),
(14, 'INFO:상환방법 및 대출기간', '만기일시상환 : 6개월이상 ~ 1년이내(종합통장대출(마이너스대출) 포함)
이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)
※ 휴일 대출원금 또는 이자 상환 가능<br>원금균등상환 : 1년초과 ~ 10년이내
이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)
※ 휴일 대출원금 또는 이자 상환 가능<br>원리금균등상환 : 1년초과 ~ 10년이내
이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)
※ 휴일 대출원금 또는 이자 상환 가능', 103, NOW(), NOW()),
(14, 'INFO:금리변동주기', '  3개월,  6개월,  12개월', 104, NOW(), NOW()),
(14, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 105, NOW(), NOW()),
(14, 'INFO:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 1.80%</p>', 106, NOW(), NOW()),
(14, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출(마이너스통장)의 경우 0.50%</li><li>내부등급에 따라 최대 0.20%&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</li></ul>', 107, NOW(), NOW()),
(14, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>거래실적옵션 감면금리<ul class="info-list type-li-dash-ed"><li>급여이체 0.20%</li></ul></li><li>내부등급에 따라 최대 0.20% </li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li>&nbsp;부산지역 공무원 특별감면금리 0.40% (특별우대금리 운영 한도 소진 시 적용중단)</li></ul>', 108, NOW(), NOW()),
(14, 'INFO:최종금리', '최저 연 3.47 % ~최고 연 4.97 % (2026-03-17 신 잔액기준 (COFIX) : 2.47 %)
(최저금리는 부산지역 노동조합 협약 대상자(단, 대면 가입 한정)이고, 최대 우대금리 적용 및 가산금리 미적용하는 경우)
(최고금리는 최대 가산금리 적용 및 우대금리 미적용하는 경우)
', 109, NOW(), NOW()),
(14, 'INFO:담보조건', '무보증 신용대출
', 110, NOW(), NOW()),
(14, 'INFO:가입방법', '영업점,모바일뱅킹(개인)', 111, NOW(), NOW()),
(14, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">해당사항없음</p>', 112, NOW(), NOW()),
(14, 'INFO:중도상환수수료', '면제', 113, NOW(), NOW()),
(14, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출장액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생이로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(14, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>본인 확인서류(주민등록증, 운전면허증 등)</li><li>재직 확인서류(건강보험자격득실확인서, 재직증명서 등)</li><li>소득 확인서류(근로소득원천징수영수증,소득금액증명원 등)</li><li>기타 확인서류(금융거래확인서 등)</li><li>모바일 대출심사 시 공인인증서가 있는 스마트폰에서 공인인증서 비밀번호 입력으로 자동제출이 가능 합니다.</li></ul>', 115, NOW(), NOW()),
(14, 'INFO:마이너스통장(종합통장대출)가능여부', '여', 116, NOW(), NOW()),
(14, 'INFO:기타', '<ul class="info-list type-li-dot"><li>비대면대출 제한사항<ul class="info-list type-li-dash-ed"><li>동일상품으로 1일 5회까지 신청 가능하며, 통합하여 1일 7회, 1개월 20회까지 신청 가능합니다.</li><li>동일상품으로 1인 최대 3건까지 실행 가능하며, 통합하여 최대 5건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출진행이 불가합니다.</li></ul></li></ul>', 117, NOW(), NOW()),
(14, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(14, 'RATE:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 1.80%</p>', 301, NOW(), NOW()),
(14, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출(마이너스통장)의 경우 0.50%</li><li>내부등급에 따라 최대 0.20%&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</li></ul>', 302, NOW(), NOW()),
(14, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>거래실적옵션 감면금리<ul class="info-list type-li-dash-ed"><li>급여이체 0.20%</li></ul></li><li>내부등급에 따라 최대 0.20% </li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li>&nbsp;부산지역 공무원 특별감면금리 0.40% (특별우대금리 운영 한도 소진 시 적용중단)</li></ul>', 303, NOW(), NOW()),
(14, 'RATE:최종금리', '최저 연 3.47 % ~최고 연 4.97 % (2026-03-17 신 잔액기준 (COFIX) : 2.47 %)<br>(최저금리는 부산지역 노동조합 협약 대상자(단, 대면 가입 한정)이고, 최대 우대금리 적용 및 가산금리 미적용하는 경우)<br>(최고금리는 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 304, NOW(), NOW()),
(14, 'DOC:여신거래 기본약관', '/terms/p14/0.pdf', 500, NOW(), NOW()),
(14, 'DOC:가계대출 상품설명서', '/terms/p14/1.pdf', 501, NOW(), NOW()),
(14, 'DOC:상품공시자료', '/terms/p14/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(14, 'PREF_14_1', '급여이체', 0.2, '급여이체 실적 시', NOW(), NOW());

-- [15] BNK공무원연금수급권자신용대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(15, 'BNK공무원연금수급권자신용대출', 7.8, '6개월~5년', 'SALE', '신용대출', '0300000020', '공무원연금관리공단 협약대출상품', '5', '7.8', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(15, 'BASE_RATE_RAW', '2.9', 4, NOW(), NOW()),
(15, 'LOAN_LIMIT', '3천만원', 5, NOW(), NOW()),
(15, 'LOAN_TERM', '6개월~5년', 6, NOW(), NOW()),
(15, 'TARGET', '공무원연금관리공단으로부터, 공무원연금 수혜중 고객', 7, NOW(), NOW()),
(15, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(15, 'OPT_RATE_TYPE', '신규취급액기준 COFIX', 9, NOW(), NOW()),
(15, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(15, 'OPT_TERMS', '6개월,1년,2년,3년,5년', 11, NOW(), NOW()),
(15, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원리금균등상환","minM":6,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(15, 'INFO:상품개요', '<p class="ssp-editor-p">공무원연금관리공단과 협약에 의해 공무원 연금 수령자에 대하여 매월 수령연금을 본인계좌에 입금하여 상환하는 조건부로 생활자금을 지원하는 상품입니다.</p>', 100, NOW(), NOW()),
(15, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">공무원연금관리공단(이하 “공단”이라 함)으로부터 공무원 퇴직연금(유족, 장애연금 등 포함)을&nbsp; 1회 이상 당행 본인계좌로 수령중인 고객<br><b>주의) 군인연금법에 의한 “군인 연금수급권자” 및 사립학교 교직원 연금법에 의한 ‘사학연금수급권자’는 대출대상이 아님</b></p>', 101, NOW(), NOW()),
(15, 'INFO:대출한도', '차주별 대출한도는 30백만원 이내로 함
※ 동일상품 기보유 잔액이 있는 경우, 본건 포함 총 한도 30백만원 초과불가 
※ 월 원리금상환금액은 월 연금수령액의 1/2 범위내', 102, NOW(), NOW()),
(15, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식(종합통장대출(마이너스통장) 포함): 6개월 이상 ~ 1년 이내</li><li>원리금균등 상환방식: 6개월 이상 ~ 5년 이내</li><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p"><span style="font-size: 100%">※ 휴일 대출원금 또는 이자 상환 가능</span></p>', 103, NOW(), NOW()),
(15, 'INFO:금리변동주기', '<p class="ssp-editor-p">3개월, 6개월</p>', 104, NOW(), NOW()),
(15, 'INFO:기준금리', '신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(15, 'INFO:기본금리', '<p>기준금리(신규취급액기준 (COFIX) ) + 2.90%</p>', 106, NOW(), NOW()),
(15, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출 0.5%</li><li>신용평점에 따라 최대 1.50%</li></ul>', 107, NOW(), NOW()),
(15, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>거래실적연동 옵션 감면금리<ul class="info-list type-li-dash-ed"><li>연금 자동이체 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체(아파트 관리비,공과금,통신요금)건수 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준 0.20%<ul class="info-list type-li-gt"><li>매3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원이상인 경우</li></ul></li><li>신용카드 회원(신규발급포함)<ul class="info-list type-li-gt"><li>매 3개월간 1백만원 이상 0.10%</li><li>매 3개월간 2백만원 이상 0.20%</li></ul></li></ul></li></ul>', 108, NOW(), NOW()),
(15, 'INFO:최종금리', '최저 연 4.91% ~ 최고 7.71% (2025-12-17 신규취급액기준 COFIX: 2.81%)
(최저금리는  신규취급액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우)
(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 109, NOW(), NOW()),
(15, 'INFO:담보조건', '<p class="ssp-editor-p">무보증 신용대출</p>', 110, NOW(), NOW()),
(15, 'INFO:가입방법', ' 영업점, 모바일뱅킹(개인)', 111, NOW(), NOW()),
(15, 'INFO:중도상환수수료', '대출기간 중 대출금의 전부 또는 일부를 상환하는 경우 은행에서 정한 기준에 따라 중도상환수수료를 부담하셔야 합니다.
- 영업점: 중도상환금액 × 수수료율(0.33%) × 대출잔여일수/대출기간
- 모바일: 면제', 112, NOW(), NOW()),
(15, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 113, NOW(), NOW()),
(15, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>공무원연금 지급 사실 확인서</li><li>주민등록등본 및 가족관계증명서</li><li>연금수령계좌 통장사본</li></ul>', 114, NOW(), NOW()),
(15, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>가능</p>', 115, NOW(), NOW()),
(15, 'INFO:기타', '<p class="ssp-editor-p">※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p>', 116, NOW(), NOW()),
(15, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(15, 'RATE:기본금리', '<p>기준금리(신규취급액기준 (COFIX) ) + 2.90%</p>', 301, NOW(), NOW()),
(15, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출 0.5%</li><li>신용평점에 따라 최대 1.50%</li></ul>', 302, NOW(), NOW()),
(15, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>거래실적연동 옵션 감면금리<ul class="info-list type-li-dash-ed"><li>연금 자동이체 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체(아파트 관리비,공과금,통신요금)건수 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준 0.20%<ul class="info-list type-li-gt"><li>매3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원이상인 경우</li></ul></li><li>신용카드 회원(신규발급포함)<ul class="info-list type-li-gt"><li>매 3개월간 1백만원 이상 0.10%</li><li>매 3개월간 2백만원 이상 0.20%</li></ul></li></ul></li></ul>', 303, NOW(), NOW()),
(15, 'RATE:최종금리', '최저 연 4.91% ~ 최고 7.71% (2025-12-17 신규취급액기준 COFIX: 2.81%)<br>(최저금리는  신규취급액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우)<br>(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 304, NOW(), NOW()),
(15, 'DOC:여신거래 기본약관', '/terms/p15/0.pdf', 500, NOW(), NOW()),
(15, 'DOC:가계대출 상품설명서', '/terms/p15/1.pdf', 501, NOW(), NOW()),
(15, 'DOC:상품공시자료', '/terms/p15/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(15, 'PREF_15_1', '연금 자동이체', 0.3, '매 3개월간 2회 이상 건당 50만원 이상 연금 입금 시', NOW(), NOW()),
(15, 'PREF_15_2', '공과금 자동이체(8건)', 0.1, '매 3개월간 8건 이상 자동이체 시', NOW(), NOW()),
(15, 'PREF_15_3', '예금 평균잔액', 0.2, '매 3개월간 예금평잔 150만원(요구불 100만원) 이상 시', NOW(), NOW()),
(15, 'PREF_15_4', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW());

-- [16] BNK와 함께하는 미래설계대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(16, 'BNK와 함께하는 미래설계대출', 6, '12개월, 36개월', 'SALE', '신용대출', '0300017001', '부산 거주 청년 직장인을 위한 BNK와 함께 만드는 맞춤 금융', '0.1', '6', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(16, 'BASE_RATE_RAW', '0.1', 4, NOW(), NOW()),
(16, 'LOAN_LIMIT', '1천만원', 5, NOW(), NOW()),
(16, 'LOAN_TERM', '12개월, 36개월', 6, NOW(), NOW()),
(16, 'TARGET', '부산 거주 및 부산 소재 직장에
재직중인 청년 직장인', 7, NOW(), NOW()),
(16, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(16, 'OPT_RATE_TYPE', '고정금리', 9, NOW(), NOW()),
(16, 'OPT_RATE_CYCLES', '6,12', 10, NOW(), NOW()),
(16, 'OPT_TERMS', '1년,2년,3년', 11, NOW(), NOW()),
(16, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":12,"maxM":36,"minIncl":true}]', 12, NOW(), NOW()),
(16, 'INFO:상품개요', '부산 거주 및 부산 소재 회사에 재직중인 청년 직장인을 위한 BNK와 함께 만드는 맞춤 금융', 100, NOW(), NOW()),
(16, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>현재 부산에 거주 및&nbsp;부산 소재&nbsp;기업에 6개월 이상 재직중인 청년 급여소득자&nbsp;中&nbsp;아래 요건을 충족하는 고객</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p"><span>1) 연소득&nbsp;:&nbsp;5천만원 이하</span></p><p class="ssp-editor-p"><span>2) 재직기간 : 6개월 이상</span></p><p class="ssp-editor-p"><span style="font-size: 100%">3</span><span style="font-size: 100%">)&nbsp;</span><span style="font-family: Pretendard; font-size: 100%">연령: 만 19세 이상 ~ 만&nbsp;</span><span style="font-family: Pretendard; font-size: 100%">39세 이하의&nbsp;</span><span style="font-family: Pretendard; font-size: 100%">청년</span></p><p class="ssp-editor-p">4) 신용평점기준 : NICE 평점 675점 이상이면서&nbsp;KCB 평점 625점 이상인 고객</p><p class="ssp-editor-p">5) BNK와 함께하는 미래설계대출을 받은 이력이 없는 고객 (1회에 한하여 지원가능)</p>', 101, NOW(), NOW()),
(16, 'INFO:대출한도', '심사결과에 따라 최소 1백만원 ~ 최대 1천만원', 102, NOW(), NOW()),
(16, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>계단식금리 선택시 :&nbsp;1년 만기일시상환방식</li><li>고정금리 선택시 : 3년 만기일시상환방식</li></ul><ul class="info-list type-li-dot"><li>이자부과시기: 매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능&nbsp;</p>', 103, NOW(), NOW()),
(16, 'INFO:금리변동주기', '  6개월,  12개월', 104, NOW(), NOW()),
(16, 'INFO:기준금리', '고정금리', 105, NOW(), NOW()),
(16, 'INFO:기본금리', '<p class="ssp-editor-p">계단식금리, 고정금리 中 택 1<br></p>', 106, NOW(), NOW()),
(16, 'INFO:가산금리', ' ', 107, NOW(), NOW()),
(16, 'INFO:최종금리', '· 계단식금리 선택시
　- 우대금리 충족 개수에 따라 
　　> 3개 충족시 : 0.1% (1년차), 2.0% (2년차), 3.9% (3년차)
　　> 2개 충족시 : 1.0% (1년차), 3.0% (2년차), 5.0% (3년차)
　　> 1개 충족시 : 2.0% (1년차), 4.0% (2년차), 6.0% (3년차)

· 고정금리 선택시
　- 우대금리 충족 개수에 따라
　　> 3개 충족시 : 연 2.0%
　　> 2개 충족시 : 연 3.0%
　　> 1개 충족시 : 연 4.0%

· 우대조건
1) 부산 소재 기업 6개월 이상 재직
2) 부산 거주 1년 이상
3) 당행 주거래고객 여부주) (급여이체&카드보유)
주) 주거래고객 판단 여부
1. 급여이체 : 당행 급여 이체 1회 이상 이력 보유
(대출 신청월(M) 기준 전전월(M-2)까지 거래 인정)
2. 카드보유 : 신청일 기준 신용카드 또는 체크카드 정상 회원

 최저 연 0.10% ~ 최고 연 6.00% 
(최저금리는 우대요건 3개 충족시 1년차 금리이며, 최고금리는 우대요건 1개 충족시 3년차 금리) 

※ 단, 최초 취급후 3년 경과 후 산출금리(변동) 적용
1) 기준금리 : 신잔액 COFIX
2) 금리변동주기 : 6개월, 12개월
3) 본부 금리 감면 불가', 108, NOW(), NOW()),
(16, 'INFO:담보조건', '무보증 신용대출', 109, NOW(), NOW()),
(16, 'INFO:가입방법', ' 모바일뱅킹(개인)', 110, NOW(), NOW()),
(16, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p"><span>※ 수입인지대금 : 5천 만원 초과시 대출금액별 수입인지비용 차등 부과(50% 균등부담)</span></p><p class="ssp-editor-p">&nbsp;</p><div class="table-box"><table class="tbl-matrix" ><thead><tr><th rowspan="2" ><b>대출금액</b></th><th colspan="2" ><b>인지세액</b></th></tr><tr><th ><b>고객</b></th><th ><b>은행</b></th></tr></thead><tbody><tr><td ><p class="ssp-editor-p">5천만원 초과1억원 이하</p></td><td ><p class="ssp-editor-p">3만5천원</p></td><td ><p class="ssp-editor-p">3만5천원</p></td></tr><tr><td ><p class="ssp-editor-p">1억원 초과10억원 이하</p></td><td ><p class="ssp-editor-p">7만5천원</p></td><td ><p class="ssp-editor-p">7만5천원</p></td></tr><tr><td ><p class="ssp-editor-p">10억원 초과</p></td><td ><p class="ssp-editor-p">17만5천원</p></td><td ><p class="ssp-editor-p">17만5천원</p></td></tr></tbody></table></div>', 111, NOW(), NOW()),
(16, 'INFO:중도상환수수료', '면제', 112, NOW(), NOW()),
(16, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 상기에도 불구하고, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li></ul><ul class="info-list type-li-dot"><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으면 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 113, NOW(), NOW()),
(16, 'INFO:필요서류', '<p>1. 본인확인서류 : 신분증 (주민등록증, 운전면허증 등)</p><p class="ssp-editor-p">2. 재직확인서류 : 재직증명서, 건강보험자격득실확인서 등</p><p class="ssp-editor-p">3. 소득증빙서류 : 급여명세서 등</p><p class="ssp-editor-p">4. 거주확인서류 : 주민등록초본 (전체 주소변동이력 포함)</p><p class="ssp-editor-p">※ 심사 시 대상 자격 요건 확인을 위해 재직확인 서류는고객님께서 사진촬영을 통해 직접 업로드 해주셔야 하며, 소득증빙및 거주확인 서류는 공공마이데이터 또는 스크래핑을 통해모바일 서류 제출이 가능합니다.</p>', 114, NOW(), NOW()),
(16, 'INFO:마이너스통장(종합통장대출)가능여부', '부', 115, NOW(), NOW()),
(16, 'RATE:기준금리', '고정금리', 300, NOW(), NOW()),
(16, 'RATE:기본금리', '<p class="ssp-editor-p">계단식금리, 고정금리 中 택 1<br></p>', 301, NOW(), NOW()),
(16, 'RATE:우대금리', '0', 302, NOW(), NOW()),
(16, 'RATE:최종금리', '· 계단식금리 선택시<br>　- 우대금리 충족 개수에 따라 <br>　　&gt; 3개 충족시 : 0.1% (1년차), 2.0% (2년차), 3.9% (3년차)<br>　　&gt; 2개 충족시 : 1.0% (1년차), 3.0% (2년차), 5.0% (3년차)<br>　　&gt; 1개 충족시 : 2.0% (1년차), 4.0% (2년차), 6.0% (3년차)<br><br>· 고정금리 선택시<br>　- 우대금리 충족 개수에 따라<br>　　&gt; 3개 충족시 : 연 2.0%<br>　　&gt; 2개 충족시 : 연 3.0%<br>　　&gt; 1개 충족시 : 연 4.0%<br><br>· 우대조건<br>1) 부산 소재 기업 6개월 이상 재직<br>2) 부산 거주 1년 이상<br>3) 당행 주거래고객 여부주) (급여이체&amp;카드보유)<br>주) 주거래고객 판단 여부<br>1. 급여이체 : 당행 급여 이체 1회 이상 이력 보유<br>(대출 신청월(M) 기준 전전월(M-2)까지 거래 인정)<br>2. 카드보유 : 신청일 기준 신용카드 또는 체크카드 정상 회원<br><br> 최저 연 0.10% ~ 최고 연 6.00% <br>(최저금리는 우대요건 3개 충족시 1년차 금리이며, 최고금리는 우대요건 1개 충족시 3년차 금리) <br><br>※ 단, 최초 취급후 3년 경과 후 산출금리(변동) 적용<br>1) 기준금리 : 신잔액 COFIX<br>2) 금리변동주기 : 6개월, 12개월<br>3) 본부 금리 감면 불가', 303, NOW(), NOW()),
(16, 'DOC:여신거래 기본약관', '/terms/p16/0.pdf', 500, NOW(), NOW()),
(16, 'DOC:가계대출 상품설명서', '/terms/p16/1.pdf', 501, NOW(), NOW()),
(16, 'DOC:BNK와 함께하는 미래설계대출 공시자료', '/terms/p16/2.pdf', 502, NOW(), NOW());

-- [17] ONE스피드론 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(17, 'ONE스피드론', 9.8, '6개월 ~ 5년(상환방식에 따라 다름)', 'SALE', '신용대출', '0300000299', '소득 및 거래실적을 반영한 초간편 사전승인 대출', '5.5', '9.8', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(17, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(17, 'LOAN_LIMIT', '5천만원', 5, NOW(), NOW()),
(17, 'LOAN_TERM', '6개월 ~ 5년(상환방식에 따라 다름)', 6, NOW(), NOW()),
(17, 'TARGET', '사전승인 대상자라면 누구나 가능', 7, NOW(), NOW()),
(17, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(17, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(17, 'OPT_RATE_CYCLES', '3,6,12', 10, NOW(), NOW()),
(17, 'OPT_TERMS', '6개월,1년,2년,3년,5년', 11, NOW(), NOW()),
(17, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":60,"minIncl":false},{"method":"원리금균등상환","minM":6,"maxM":60,"minIncl":true}]', 12, NOW(), NOW()),
(17, 'INFO:상품개요', '<p class="ssp-editor-p">당행 거래실적에 따라 혜택을 더하는 사전승인 무보증 신용대출 상품</p>', 100, NOW(), NOW()),
(17, 'INFO:대출조건(자격)', '<ul class="info-list type-li-dot"><li>아래 요건을 모두 충족하는 경우<ul class="info-list type-li-dash-ed"><li>나이 25세 ~ 55세&nbsp;</li><li>가처분소득 1천만원 이상</li><li>NICE 745점 이상, KCB 690점 이상 모두 충족</li><li>고위험업권(저축은행, 캐피탈, 대부업) 신용대출 1건 이하 보유</li></ul></li></ul><p><span style="font-size: 100%">※ 사전승인대출 상품으로 1인 1계좌 원칙(멤버스론, 스피드론 계좌 포함)</span></p>', 101, NOW(), NOW()),
(17, 'INFO:대출한도', '<ul class="info-list type-li-dot"><li>사전승인 심사기준에 따라 최대 5천만원 이내<ul class="info-list type-li-dash-ed"><li><span>급여소득자: 최대 5천만원 이내</span></li><li><span>자영업자 및 기타소득자: 최대 4천만원 이내</span></li></ul></li></ul>', 102, NOW(), NOW()),
(17, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식(종합통장대출(마이너스통장) 포함)<ul class="info-list type-li-dash-ed"><li>6개월 이상~1년 이내</li></ul></li><li>원금균등 상환방식<ul class="info-list type-li-dash-ed"><li>1년 초과 ~ 5년 이내</li></ul></li><li>원리금균등 상환방식<ul class="info-list type-li-dash-ed"><li>6개월 이상 ~ 5년 이내</li></ul></li><li>이자부과시기<ul class="info-list type-li-dash-ed"><li>매월 후취(대출 해당일 또는 응당일 부과)</li></ul></li></ul><p class="ssp-editor-p">※ 휴일 대출원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(17, 'INFO:금리변동주기', '<p>3개월, 6개월, 12개월<br></p>', 104, NOW(), NOW()),
(17, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(17, 'INFO:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 5.50%</p>', 106, NOW(), NOW()),
(17, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>신용평가회사(CB사) 신용평점에 따라 최대 연 0.80% </li><li>마이너스통장대출 연 0.50% </li><li>고위험업권(캐피탈, 저축은행, 대부업) 1건 보유 연 0.50%</li></ul>', 107, NOW(), NOW()),
(17, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라최대 연 0.80% </li><li>신용평점에 따른 추가 우대금리 최대 연 0.50%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>거래실적 연동옵션 우대금리 최대 연 0.80%<ul class="info-list type-li-dash-ed"><li>급여(연금) 및 가맹점 결제대금(요양급여 포함) 자동이체: 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우<br></li></ul></li><li>자동이체(아파트관리비, 공과금, 통신요금) 건수: 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우<br></li></ul></li><li>예금평잔기준: 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우<br></li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우: 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우: 0.20%</li></ul></li><li>주택청약종합저축 자동이체: 0.10% <ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우<br></li></ul></li></ul></li><li>우수고객 우대금리 적용시 최대 연 0.40%<ul class="info-list type-li-dash-ed"><li>수신 거래: 0.30%</li><li>기타 거래: 0.10%</li><li>사전승인 재이용 고객: 0.10% </li></ul></li></ul><p class="ssp-editor-p">※ 취급 시 대출금리 10% 이상 적용 불가</p>', 108, NOW(), NOW()),
(17, 'INFO:최종금리', '최저 연 5.54 % ~최고 연 9.84 % (2024.9.24 신 잔액기준 (COFIX) : 2.54 %)', 109, NOW(), NOW()),
(17, 'INFO:담보조건', '<p>무보증 신용</p>', 110, NOW(), NOW()),
(17, 'INFO:가입방법', '<p>영업점, 모바일, 모바일웹 <br></p>', 111, NOW(), NOW()),
(17, 'INFO:수수료(부대비용)', '<p class="ssp-editor-p">해당사항없음</p>', 112, NOW(), NOW()),
(17, 'INFO:중도상환수수료', '<p class="ssp-editor-p">면제</p>', 113, NOW(), NOW()),
(17, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출장액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생이로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(17, 'INFO:필요서류', '<p>본인 확인서류(주민등록증, 운전면허증 등) <br></p>', 115, NOW(), NOW()),
(17, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>여</p>', 116, NOW(), NOW()),
(17, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(17, 'RATE:기본금리', '기준금리 + 5.50%', 301, NOW(), NOW()),
(17, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>신용평가회사(CB사) 신용평점에 따라 최대 연 0.80% </li><li>마이너스통장대출 연 0.50% </li><li>고위험업권(캐피탈, 저축은행, 대부업) 1건 보유 연 0.50%</li></ul>', 302, NOW(), NOW()),
(17, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>신용평점에 따라최대 연 0.80% </li><li>신용평점에 따른 추가 우대금리 최대 연 0.50%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>거래실적 연동옵션 우대금리 최대 연 0.80%<ul class="info-list type-li-dash-ed"><li>급여(연금) 및 가맹점 결제대금(요양급여 포함) 자동이체: 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우<br></li></ul></li><li>자동이체(아파트관리비, 공과금, 통신요금) 건수: 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우<br></li></ul></li><li>예금평잔기준: 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우<br></li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우: 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우: 0.20%</li></ul></li><li>주택청약종합저축 자동이체: 0.10% <ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우<br></li></ul></li></ul></li><li>우수고객 우대금리 적용시 최대 연 0.40%<ul class="info-list type-li-dash-ed"><li>수신 거래: 0.30%</li><li>기타 거래: 0.10%</li><li>사전승인 재이용 고객: 0.10% </li></ul></li></ul><p class="ssp-editor-p">※ 취급 시 대출금리 10% 이상 적용 불가</p>', 303, NOW(), NOW()),
(17, 'RATE:최종금리', '최저 연 5.54 % ~최고 연 9.84 % (2024.9.24 신 잔액기준 (COFIX) : 2.54 %)', 304, NOW(), NOW()),
(17, 'DOC:여신거래 기본약관', '/terms/p17/0.pdf', 500, NOW(), NOW()),
(17, 'DOC:가계대출 상품설명서', '/terms/p17/1.pdf', 501, NOW(), NOW()),
(17, 'DOC:상품공시자료', '/terms/p17/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(17, 'PREF_17_1', '급여(연금)·가맹점 자동이체', 0.3, '매 3개월간 2회 이상 건당 50만원 이상 급여 입금 시', NOW(), NOW()),
(17, 'PREF_17_2', '공과금 자동이체(8건)', 0.1, '매 3개월간 8건 이상 자동이체 시', NOW(), NOW()),
(17, 'PREF_17_3', '예금 평균잔액', 0.2, '매 3개월간 예금평잔 150만원(요구불 100만원) 이상 시', NOW(), NOW()),
(17, 'PREF_17_4', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW()),
(17, 'PREF_17_5', '주택청약종합저축 자동이체', 0.1, '매 3개월간 2회 이상 건당 10만원 이상 자동이체 시', NOW(), NOW());

-- [18] ONE장기근속 직장인 우대대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(18, 'ONE장기근속 직장인 우대대출', 12.5, '6개월~10년', 'SALE', '신용대출', '0300000306', '장기근속직장인을 위한 특별대출', '3.8', '12.5', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(18, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(18, 'LOAN_LIMIT', '200백만원', 5, NOW(), NOW()),
(18, 'LOAN_TERM', '6개월~10년', 6, NOW(), NOW()),
(18, 'TARGET', '현 직장 60개월 이상, 재직중 급여소득자', 7, NOW(), NOW()),
(18, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(18, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(18, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(18, 'OPT_TERMS', '6개월,1년,2년,3년,5년,7년,10년', 11, NOW(), NOW()),
(18, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":120,"minIncl":false},{"method":"원리금균등상환","minM":6,"maxM":120,"minIncl":true}]', 12, NOW(), NOW()),
(18, 'INFO:상품개요', '<p class="ssp-editor-p">장기근속직장인을 위한 특별 우대대출</p>', 100, NOW(), NOW()),
(18, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">현 직장 60개월 이상 재직 중인 급여소득자로 상세 자격요건은 다음과 같음</p><div class="table-box"><table class="tbl-matrix" ><colgroup><col span="2"> </colgroup><tbody><tr><th height="31" scope="" >직업체형태</th><td height="31" scope="" ><p class="ssp-editor-p" style="text-align: center">법인기업체/일반기업체</p></td></tr><tr><th height="31" scope="" >재직기간</th><td height="31" scope="" ><p class="ssp-editor-p" style="text-align: center">재직기간&nbsp;60개월  이상</p></td></tr><tr><th height="31" scope="" >재직업체구분</th><td height="31" scope="" ><p class="ssp-editor-p" style="text-align: center">일반기업체</p></td></tr><tr><th height="46" scope="" >CB평점기준</th><td height="46" scope="" ><p class="ssp-editor-p" style="text-align: center">NICE 675점&amp; KCB 625점 이상</p></td></tr><tr><th height="30" scope="" >연소득</th><td height="30" scope="" ><p class="ssp-editor-p" style="text-align: center">30백만원 이상</p></td></tr></tbody></table></div>', 101, NOW(), NOW()),
(18, 'INFO:대출한도', '심사결과에 따라 최소 5백만원 ~ 최대 200백만원
단, 종합통장대출(마이너스통장)은 100백만원 이내', 102, NOW(), NOW()),
(18, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식(종합통장대출(마이너스통장) 포함): 6개월 이상 ~ 1년</li><li>원금균등 상환방식: 1년 초과 ~ 10년</li><li>원리금균등 상환방식: 6개월 이상 ~ 10년</li><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p>※ 휴일 대출원금 또는 이자 상환 가능</p>', 103, NOW(), NOW()),
(18, 'INFO:금리변동주기', '<p class="ssp-editor-p">3, 6개월 중 선택</p>', 104, NOW(), NOW()),
(18, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(18, 'INFO:기본금리', '<p class="ssp-editor-p">기준금리 + 4.00%</p>', 106, NOW(), NOW()),
(18, 'INFO:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출(마이너스통장) 최대 연 0.50%</li><li>신용평점에 따라 최대 연 3.90%</li><li>당행 내부등급에 따라 최대 연 1.20%</li></ul>', 107, NOW(), NOW()),
(18, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>거래실적에 따른 감면금리 최대 연 0.60%
<ul class="info-list type-li-dash-ed"><li>급여 입금 : 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우<br></li></ul></li><li>자동이체건수 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우<br></li></ul></li><li>예금평잔기준 : 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)
<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우 : 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우 : 0.20%</li></ul></li><li>&nbsp; 주택청약종합저축(청년우대형 포함) 자동이체 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우</li></ul></li></ul></li><li>신용평점에 따라 최대 연 0.70%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.40%
<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>당행 내부등급에 따라 최대 연 0.50%</li><li>장기근속 우대금리 최대 연 0.30%</li><li>협약기관 우대금리 최대 연 0.30%</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li><span>가덕도 원주민 토지 보상 대상 특별 우대금리 최대 연 </span><span>0.20%</span></li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 상기 적용금리에도 불구하고최저스프레드는 아래 미만으로 적용 불가</p><ul class="info-list type-li-dot"><li>장기근속직장인(금리)&nbsp; &nbsp; &nbsp; &nbsp;: 기준금리 + 1.30%</li><li>장기근속직장인(한도)&nbsp; &nbsp; &nbsp; &nbsp;: 기준금리 + 2.10%</li></ul>', 108, NOW(), NOW()),
(18, 'INFO:최종금리', '최저 연 3.79% ~ 최고 12.49% (2026-05-28 신잔액기준 COFIX: 2.49%, 신규취급액기준 COFIX: 2.89%)
(최저금리는 신잔액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우. 
단, 상품별 최저스프레드 미만 적용 불가 반영)
(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 109, NOW(), NOW()),
(18, 'INFO:담보조건', '<p>신용</p>', 110, NOW(), NOW()),
(18, 'INFO:가입방법', '<p class="ssp-editor-p">영업점, 모바일</p>', 111, NOW(), NOW()),
(18, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li>인지세<ul class="info-list type-li-dash-ed"><li>「인지세법」에 따라 대출약정 시 납부하는 세금으로 대출금액에 따라 세액이 차등 적용되며, 은행과 고객이 각각 50% 부담<div class="table-box"><table class="tbl-matrix" ><tbody><tr><th scope="" ><p class="ssp-editor-p"><b>대출금액</b></p></th><th scope="" ><p class="ssp-editor-p"><b>5</b><b>천만원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>5</b><b>천만원 초과</b></p><p class="ssp-editor-p"><b>~ 1</b><b>억원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>1</b><b>억원 초과</b></p><p class="ssp-editor-p"><b>~ 10</b><b>억원 이하</b></p></th><th scope="" ><p class="ssp-editor-p"><b>10</b><b>억원 초과</b></p></th></tr><tr><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>인지세액</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>비과세</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>7</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각3만5천원)</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>15</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각7만5천원)</b></p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center"><b>35</b><b>만원</b></p><p class="ssp-editor-p" style="text-align: center"><b>(</b><b>각각17만5천원)</b></p></td></tr></tbody></table></div><p class="ssp-editor-p">&nbsp;&nbsp;</p></li></ul></li></ul>', 112, NOW(), NOW()),
(18, 'INFO:중도상환수수료', '대출기간 중 대출금의 전부 또는 일부를 상환하는 경우 은행에서 정한 기준에 따라 중도상환
수수료를 부담하셔야 합니다.
창구: 중도상환금액 × 수수료율(0.33%) × 대출잔여일수/대출기간
모바일: 면제 ', 113, NOW(), NOW()),
(18, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다. 단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지 아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다..<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.</li></ul>', 114, NOW(), NOW()),
(18, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>본인 확인서류(주민등록증, 운전면허증 등)</li><li>재직 확인서류(건강보험자격득실확인서, 재직증명서 등)</li><li>소득 확인서류(근로소득원천징수영수증, 소득금액증명원 등)</li><li>기타 확인서류(금융거래확인서 등)</li></ul><p class="ssp-editor-p">※ 모바일 대출심사 시 공동인증서가 있는 스마트폰에서 공동인증서 비밀번호 입력으로 자동제출이 가능합니다.</p>', 115, NOW(), NOW()),
(18, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>여</p>', 116, NOW(), NOW()),
(18, 'INFO:기타', '<ul class="info-list type-li-dot"><li>비대면 채널 신용대출 신청 시 1일 총 7회, 30일 내 총 20회까지 신청 가능합니다.</li><li>비대면 채널 신용대출 실행 시 1일 최대 1건, 1인 최대 7건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출 진행이 불가합니다.&nbsp; </li></ul><p>※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</p>', 117, NOW(), NOW()),
(18, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.<br>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(18, 'RATE:기본금리', '<p class="ssp-editor-p">기준금리 + 4.00%</p>', 301, NOW(), NOW()),
(18, 'RATE:가산금리', '<ul class="info-list type-li-dot"><li>종합통장대출(마이너스통장) 최대 연 0.50%</li><li>신용평점에 따라 최대 연 3.90%</li><li>당행 내부등급에 따라 최대 연 1.20%</li></ul>', 302, NOW(), NOW()),
(18, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>거래실적에 따른 감면금리 최대 연 0.60%
<ul class="info-list type-li-dash-ed"><li>급여 입금 : 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우<br></li></ul></li><li>자동이체건수 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우<br></li></ul></li><li>예금평잔기준 : 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)
<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우 : 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우 : 0.20%</li></ul></li><li>&nbsp; 주택청약종합저축(청년우대형 포함) 자동이체 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우</li></ul></li></ul></li><li>신용평점에 따라 최대 연 0.70%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.40%
<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>당행 내부등급에 따라 최대 연 0.50%</li><li>장기근속 우대금리 최대 연 0.30%</li><li>협약기관 우대금리 최대 연 0.30%</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li><span>가덕도 원주민 토지 보상 대상 특별 우대금리 최대 연 </span><span>0.20%</span></li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 상기 적용금리에도 불구하고최저스프레드는 아래 미만으로 적용 불가</p><ul class="info-list type-li-dot"><li>장기근속직장인(금리)&nbsp; &nbsp; &nbsp; &nbsp;: 기준금리 + 1.30%</li><li>장기근속직장인(한도)&nbsp; &nbsp; &nbsp; &nbsp;: 기준금리 + 2.10%</li></ul>', 303, NOW(), NOW()),
(18, 'RATE:최종금리', '최저 연 3.79% ~ 최고 12.49% (2026-05-28 신잔액기준 COFIX: 2.49%, 신규취급액기준 COFIX: 2.89%)<br>(최저금리는 신잔액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우. <br>단, 상품별 최저스프레드 미만 적용 불가 반영)<br>(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 304, NOW(), NOW()),
(18, 'DOC:여신거래 기본약관', '/terms/p18/0.pdf', 500, NOW(), NOW()),
(18, 'DOC:가계대출 상품설명서', '/terms/p18/1.pdf', 501, NOW(), NOW()),
(18, 'DOC:상품공시자료', '/terms/p18/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(18, 'PREF_18_1', '급여 입금', 0.3, '매 3개월간 2회 이상 건당 50만원 이상 급여 입금 시', NOW(), NOW()),
(18, 'PREF_18_2', '공과금 자동이체(8건)', 0.1, '매 3개월간 8건 이상 자동이체 시', NOW(), NOW()),
(18, 'PREF_18_3', '예금 평균잔액', 0.2, '매 3개월간 예금평잔 150만원(요구불 100만원) 이상 시', NOW(), NOW()),
(18, 'PREF_18_4', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW()),
(18, 'PREF_18_5', '주택청약종합저축 자동이체', 0.1, '매 3개월간 2회 이상 건당 10만원 이상 자동이체 시', NOW(), NOW());

-- [19] ONE퍼스트 금융인대출 (신용대출)
INSERT INTO loan_product (product_id, product_name, base_rate, loan_period, status, category, mkpd_cd, catchphrase, rate_min, rate_max, created_at, updated_at) VALUES
(19, 'ONE퍼스트 금융인대출', 6.82, '6개월~10년', 'SALE', '신용대출', '0300000183', '금융인을 위한 특별대출!', '3.62', '6.82', NOW(), NOW());
INSERT INTO product_description (product_id, attr_key, attr_value, sort_order, created_at, updated_at) VALUES
(19, 'BASE_RATE_RAW', '2.5', 4, NOW(), NOW()),
(19, 'LOAN_LIMIT', '350백만원', 5, NOW(), NOW()),
(19, 'LOAN_TERM', '6개월~10년', 6, NOW(), NOW()),
(19, 'TARGET', '제1금융기관 및, 금융관련기관 재직자', 7, NOW(), NOW()),
(19, 'BASE_DATE', '2026-06-23', 8, NOW(), NOW()),
(19, 'OPT_RATE_TYPE', '신잔액기준 COFIX', 9, NOW(), NOW()),
(19, 'OPT_RATE_CYCLES', '3,6', 10, NOW(), NOW()),
(19, 'OPT_TERMS', '6개월,1년,2년,3년,5년,7년,10년', 11, NOW(), NOW()),
(19, 'OPT_REPAYMENTS', '[{"method":"만기일시상환","minM":6,"maxM":12,"minIncl":true},{"method":"종합통장대출(마이너스통장)","minM":6,"maxM":12,"minIncl":true},{"method":"원금균등상환","minM":12,"maxM":120,"minIncl":false},{"method":"원리금균등상환","minM":6,"maxM":120,"minIncl":true}]', 12, NOW(), NOW()),
(19, 'INFO:상품개요', '<p>금융기관 재직자를 위한 우대대출</p>', 100, NOW(), NOW()),
(19, 'INFO:대출조건(자격)', '<p class="ssp-editor-p">제1금융기관(시중은행, 특수은행, 외국계은행, 지방은행) 및 금융관련기관에 재직중인 급여소득자로 상세 자격요건은 다음과 같음</p><div class="table-box"><table class="__se_tbl tbl-matrix" ><tbody><tr><th scope="" ><p class="ssp-editor-p">&nbsp;재직업체형태</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">&nbsp;법인기업체</p></td></tr><tr><th scope="" ><p class="ssp-editor-p">&nbsp;재직기간</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">&nbsp;재직기간 3개월 이상</p></td></tr><tr><th scope="" ><p class="ssp-editor-p">&nbsp;CB평점기준</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">&nbsp;NICE 835점 &amp; KCB 845점 이상</p></td></tr><tr><th scope="" ><p class="ssp-editor-p">&nbsp;연소득</p></th><td scope="" ><p class="ssp-editor-p" style="text-align: center">&nbsp;50백만원 이상</p></td></tr></tbody></table></div>', 101, NOW(), NOW()),
(19, 'INFO:대출한도', '심사결과에 따라 최소 5백만원 ~ 최대 350백만원
단, 종합통장대출(마이너스통장)은 100백만원 이내', 102, NOW(), NOW()),
(19, 'INFO:상환방법 및 대출기간', '<ul class="info-list type-li-dot"><li>만기일시상환방식(종합통장대출(마이너스통장) 포함): 6개월 이상 ~ 1년</li><li>원금균등 상환방식: 1년 초과 ~ 10년</li><li>원리금균등 상환방식: 6개월 이상 ~ 10년</li><li>이자부과시기:매월 후취(대출 해당일 또는 응당일 부과)</li></ul><p><span style="font-size: 100%">※ 휴일 대출원금 또는 이자 상환 가능</span></p>', 103, NOW(), NOW()),
(19, 'INFO:금리변동주기', '<p>3, 6개월 중 선택</p>', 104, NOW(), NOW()),
(19, 'INFO:기준금리', '신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.
', 105, NOW(), NOW()),
(19, 'INFO:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 3.42%</p><p>기준금리(신규취급액기준 (COFIX) ) + 3.42%</p>', 106, NOW(), NOW()),
(19, 'INFO:가산금리', '<p><span style="font-size: 100%">종합통장대출(마이너스통장) 연 0.50%&nbsp;</span></p>', 107, NOW(), NOW()),
(19, 'INFO:우대금리', '<ul class="info-list type-li-dot"><li>거래실적에 따른 감면금리 최대 연 0.60%<ul class="info-list type-li-dash-ed"><li>급여 입금 : 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체건수 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준 : 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우 : 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우 : 0.20%</li></ul></li><li>&nbsp; 주택청약종합저축(청년우대형 포함) 자동이체 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우</li></ul></li></ul></li><li>신용평점에 따라 최대 연 0.70%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.40%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>ONE퍼스트 금융인대출 최초신규 연 0.20%</li><li>모바일뱅킹(App)신청 고객 우대금리 연 0.20%</li><li>협약기관 우대금리 최대 연 0.30%</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li>가덕도 원주민 토지 보상 대상 특별 우대금리 최대 연 0.20%&nbsp;</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 상기 적용금리에도 불구하고최저스프레드는 1.12%&nbsp;미만으로 적용 불가 (최저금리 : 기준금리+1.12%)</p><p>&nbsp;</p>', 108, NOW(), NOW()),
(19, 'INFO:최종금리', '최저 연 3.61% ~ 최고 6.81% (2026-05-28 신잔액 COFIX: 2.49%, 신규취급액 COFIX: 2.89% )
(최저금리는 신잔액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우.
  단, 상품별 최저스프레드 미만 적용 불가 반영)
(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 109, NOW(), NOW()),
(19, 'INFO:담보조건', '<p>신용</p>', 110, NOW(), NOW()),
(19, 'INFO:가입방법', '<p><span style="font-size: 100%">영업점, 모바일&nbsp;</span></p>', 111, NOW(), NOW()),
(19, 'INFO:수수료(부대비용)', '<ul class="info-list type-li-dot"><li>인지세<ul class="info-list type-li-dash-ed"><li>「인지세법」에 따라 대출약정 시 납부하는 세금으로 대출금액에 따라 세액이 차등 적용되며, 은행과 고객이 각각 50% 부담<div class="table-box"><table class="tbl-matrix" ><tbody><tr><th scope="" ><p class="ssp-editor-p">대출금액</p></th><th scope="" ><p class="ssp-editor-p">5천만원 이하</p></th><th scope="" ><p class="ssp-editor-p">5천만원 초과</p><p class="ssp-editor-p">~ 1억원 이하</p></th><th scope="" ><p class="ssp-editor-p">1억원 초과</p><p class="ssp-editor-p">~ 10억원 이하</p></th><th scope="" ><p class="ssp-editor-p">10억원 초과</p></th></tr><tr><td scope="" ><p class="ssp-editor-p" style="text-align: center">인지세액</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">비과세</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">7만원</p><p class="ssp-editor-p" style="text-align: center">(각각3만5천원)</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">15만원</p><p class="ssp-editor-p" style="text-align: center">(각각7만5천원)</p></td><td scope="" ><p class="ssp-editor-p" style="text-align: center">35만원</p><p class="ssp-editor-p" style="text-align: center">(각각17만5천원)</p></td></tr></tbody></table></div></li></ul></li></ul>', 112, NOW(), NOW()),
(19, 'INFO:중도상환수수료', '대출기간 중 대출금의 전부 또는 일부를 상환하는 경우 은행에서 정한 기준에 따라 중도상환
수수료를 부담하셔야 합니다.
창구: 중도상환금액 × 수수료율(0.33%) × 대출잔여일수/대출기간
모바일: 면제', 113, NOW(), NOW()),
(19, 'INFO:금융소비자가 알아야 할 사항', '<ul class="info-list type-li-dot"><li>대출의 만기가 도래하여 상환 기일이 연장되거나, 연장에 따른 대출이율이 변경될 경우 (대출이자율 적용방식의 변경은 제외)에도 은행에서 정한 연장기준을 따르기로 합니다.</li><li>대출 만기도래 시 고객님의 재직상태, 신용상태 및 은행의 심사기준에 따라&nbsp; 대출이 연장되지 않을 수도 있고, 대출거래조건(이율 및 한도등)이 변경 될 수도 있으니, 이점 유의하시기 바랍니다.</li><li>대출 취급 후 거래조건(상환 조건 변경 등)을 변경하고자 하는 경우에는 은행의 승인이 있어야 합니다.</li><li>대출 이자 또는 원금을 약정한 날에 납입(상환)하지 않을 경우 연체이자를 부담하여야하며,금융거래 제약 등 불이익을 받으실 수 있습니다.&nbsp;단, 「개인금융채권의 관리 및 개인금융채무자의 보호에 관한 법률」에서 정하는 바에 따라 계좌별 대출원금(약정금액을 기준으로 하며, 한도대출의 경우 한도금액을 기준으로 함)이 5천만원 미만인 경우로서 연체가 발생하여 기한의 이익이 상실된 경우에는, 채무이행의 기한이 도래하지&nbsp;아니한 대출원금에 대하여는 연체이자율이 적용되지 않습니다.<br>※ 연체이율 : 대출금리에 3.0%를 가산(최고 15.0%) (대출금리가 최고 연체이율 이상일 경우 대출금리 +2.0%)</li><li>만기일 경과 후 대출금액을 전액 상환하지 않거나 기한연장 하지 않은 경우, 은행여신거래 기본약관에 따라 기한의 이익이 상실되어 대출잔액에 대한 지연배상금이 부과되며, 예금등 기타채권과 상계나 법적절차 등으로 재산상 불이익과 금융거래 제약을 받으실 수 있습니다.</li><li>대출원금, 이자 등을 3개월 이상 연체한 경우에는 3개월이 되는 날을 등록사유 발생일로 하여 그 때부터 7영업일 이내에 "신용정보관리규약"에서 정한 ''연체 등'' 정보거래처로 등록되어 금융거래제약 등 불이익을 받을 수 있습니다.</li><li>납부해야할 원리금이 연체될 경우 계약만료 기한이 도래하기 전 모든 원리금을 변제 해야할 의무가 발생할 수 있습니다.</li><li>상환능력에 비해 대출금이 과도할 경우 신용평점이 하락할수 있으며 신용평점 하락에 따라 금융거래 제약 등 불이익을 받으실 수 있습니다.</li><li>이 설명서는 은행이용자의 상품에 대한 이해를 돕고 약관의 중요내용을 알려드리기 위한 참고자료이며, 실제 계약은 은행여신거래 기본약관(가계용)이 적용 됩니다. 계약을 신청하는 경우 약관이, 계약을 체결하는 경우 계약서류가 교부됩니다.</li><li>금융상품 계약 체결 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li><li>금융소비자는 상품에 대한 충분한 설명을 받을 권리가 있으며, 그 설명을 이해하신 후 거래하시기 바랍니다.</li><li>기타 자세한 사항은 가까운 영업점 또는 부산은행 고객센터(1544-6200, 1588-6200)로 문의하시기 바랍니다.&nbsp;</li></ul>', 114, NOW(), NOW()),
(19, 'INFO:필요서류', '<ul class="info-list type-li-dot"><li>본인 확인서류(주민등록증, 운전면허증 등)</li><li><span>재직 확인서류(건강보험자격득실확인서, 재직증명서 등)</span></li><li><span>소득 확인서류(근로소득원천징수영수증, 소득금액증명원 등)</span></li><li><span>기타 확인서류(금융거래확인서 등)</span></li></ul><p><span style="font-size: 100%">※ 모바일 대출심사 시 공인인증서가 있는 스마트폰에서 공동인증서 비밀번호 입력으로 자동제출이 가능합니다.</span></p>', 115, NOW(), NOW()),
(19, 'INFO:마이너스통장(종합통장대출)가능여부', '<p>여</p>', 116, NOW(), NOW()),
(19, 'INFO:기타', '<ul class="info-list type-li-dot"><li>비대면 채널 신용대출 신청 시 1일 총 7회, 30일 내 총 20회까지 신청 가능합니다.</li><li>비대면 채널 신용대출 실행 시 1일 최대 1건, 1인 최대 7건까지 실행 가능합니다.</li><li>최근 당행에서 대출을 실행한 경우 추가대출이 불가능할 수 있으며, 신청일 현재 당행에서 진행중인 대출이 있는 경우 대출 진행이 불가합니다.</li></ul><p><span style="font-size: 100%">※ 내부 상품 심사기준에 따라 대출 거래가 제한될 수 있습니다.</span></p>', 117, NOW(), NOW()),
(19, 'RATE:기준금리', '<div class="rate-vary"><span class="vary-label">변동</span><ul class="info-list type-li-dot"><li>신 잔액기준 (COFIX) : 2.5%(2026-06-23현재)</li><li>신규취급액기준 (COFIX) : 2.9%(2026-06-23현재)</li></ul></div>신잔액기준 COFIX : 전국은행연합회에서 매월 고시하는 신잔액 기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.<br>신규취급액기준 COFIX : 전국은행연합회에서 매월 고시하는 신규취급액기준 자금조달비용지수를 기초로 은행에서 고시하는 금리.', 300, NOW(), NOW()),
(19, 'RATE:기본금리', '<p>기준금리(신 잔액기준 (COFIX) ) + 3.42%</p><p>기준금리(신규취급액기준 (COFIX) ) + 3.42%</p>', 301, NOW(), NOW()),
(19, 'RATE:가산금리', '<p><span style="font-size: 100%">종합통장대출(마이너스통장) 연 0.50%&nbsp;</span></p>', 302, NOW(), NOW()),
(19, 'RATE:우대금리', '<ul class="info-list type-li-dot"><li>거래실적에 따른 감면금리 최대 연 0.60%<ul class="info-list type-li-dash-ed"><li>급여 입금 : 0.30%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 50만원 이상의 급여가 입금되는 경우</li></ul></li><li>자동이체건수 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 8건 이상 공과금 또는 지로, 아파트관리비, 통신요금이 자동이체되는 경우</li></ul></li><li>예금평잔기준 : 0.20%<ul class="info-list type-li-gt"><li>매 3개월간 예금평잔 1.5백만원 또는 요구불예금평잔 1백만원 이상인 경우</li></ul></li><li>신용카드 회원(신규 포함)<ul class="info-list type-li-gt"><li>매 3개월간 신용카드 사용금액이 1백만원 이상인 경우 : 0.10%</li><li>매 3개월간 신용카드 사용금액이 2백만원 이상인 경우 : 0.20%</li></ul></li><li>&nbsp; 주택청약종합저축(청년우대형 포함) 자동이체 : 0.10%<ul class="info-list type-li-gt"><li>매 3개월간 2회 이상 건당 10만원 이상 주택청약종합저축(청년우대형 포함) 계좌에 자동이체 되는 경우</li></ul></li></ul></li><li>신용평점에 따라 최대 연 0.70%</li><li>신용평점에 따른 추가 우대금리 최대 연 0.40%<ul class="info-list type-li-dash-ed"><li>종합통장대출 제외, 신규 시에만 적용</li></ul></li><li>ONE퍼스트 금융인대출 최초신규 연 0.20%</li><li>모바일뱅킹(App)신청 고객 우대금리 연 0.20%</li><li>협약기관 우대금리 최대 연 0.30%</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 영업점 대면 가입 특별감면금리 (비대면 채널 적용 불가)</p><ul class="info-list type-li-dot"><li>가덕도 원주민 토지 보상 대상 특별 우대금리 최대 연 0.20%&nbsp;</li></ul><p class="ssp-editor-p">&nbsp;</p><p class="ssp-editor-p">※ 상기 적용금리에도 불구하고최저스프레드는 1.12%&nbsp;미만으로 적용 불가 (최저금리 : 기준금리+1.12%)</p><p>&nbsp;</p>', 303, NOW(), NOW()),
(19, 'RATE:최종금리', '최저 연 3.61% ~ 최고 6.81% (2026-05-28 신잔액 COFIX: 2.49%, 신규취급액 COFIX: 2.89% )<br>(최저금리는 신잔액기준 COFIX 변동금리 적용, 최대 우대금리 적용 및 가산금리 미적용하는 경우.<br>  단, 상품별 최저스프레드 미만 적용 불가 반영)<br>(최고금리는 신규취급액기준 COFIX 변동금리 적용, 최대 가산금리 적용 및 우대금리 미적용하는 경우)', 304, NOW(), NOW()),
(19, 'DOC:여신거래 기본약관', '/terms/p19/0.pdf', 500, NOW(), NOW()),
(19, 'DOC:가계대출 상품설명서', '/terms/p19/1.pdf', 501, NOW(), NOW()),
(19, 'DOC:상품공시자료', '/terms/p19/2.pdf', 502, NOW(), NOW());
INSERT INTO product_preferential_rate (product_id, condition_code, condition_name, rate_value, description, created_at, updated_at) VALUES
(19, 'PREF_19_1', '급여 입금', 0.3, '매 3개월간 2회 이상 건당 50만원 이상 급여 입금 시', NOW(), NOW()),
(19, 'PREF_19_2', '공과금 자동이체(8건)', 0.1, '매 3개월간 8건 이상 자동이체 시', NOW(), NOW()),
(19, 'PREF_19_3', '예금 평균잔액', 0.2, '매 3개월간 예금평잔 150만원(요구불 100만원) 이상 시', NOW(), NOW()),
(19, 'PREF_19_4', '신용카드 200만원 이상 사용', 0.2, '매 3개월간 신용카드 200만원 이상 사용 시', NOW(), NOW()),
(19, 'PREF_19_5', '주택청약종합저축 자동이체', 0.1, '매 3개월간 2회 이상 건당 10만원 이상 자동이체 시', NOW(), NOW()),
(19, 'PREF_19_6', '모바일뱅킹(App) 신청', 0.2, '모바일뱅킹(App) 신청 고객', NOW(), NOW());

-- ============================================================
-- 약관 기본 (PRODUCT_TERMS_BASE) — 상품별 6종
-- ============================================================
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(1, 'ADMIN_INFO_REQUEST', '/terms/p1/admin_info_request.pdf', 'Y', NOW(), NOW()),
(1, 'PERSONAL_INFO_CONSENT', '/terms/p1/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(1, 'MOBILE_AUTH_TERMS', '/terms/p1/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(1, 'PRODUCT_TERMS', '/terms/p1/product_terms.pdf', 'Y', NOW(), NOW()),
(1, 'PRODUCT_DESCRIPTION', '/terms/p1/product_description.pdf', 'Y', NOW(), NOW()),
(1, 'BOND_CONTRACT', '/terms/p1/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(2, 'ADMIN_INFO_REQUEST', '/terms/p2/admin_info_request.pdf', 'Y', NOW(), NOW()),
(2, 'PERSONAL_INFO_CONSENT', '/terms/p2/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(2, 'MOBILE_AUTH_TERMS', '/terms/p2/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(2, 'PRODUCT_TERMS', '/terms/p2/product_terms.pdf', 'Y', NOW(), NOW()),
(2, 'PRODUCT_DESCRIPTION', '/terms/p2/product_description.pdf', 'Y', NOW(), NOW()),
(2, 'BOND_CONTRACT', '/terms/p2/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(3, 'ADMIN_INFO_REQUEST', '/terms/p3/admin_info_request.pdf', 'Y', NOW(), NOW()),
(3, 'PERSONAL_INFO_CONSENT', '/terms/p3/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(3, 'MOBILE_AUTH_TERMS', '/terms/p3/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(3, 'PRODUCT_TERMS', '/terms/p3/product_terms.pdf', 'Y', NOW(), NOW()),
(3, 'PRODUCT_DESCRIPTION', '/terms/p3/product_description.pdf', 'Y', NOW(), NOW()),
(3, 'BOND_CONTRACT', '/terms/p3/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(4, 'ADMIN_INFO_REQUEST', '/terms/p4/admin_info_request.pdf', 'Y', NOW(), NOW()),
(4, 'PERSONAL_INFO_CONSENT', '/terms/p4/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(4, 'MOBILE_AUTH_TERMS', '/terms/p4/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(4, 'PRODUCT_TERMS', '/terms/p4/product_terms.pdf', 'Y', NOW(), NOW()),
(4, 'PRODUCT_DESCRIPTION', '/terms/p4/product_description.pdf', 'Y', NOW(), NOW()),
(4, 'BOND_CONTRACT', '/terms/p4/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(5, 'ADMIN_INFO_REQUEST', '/terms/p5/admin_info_request.pdf', 'Y', NOW(), NOW()),
(5, 'PERSONAL_INFO_CONSENT', '/terms/p5/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(5, 'MOBILE_AUTH_TERMS', '/terms/p5/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(5, 'PRODUCT_TERMS', '/terms/p5/product_terms.pdf', 'Y', NOW(), NOW()),
(5, 'PRODUCT_DESCRIPTION', '/terms/p5/product_description.pdf', 'Y', NOW(), NOW()),
(5, 'BOND_CONTRACT', '/terms/p5/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(6, 'ADMIN_INFO_REQUEST', '/terms/p6/admin_info_request.pdf', 'Y', NOW(), NOW()),
(6, 'PERSONAL_INFO_CONSENT', '/terms/p6/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(6, 'MOBILE_AUTH_TERMS', '/terms/p6/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(6, 'PRODUCT_TERMS', '/terms/p6/product_terms.pdf', 'Y', NOW(), NOW()),
(6, 'PRODUCT_DESCRIPTION', '/terms/p6/product_description.pdf', 'Y', NOW(), NOW()),
(6, 'BOND_CONTRACT', '/terms/p6/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(7, 'ADMIN_INFO_REQUEST', '/terms/p7/admin_info_request.pdf', 'Y', NOW(), NOW()),
(7, 'PERSONAL_INFO_CONSENT', '/terms/p7/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(7, 'MOBILE_AUTH_TERMS', '/terms/p7/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(7, 'PRODUCT_TERMS', '/terms/p7/product_terms.pdf', 'Y', NOW(), NOW()),
(7, 'PRODUCT_DESCRIPTION', '/terms/p7/product_description.pdf', 'Y', NOW(), NOW()),
(7, 'BOND_CONTRACT', '/terms/p7/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(8, 'ADMIN_INFO_REQUEST', '/terms/p8/admin_info_request.pdf', 'Y', NOW(), NOW()),
(8, 'PERSONAL_INFO_CONSENT', '/terms/p8/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(8, 'MOBILE_AUTH_TERMS', '/terms/p8/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(8, 'PRODUCT_TERMS', '/terms/p8/product_terms.pdf', 'Y', NOW(), NOW()),
(8, 'PRODUCT_DESCRIPTION', '/terms/p8/product_description.pdf', 'Y', NOW(), NOW()),
(8, 'BOND_CONTRACT', '/terms/p8/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(9, 'ADMIN_INFO_REQUEST', '/terms/p9/admin_info_request.pdf', 'Y', NOW(), NOW()),
(9, 'PERSONAL_INFO_CONSENT', '/terms/p9/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(9, 'MOBILE_AUTH_TERMS', '/terms/p9/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(9, 'PRODUCT_TERMS', '/terms/p9/product_terms.pdf', 'Y', NOW(), NOW()),
(9, 'PRODUCT_DESCRIPTION', '/terms/p9/product_description.pdf', 'Y', NOW(), NOW()),
(9, 'BOND_CONTRACT', '/terms/p9/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(10, 'ADMIN_INFO_REQUEST', '/terms/p10/admin_info_request.pdf', 'Y', NOW(), NOW()),
(10, 'PERSONAL_INFO_CONSENT', '/terms/p10/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(10, 'MOBILE_AUTH_TERMS', '/terms/p10/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(10, 'PRODUCT_TERMS', '/terms/p10/product_terms.pdf', 'Y', NOW(), NOW()),
(10, 'PRODUCT_DESCRIPTION', '/terms/p10/product_description.pdf', 'Y', NOW(), NOW()),
(10, 'BOND_CONTRACT', '/terms/p10/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(11, 'ADMIN_INFO_REQUEST', '/terms/p11/admin_info_request.pdf', 'Y', NOW(), NOW()),
(11, 'PERSONAL_INFO_CONSENT', '/terms/p11/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(11, 'MOBILE_AUTH_TERMS', '/terms/p11/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(11, 'PRODUCT_TERMS', '/terms/p11/product_terms.pdf', 'Y', NOW(), NOW()),
(11, 'PRODUCT_DESCRIPTION', '/terms/p11/product_description.pdf', 'Y', NOW(), NOW()),
(11, 'BOND_CONTRACT', '/terms/p11/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(12, 'ADMIN_INFO_REQUEST', '/terms/p12/admin_info_request.pdf', 'Y', NOW(), NOW()),
(12, 'PERSONAL_INFO_CONSENT', '/terms/p12/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(12, 'MOBILE_AUTH_TERMS', '/terms/p12/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(12, 'PRODUCT_TERMS', '/terms/p12/product_terms.pdf', 'Y', NOW(), NOW()),
(12, 'PRODUCT_DESCRIPTION', '/terms/p12/product_description.pdf', 'Y', NOW(), NOW()),
(12, 'BOND_CONTRACT', '/terms/p12/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(13, 'ADMIN_INFO_REQUEST', '/terms/p13/admin_info_request.pdf', 'Y', NOW(), NOW()),
(13, 'PERSONAL_INFO_CONSENT', '/terms/p13/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(13, 'MOBILE_AUTH_TERMS', '/terms/p13/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(13, 'PRODUCT_TERMS', '/terms/p13/product_terms.pdf', 'Y', NOW(), NOW()),
(13, 'PRODUCT_DESCRIPTION', '/terms/p13/product_description.pdf', 'Y', NOW(), NOW()),
(13, 'BOND_CONTRACT', '/terms/p13/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(14, 'ADMIN_INFO_REQUEST', '/terms/p14/admin_info_request.pdf', 'Y', NOW(), NOW()),
(14, 'PERSONAL_INFO_CONSENT', '/terms/p14/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(14, 'MOBILE_AUTH_TERMS', '/terms/p14/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(14, 'PRODUCT_TERMS', '/terms/p14/product_terms.pdf', 'Y', NOW(), NOW()),
(14, 'PRODUCT_DESCRIPTION', '/terms/p14/product_description.pdf', 'Y', NOW(), NOW()),
(14, 'BOND_CONTRACT', '/terms/p14/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(15, 'ADMIN_INFO_REQUEST', '/terms/p15/admin_info_request.pdf', 'Y', NOW(), NOW()),
(15, 'PERSONAL_INFO_CONSENT', '/terms/p15/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(15, 'MOBILE_AUTH_TERMS', '/terms/p15/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(15, 'PRODUCT_TERMS', '/terms/p15/product_terms.pdf', 'Y', NOW(), NOW()),
(15, 'PRODUCT_DESCRIPTION', '/terms/p15/product_description.pdf', 'Y', NOW(), NOW()),
(15, 'BOND_CONTRACT', '/terms/p15/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(16, 'ADMIN_INFO_REQUEST', '/terms/p16/admin_info_request.pdf', 'Y', NOW(), NOW()),
(16, 'PERSONAL_INFO_CONSENT', '/terms/p16/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(16, 'MOBILE_AUTH_TERMS', '/terms/p16/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(16, 'PRODUCT_TERMS', '/terms/p16/product_terms.pdf', 'Y', NOW(), NOW()),
(16, 'PRODUCT_DESCRIPTION', '/terms/p16/product_description.pdf', 'Y', NOW(), NOW()),
(16, 'BOND_CONTRACT', '/terms/p16/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(17, 'ADMIN_INFO_REQUEST', '/terms/p17/admin_info_request.pdf', 'Y', NOW(), NOW()),
(17, 'PERSONAL_INFO_CONSENT', '/terms/p17/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(17, 'MOBILE_AUTH_TERMS', '/terms/p17/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(17, 'PRODUCT_TERMS', '/terms/p17/product_terms.pdf', 'Y', NOW(), NOW()),
(17, 'PRODUCT_DESCRIPTION', '/terms/p17/product_description.pdf', 'Y', NOW(), NOW()),
(17, 'BOND_CONTRACT', '/terms/p17/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(18, 'ADMIN_INFO_REQUEST', '/terms/p18/admin_info_request.pdf', 'Y', NOW(), NOW()),
(18, 'PERSONAL_INFO_CONSENT', '/terms/p18/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(18, 'MOBILE_AUTH_TERMS', '/terms/p18/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(18, 'PRODUCT_TERMS', '/terms/p18/product_terms.pdf', 'Y', NOW(), NOW()),
(18, 'PRODUCT_DESCRIPTION', '/terms/p18/product_description.pdf', 'Y', NOW(), NOW()),
(18, 'BOND_CONTRACT', '/terms/p18/bond_contract.pdf', 'Y', NOW(), NOW());
INSERT INTO product_terms_base (product_id, terms_type, terms_path, active_yn, created_at, updated_at) VALUES
(19, 'ADMIN_INFO_REQUEST', '/terms/p19/admin_info_request.pdf', 'Y', NOW(), NOW()),
(19, 'PERSONAL_INFO_CONSENT', '/terms/p19/personal_info_consent.pdf', 'Y', NOW(), NOW()),
(19, 'MOBILE_AUTH_TERMS', '/terms/p19/mobile_auth_terms.pdf', 'Y', NOW(), NOW()),
(19, 'PRODUCT_TERMS', '/terms/p19/product_terms.pdf', 'Y', NOW(), NOW()),
(19, 'PRODUCT_DESCRIPTION', '/terms/p19/product_description.pdf', 'Y', NOW(), NOW()),
(19, 'BOND_CONTRACT', '/terms/p19/bond_contract.pdf', 'Y', NOW(), NOW());

-- 약관 이력 (각 base 당 seq=1)
INSERT INTO product_terms_history (terms_id, terms_seq, terms_path, created_at)
SELECT terms_id, 1, terms_path, NOW() FROM product_terms_base;
