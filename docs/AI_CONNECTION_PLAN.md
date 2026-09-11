# AI Connection Plan

## 제품 원칙

사용자에게는 특정 회사명보다 **AI 연결**이라는 개념으로 보인다.

AI는 선택 기능이며, 연결하지 않아도 업무수첩의 기본 기록 기능은 완전히 동작해야 한다.

## 단계별 계획

### Phase 1 — Share to AI

앱이 선택한 업무/오늘 기록을 정리된 텍스트로 만들어 Android 공유 기능으로 사용자가 설치한 AI 앱에 보낸다.

지원 개념:
- ChatGPT
- Gemini
- Claude
- 기타 공유 대상

이 단계에서는 별도 AI 로그인/OAuth/API를 구현하지 않는다.

### Phase 2 — Result Back

AI에서 정리한 결과를 공유/복사 방식으로 업무수첩에 다시 가져와 브리핑이나 메모로 저장하는 흐름을 검토한다.

### Phase 3 — Automatic Connectors

실사용 수요가 확인되면 공급자별 Adapter를 추가한다.

```text
업무수첩 Core
  └─ AI Bridge
      ├─ OpenAI Adapter
      ├─ Gemini Adapter
      └─ Claude Adapter
```

## 장기 자동화 후보

- 오타/띄어쓰기 보정
- 업무 분리
- 날짜/시간 추출
- 일정 후보 생성
- 요약
- 우선순위 판단
- 일일 브리핑
- 방치된 업무/후속조치 탐지

사용자 원문은 가능한 보존하고 AI 결과와 구분한다.
