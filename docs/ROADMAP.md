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
- 후속으로 연속 듣기/다건 업무 분리 검토

관련 Issue: #2

## MVP 3 — Widget

- 4×2 오늘 업무 위젯
- 오늘 미완료 업무 최대 3개
- 음성/사진 빠른 실행 진입점
- 로컬 DB 기반 오프라인 표시

관련 Issue: #3

## MVP 4 — Camera

- 사진 촬영
- 업무 기록에 로컬 사진 첨부
- 재실행 후 첨부 유지
- OCR/AI 없이도 기록 자산으로 사용

관련 Issue: #4

## Beta

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
