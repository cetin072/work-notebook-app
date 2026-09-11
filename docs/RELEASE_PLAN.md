# Release Plan

## 운영 전제

현재 실사용 웹앱 `cetin072/worklog-voice-pwa`는 계속 Production으로 유지한다.

새 상용화 앱은 이 저장소 `cetin072/work-notebook-app`에서 별도 개발한다.

두 저장소는 개발 중 섞지 않는다.

개발자가 기능을 추가할 때마다 사용자가 APK를 직접 설치하는 방식은 기본 운영으로 사용하지 않는다.

## 단계

### 0. Planning
- 핵심 문서 확정
- 로컬 데이터 모델 확정
- Android 기술 구조 확정

### 1. Local MVP
- 업무 생성/조회/수정/완료
- Room/SQLite 저장
- 오프라인 동작

### 2. Capture
- 음성 기록
- 사진 촬영/첨부

### 3. Widget
- 4×2 오늘 업무 위젯
- 음성/사진 바로가기

### 4. Beta Delivery
- GitHub Actions 자동 빌드/테스트
- release AAB 빌드 검증
- 작은 UI 변화는 별도 Preview/스크린샷으로 확인
- 실기기 배포는 Google Play 내부테스트를 기본으로 사용
- 사용자는 큰 Beta 체크포인트에서만 Play 스토어 업데이트 후 검수

### 5. Private Beta
- 3~5명 실제 사용
- 설치 난이도, 기록 누락, 데이터 안정성, 위젯 사용성 확인
- 각 사용자의 데이터는 각자의 휴대폰 로컬 DB가 원본

### 6. Smart Extensions
- OCR
- AI 연결
- 개인 Notion 연결
- 캘린더 연결
- 로컬 일일 브리핑 고도화

### 7. Commercialization Review
- 개인정보처리방침
- 백업/복구
- 다중기기/동기화 필요성
- 선택형 클라우드 구조
- AI 제공자/비용 구조
- 과금 여부
- 정식 Google Play 출시 준비

## 테스트 배포 원칙

- 직접 APK 설치는 긴급 확인용 예외 경로로만 사용한다.
- 실기기에서만 확인 가능한 마이크·위젯·카메라·권한 동작은 Beta 체크포인트마다 확인한다.
- Google Play 내부테스트 최초 설정 전까지는 GitHub CI 결과로 기능을 누적 검증한다.
- 자세한 절차는 `docs/PLAY_INTERNAL_TESTING.md`를 따른다.

## 교체 원칙

새 앱이 현재 웹앱보다 안정적이라고 확인되기 전에는 기존 웹앱을 종료하거나 이전하지 않는다.

전환 시에도 일정 기간 병행 사용 후 결정한다.
