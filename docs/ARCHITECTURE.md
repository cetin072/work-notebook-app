# Architecture

## 기본 구조

```text
Existing Stable PWA (reference implementation)
`cetin072/worklog-voice-pwa`
        │
        │ 검증된 UX · 규칙 · 데이터 계약 · 테스트를 선별 이식
        ▼
Android App
  ├─ Compose UI
  ├─ Local Repository
  │   └─ Room / SQLite
  ├─ Media Storage
  │   ├─ Voice
  │   └─ Photos
  ├─ Widget
  └─ Optional Connectors (future)
      ├─ AI
      ├─ Notion
      └─ Calendar
```

## 핵심 원칙

- 로컬 DB가 기본 Source of Truth다.
- 네트워크 연결 여부와 무관하게 기록·조회·수정·완료가 가능해야 한다.
- 사진/음성은 로컬 파일로 저장하고 DB에는 참조 경로와 메타데이터를 저장한다.
- 위젯은 로컬 DB를 읽어 오늘 업무를 표시한다.
- 외부 서비스 연결은 코어 데이터 모델과 분리된 Adapter/Connector 계층으로 추가한다.
- 기존 웹앱에서 이미 실사용 검증된 UX와 규칙은 새로 발명하지 않고 우선 재사용/이식한다.
- JavaScript/Netlify 의존 코드를 무조건 복사하지 않고, 플랫폼 중립적인 **동작 계약**을 Kotlin/Android 방식으로 옮긴다.

## 권장 Android 기술 방향

초기 상용화 버전은 **Kotlin + Jetpack Compose + Room** 기반 네이티브 앱을 권장한다.

이유:
- 홈 화면 위젯 구현이 중요함
- 카메라/파일/오프라인 저장이 핵심임
- 장기적으로 Android 시스템 기능과의 결합이 많음
- 기존 웹앱을 그대로 감싼 단순 WebView보다 로컬 기능 확장성이 좋음

## 기존 웹앱과의 관계

기존 `worklog-voice-pwa`는 별도 운영 안정판으로 유지하며 이 저장소로 통째로 이전하지 않는다.

대신 다음을 reference implementation으로 취급한다.
- 말하기 중심의 빠른 기록 UX
- 작성 중 초안 보존/복구
- 음성 결과 중복 병합
- 중지 후 마지막 음성까지 반영하고 저장
- 여러 업무 분리 규칙
- 사용자 수동 선택 보호
- 업무 데이터 계약
- 브리핑의 TOP/오늘/다가오는 일정/확인 필요 모델
- 완료/실행취소 UX
- 회귀 테스트와 모바일 검수 시나리오

구체적인 재사용 판정은 `WEBAPP_REUSE_AUDIT.md`를 기준으로 한다.

## 단계별 구현

### MVP 1 — Local Core
- Compose 기본 화면
- Room WorkItem 저장/조회/수정
- 완료/미완료
- 날짜/시간
- 작성 중 draft 보호
- 오늘 할 일 조회
- 완전 오프라인 동작

### MVP 2 — Voice Port
- Android 음성 입력
- 기존 웹앱의 `mergeWithOverlap` 동작 이식
- 중지→마지막 결과 확정→저장
- 필요 시 연속 듣기/다건 분리 포팅

### MVP 3 — Widget
- 로컬 DB 기반 오늘 업무 표시
- 기록 진입 바로가기

### MVP 4 — Camera
- 사진 원본 로컬 저장
- 기록에 첨부
- OCR/AI 자동정리는 후속 Smart 단계

### Later — Optional Connectors
- 개인 AI
- 개인 Notion
- Calendar
- 선택적 백업/동기화
