# Release Plan

## 운영 전제

현재 실사용 웹앱 `cetin072/worklog-voice-pwa`는 계속 Production으로 유지한다.

새 상용화 앱은 이 저장소 `cetin072/work-notebook-app`에서 별도 개발한다.

두 저장소는 개발 중 섞지 않는다.

## 단계

### 0. Planning
- 핵심 문서 확정
- 로컬 데이터 모델 확정
- Android 기술 구조 확정

### 1. Local MVP
- 앱 설치
- 업무 생성/조회/수정/완료
- Room/SQLite 저장
- 오프라인 동작

### 2. Capture
- 음성 메모
- 사진 촬영/첨부

### 3. Widget
- 4×2 오늘 업무 위젯
- 음성/사진 바로가기

### 4. Private Beta
- 3~5명 실제 사용
- 설치 난이도, 기록 누락, 데이터 안정성, 위젯 사용성 확인

### 5. Smart Extensions
- OCR
- AI 연결
- 개인 Notion 연결
- 캘린더 연결

### 6. Commercialization Review
- Google Play 배포 방식
- 개인정보처리방침
- 백업/복구
- 다중기기/동기화 필요성
- 과금 여부

## 교체 원칙

새 앱이 현재 웹앱보다 안정적이라고 확인되기 전에는 기존 웹앱을 종료하거나 이전하지 않는다.

전환 시에도 일정 기간 병행 사용 후 결정한다.
