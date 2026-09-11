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

## 2. GitHub가 개발 Source of Truth

- 확정된 기획: `docs/`
- 해야 할 일: GitHub Issues
- 구현 변경: 별도 branch + Pull Request
- 검수 후 merge

채팅 내용만으로 중요한 결정이 남지 않도록 확정 사항은 `DECISIONS.md`에 기록한다.

## 3. 작은 단위로 개발

한 번에 전체 앱을 만들지 않는다.

기본 순서:
1. 한 가지 목표를 Issue로 정의
2. 작은 branch 생성
3. 구현
4. 빌드/테스트
5. 실기기 검수 가능 상태 만들기
6. 승인 후 merge

## 4. 초기 외부 서비스 최소화

Local MVP 단계에서는 불필요한 Supabase/Firebase/Notion/AI API/자동화 서비스를 추가하지 않는다.

외부 서비스는 실사용 필요가 확인될 때만 도입한다.

## 5. 데이터 안정성 우선

- 사용자 기록 저장 실패를 조용히 무시하지 않는다.
- 원본 음성/사진을 임의 삭제하지 않는다.
- DB migration이 생기면 기존 데이터 보존을 우선한다.
- 개발용 테스트가 실사용 웹앱 데이터에 영향을 주지 않도록 분리한다.
