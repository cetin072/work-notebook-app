# 업무수첩 Android

`work-notebook-app`은 현재 실사용 중인 `cetin072/worklog-voice-pwa`의 검증된 기록 UX를 계승하면서, Android 네이티브 Local-first 구조로 확장하는 상용화 앱 저장소입니다.

## 현재 목표

MVP 1에서는 인터넷·로그인·Notion·AI 없이 다음이 동작해야 합니다.

- 업무 직접 입력
- 날짜/시간 선택 입력
- Room/SQLite 로컬 저장
- 오늘 미완료 업무 보기
- 전체 기록 보기
- 수정
- 완료/미완료 전환
- 작성 중 Draft 자동 복구
- 앱 재실행 후 데이터 유지

음성, 위젯, 카메라는 후속 MVP에서 기존 웹앱의 검증된 흐름을 기준으로 순차 이식합니다.

## 저장소 역할 분리

- 기존 실사용 웹앱: `cetin072/worklog-voice-pwa`
- 상용화 Android 앱: `cetin072/work-notebook-app`

기존 Production 웹앱은 계속 실사용 안정판으로 유지하며, 이 저장소의 개발 때문에 변경하지 않습니다.

## 기준 문서

- `docs/PRODUCT_VISION.md`
- `docs/BETA2_SCOPE.md`
- `docs/ARCHITECTURE.md`
- `docs/LOCAL_DATA_MODEL.md`
- `docs/WEBAPP_REUSE_AUDIT.md`
- `docs/DEVELOPMENT_RULES.md`
- `docs/ROADMAP.md`
- `docs/DECISIONS.md`

## 개발 흐름

GitHub Issue → 별도 branch → 자동 빌드/테스트 → PR → 실기기 검수 → 승인 후 merge 순서를 기본으로 합니다.
