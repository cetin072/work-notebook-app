# Roadmap

## MVP 1 — Local Core

- 기존 `worklog-voice-pwa` UX/데이터 계약을 reference로 사용
- Android project bootstrap
- WorkItem Room entity/DAO/repository
- 오늘 화면
- 직접 입력 → 저장
- 기록 생성/조회/수정/완료
- 작성 중 Draft 보존/복구
- 오프라인 저장 검증

관련 Issue: #1

## MVP 2 — Voice Port

- 기존 웹앱의 말하기 중심 흐름 이식
- Android 음성 입력
- 음성 조각 중복 병합(`mergeWithOverlap` 동작 계승)
- 중지 후 마지막 결과 반영 → 로컬 저장
- 연속 듣기 재시작

관련 Issue: #2

## MVP 3 — Widget

- 4×2 오늘 업무 위젯
- 지연 + 오늘 핵심 업무 최대 3개
- 음성/사진 빠른 실행 진입점
- 로컬 DB 기반 오프라인 표시

관련 Issue: #3

## MVP 4 — Camera

- 사진 촬영
- 업무 기록에 로컬 사진 첨부
- 재실행 후 첨부 유지
- OCR/AI 없이도 기록 자산으로 사용

관련 Issue: #4

## MVP 5 — Local Briefing

- AI 없이 로컬 규칙으로 오늘 브리핑
- 지연 → 오늘 시간 업무 → 오늘 일반 업무 순
- 핵심 행동 최대 3건
- 홈 카드와 위젯에 동일한 재노출 원칙 적용

관련 Issue: #11

## MVP 6 — Multi-work Split

- 기존 웹앱의 `다음 업무 / 다음 건 / 그다음` 흐름 이식
- 한 번의 음성/직접 입력을 여러 WorkItem으로 저장
- 사진 첨부/기존 기록 수정은 자동 분리 제외

관련 Issue: #13

## Beta Distribution

- 개발 커밋마다 APK 직접 설치하지 않음
- GitHub Actions에서 자동 빌드/테스트
- 큰 체크포인트만 Google Play 내부테스트로 실기기 검수
- release AAB 빌드 검증

관련 Issue: #7

## Private Beta

- 3~5명 실사용
- 기록 누락/데이터 보존/설치 난이도/위젯 사용성 검수
- 기존 웹앱과 병행 사용하면서 기능/속도 비교

## Later — Smart/Connect

- OCR
- AI 연결
- Notion 연결
- 캘린더 연결
- 백업/복구
- 다중기기 동기화

## 운영 원칙

기존 `cetin072/worklog-voice-pwa` Production은 상용 앱 개발 기간 동안 계속 실사용 안정판으로 유지한다. 새 Android 앱이 충분히 안정되기 전에는 기존 업무 흐름을 강제 전환하지 않는다.
