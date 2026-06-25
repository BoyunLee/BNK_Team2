#!/usr/bin/env python3
"""
크롤링한 대출 상품(ssp_data.json)을 Gemini로 임베딩하여 Qdrant에 적재한다.

- 소스: <DATA_DIR>/<상품폴더>/ssp_data.json (74개)
- 대상: application-local.yml 의 qdrant 주소/키, gemini 키 사용
- payload 형식은 챗봇(VectorSearchService/ChatService)이 읽는 것과 동일하게 맞춤
  (productCode, productName, text, status=SALE)

사용법:
    python3 scripts/ingest_ssp_to_qdrant.py
    DATA_DIR=/경로 python3 scripts/ingest_ssp_to_qdrant.py
"""
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request

# ── 설정 ─────────────────────────────────────────────
DATA_DIR = os.environ.get("DATA_DIR", os.path.expanduser("~/Downloads/busanbank_loans"))
YML_PATH = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "application-local.yml")
COLLECTION = "loan-products"
EMBED_MODEL = "gemini-embedding-001"  # 챗봇과 동일해야 함
EMBED_DIM = 768                       # outputDimensionality로 768 고정
GEMINI_BASE = "https://generativelanguage.googleapis.com/v1beta"
MAX_TEXT_CHARS = 3000                # 임베딩 입력 길이 안전 컷


def parse_yml_value(text, section, key):
    """application-local.yml 에서 특정 top-level section 의 key 값을 추출."""
    cur = None
    for line in text.splitlines():
        if re.match(r"^[A-Za-z]", line):
            cur = line.split(":")[0].strip()
        m = re.match(r"^\s+" + re.escape(key) + r":\s*(.*)$", line)
        if m and cur == section:
            return m.group(1).strip().strip('"').strip("'") or None
    return None


def load_config():
    gemini_key = os.environ.get("GEMINI_API_KEY")
    qdrant_url = os.environ.get("QDRANT_URL")
    qdrant_key = os.environ.get("QDRANT_API_KEY")
    if not (gemini_key and qdrant_url):
        with open(YML_PATH, encoding="utf-8") as f:
            yml = f.read()
        gemini_key = gemini_key or parse_yml_value(yml, "gemini", "api-key")
        qdrant_url = qdrant_url or parse_yml_value(yml, "qdrant", "base-url")
        qdrant_key = qdrant_key or parse_yml_value(yml, "qdrant", "api-key")
    if not gemini_key:
        sys.exit("ERROR: GEMINI_API_KEY 를 찾을 수 없습니다.")
    if not qdrant_url:
        sys.exit("ERROR: qdrant base-url 을 찾을 수 없습니다.")
    return gemini_key, qdrant_url.rstrip("/"), qdrant_key


def http(method, url, body=None, headers=None):
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = urllib.request.Request(url, data=data, method=method, headers=headers or {})
    if data is not None:
        req.add_header("Content-Type", "application/json")
    with urllib.request.urlopen(req, timeout=60) as resp:
        raw = resp.read().decode("utf-8")
        return json.loads(raw) if raw else {}


def embed(text, gemini_key):
    url = f"{GEMINI_BASE}/models/{EMBED_MODEL}:embedContent?key={gemini_key}"
    body = {
        "model": f"models/{EMBED_MODEL}",
        "content": {"parts": [{"text": text}]},
        "outputDimensionality": EMBED_DIM,
        "taskType": "RETRIEVAL_DOCUMENT",
    }
    for attempt in range(6):
        try:
            res = http("POST", url, body)
            return res["embedding"]["values"]
        except urllib.error.HTTPError as e:
            if e.code == 429 and attempt < 5:      # rate limit → 점증 백오프
                time.sleep(5 * (attempt + 1))
                continue
            raise


def first(lst):
    return lst[0] if isinstance(lst, list) and lst else {}


