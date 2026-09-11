# Development Rules

## 1. 두 제품을 분리한다

### 현재 실사용 웹앱
- Repository: `cetin072/worklog-voice-pwa`
- 현재 사용 중인 Production을 유지한다.
- 상용화 개발을 이유로 구조를 대규모 변경하지 않는다.
- 긴급 버그/운영상 꼭 필요한 수정만 별도 PR로 처리한다.

### 상용화 앱
- Repository: `cetin072/work-notebook-app`
- Android Local-first 앱 개발은 여기서만 진행한다.

## 2. 새로 만들기 전에 기존 자산을 확인한다

상용 앱에서 어떤 기능을 구현하기 전에 `worklog-voice-pwa`에 이미 같은 목적의 UX, 규칙, 데이터 계약, 테스트가 있는지 먼저 확인한다.

판정은 네 가지로 기록한다.
- **재사용**: 브랜드/문구/테스트 데이터처럼 직접 가져올 수 있음
- **이식**: 검증된 동작을 Kotlin/Android 방식으로 포팅
- **나중**: 외부 Connector 단계까지 보류
- **제외**: 새 Local-first 제품에 불필요

기준 문서: `docs/WEBAPP_REUSE_AUDIT.md`

HTML/CSS/JavaScript 전체를 무조건 복제하거나 단순 WebView 앱을 기본 구조로 삼지 않는다. 반대로 이미 검증된 사용 흐름을 이유 없이 새로 설계하지도 않는다.

## 3. GitHub가 개발 Source of Truth

- 확정된 기획: `docs/`
- 해야 할 일: GitHub Issues
- 구현 변경: 별도 branch + Pull Request
- 검수 후 merge

채팅 내용만으로 중요한 결정이 남지 않도록 확정 사항은 `DECISIONS.md`에 기록한다.

## 4. 작은 단위로 개발

한 번에 전체 앱을 만들지 않는다.

기본 순서:
1. 기존 웹앱의 관련 기능/테스트 확인
2. 한 가지 목표를 Issue로 정의
3. 작은 branch 생성
4. 구현
5. 빌드/테스트
6. 실기기 검수 가능 상태 만들기
7. 승인 후 merge

## 5. 초기 외부 서비스 최소화

Local MVP 단계에서는 불필요한 Supabase/Firebase/Notion/AI API/자동화 서비스를 추가하지 않는다.

외부 서비스는 실사용 필요가 확인될 때만 도입한다.

## 6. 데이터 안정성 우선

- 사용자 기록 저장 실패를 조용히 무시하지 않는다.
- 원본 음성/사진을 임의 삭제하지 않는다.
- DB migration이 생기면 기존 데이터 보존을 우선한다.
- 개발용 테스트가 실사용 웹앱 데이터에 영향을 주지 않도록 분리한다.
- 새 앱 개발/테스트 중 기존 `worklog-voice-pwa` Production과 Notion 실데이터를 파괴적으로 사용하지 않는다.

## 7. 기존 웹앱은 기준 구현으로 유지한다

새 Android 앱이 안정될 때까지 현재 업무수첩은 계속 실사용한다.

새 앱이 기존 앱보다 충분히 안정되었다고 판단되더라도 즉시 폐기하지 않고 일정 기간 병행 사용 후 전환 여부를 결정한다.
