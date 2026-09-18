# 웹 빠른 Preview

## 목적

Android 앱 개발 중 화면/문구/사용 흐름을 확인할 때마다 APK를 설치하지 않기 위한 보조 Preview다.

## 위치

- 소스: `preview/index.html`
- 전용 Netlify 프로젝트: `work-notebook-beta-preview`
- Netlify site ID: `765e90bc-ff58-424c-af73-b29d2adc094a`

이 사이트는 기존 실사용 `worklog-voice-pwa`와 완전히 별도다.

## Preview에서 확인할 것

- 오늘 브리핑 배치
- 빠른 기록 화면
- 날짜/시간 입력 흐름
- 완료 처리
- 전체 기록 배치
- `다음 업무 / 다음 건 / 그다음` 다건 분리 흐름

브라우저 localStorage만 사용하며 실제 업무 데이터와 연결하지 않는다.

## Preview에서 확인하지 않는 것

- Room/SQLite 실제 저장
- Android 음성인식
- Android 홈 위젯
- 카메라/FileProvider
- 런타임 권한
- Play Store 업데이트

위 기능은 Google Play 내부테스트 체크포인트에서 실제 Galaxy로 확인한다.

## 배포 원칙

Preview 사이트에 올리는 코드는 데모 UI만 포함한다. Notion 토큰, GitHub 토큰, AI 키, 카카오 정보, 실제 업무 데이터는 넣지 않는다.