def build_text_and_payload(d, folder):
    data = first(d.get("dtb_Data"))
    ntrt = first(d.get("dtb_Ntrt"))
    inrst = first(d.get("dtb_Inrst"))

    name = data.get("MKPD_KNM") or folder
    code = data.get("MKPD_CD") or ""

    lines = [f"상품명: {name}"]
    if data.get("MKPD_OTL_CNTN"):
        lines.append(f"개요: {data['MKPD_OTL_CNTN']}")
    if data.get("MKPD_KYWR_CNTN"):
        lines.append(f"키워드: {data['MKPD_KYWR_CNTN']}")

    # 상품 설명 항목들 (가장 핵심)
    for item in d.get("dtb_Ltiv", []):
        nm = (item.get("MKPD_LTIV_ITEM_NM") or "").strip()
        cntn = (item.get("MKPD_LTIV_ITEM_CNTN") or "").strip()
        if nm and cntn:
            lines.append(f"{nm}: {cntn}")

    # 금리 설명
    if ntrt.get("MKPD_LT_INRST_CNTN"):
        lines.append(f"금리정보: {ntrt['MKPD_LT_INRST_CNTN']}")

    text = "\n".join(lines)[:MAX_TEXT_CHARS]

    payload = {
        "productName": name,
        "productCode": code,
        "status": "SALE",                 # 챗봇 검색 필터 통과용
        "text": text,
        "source": "crawled",
        "folder": folder,
    }
    if data.get("MKPD_KYWR_CNTN"):
        payload["keywords"] = data["MKPD_KYWR_CNTN"]
    if data.get("MKPD_LRG_CLACD"):
        payload["categoryCode"] = data["MKPD_LRG_CLACD"]
    if inrst.get("BAS_INRST"):
        payload["baseRate"] = inrst["BAS_INRST"]
    if inrst.get("PDT_LWST_INRST"):
        payload["rateMin"] = inrst["PDT_LWST_INRST"]
    if inrst.get("PDT_HI_INRST"):
        payload["rateMax"] = inrst["PDT_HI_INRST"]
    return name, code, text, payload


def point_id(code, idx):
    try:
        return int(code)
    except (TypeError, ValueError):
        return idx + 1


def main():
    gemini_key, qdrant_url, qdrant_key = load_config()
    qheaders = {"api-key": qdrant_key} if qdrant_key else {}

    print(f"DATA_DIR : {DATA_DIR}")
    print(f"QDRANT   : {qdrant_url}  (collection={COLLECTION})")

    # 1) 컬렉션 생성 (코사인, 768). 이미 있으면 409 → 무시
    try:
        http("PUT", f"{qdrant_url}/collections/{COLLECTION}",
             {"vectors": {"size": EMBED_DIM, "distance": "Cosine"}}, qheaders)
        print(f"컬렉션 생성 완료 (size={EMBED_DIM}, Cosine)")
    except urllib.error.HTTPError as e:
        if e.code == 409:
            print(f"컬렉션 이미 존재 — 그대로 사용 ({COLLECTION})")
        else:
            raise

    # 2) 파일 순회 → 임베딩 → upsert
    files = sorted(
        os.path.join(r, "ssp_data.json")
        for r, _, fs in os.walk(DATA_DIR) if "ssp_data.json" in fs
    )
    print(f"대상 파일: {len(files)}개\n")

    ok, fail = 0, 0
    for idx, path in enumerate(files):
        folder = os.path.basename(os.path.dirname(path))
        try:
            with open(path, encoding="utf-8") as f:
                d = json.load(f)
            name, code, text, payload = build_text_and_payload(d, folder)
            vector = embed(text, gemini_key)
            point = {"id": point_id(code, idx), "vector": vector, "payload": payload}
            http("PUT", f"{qdrant_url}/collections/{COLLECTION}/points?wait=true",
                 {"points": [point]}, qheaders)
            ok += 1
            print(f"  [{ok+fail}/{len(files)}] OK  {name}")
            time.sleep(1.0)   # 분당 한도(429) 회피용 throttle
        except Exception as e:
            fail += 1
            print(f"  [{ok+fail}/{len(files)}] FAIL {folder} -> {e}")

    print(f"\n완료 — 성공 {ok}, 실패 {fail}")


if __name__ == "__main__":
    main()
