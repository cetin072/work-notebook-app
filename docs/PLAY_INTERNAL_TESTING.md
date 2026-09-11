# Google Play 내부테스트 운영

## 목적

개발할 때마다 APK를 직접 내려받아 `알 수 없는 앱 설치`를 반복하지 않는다.

실기기 검수는 의미 있는 Beta 체크포인트에서만 진행하고, 배포는 Google Play 내부테스트 트랙을 기본으로 한다.

## 운영 흐름

1. ChatGPT/GitHub에서 기능 개발
2. GitHub Actions에서 단위 테스트 + debug APK + release AAB 빌드 검증
3. 작은 UI/흐름 변경은 웹/스크린샷/코드 검수로 먼저 확인
4. Beta 체크포인트가 되면 signed AAB 생성
5. Google Play 내부테스트 트랙에 업로드
6. 테스터는 Play 스토어에서 설치 또는 업데이트
7. 실제 Galaxy에서 마이크·위젯·카메라 등 네이티브 기능만 집중 검수

## 사용자에게 필요한 최초 1회 설정

아래 항목은 계정 소유자의 본인확인·약관 동의가 필요하므로 자동화하지 않는다.

- Google Play Console 개발자 계정 준비
- 개발자 본인확인 및 필요한 등록 절차 완료
- `업무수첩 Beta` 앱 최초 생성
- 패키지명 `com.cetin072.worknotebook` 확인
- Play App Signing 사용 설정
- 내부테스트 트랙 생성
- 최초 테스터 계정 등록

## 서명 원칙

GitHub 저장소에는 다음을 절대 커밋하지 않는다.

- keystore 파일
- keystore 비밀번호
- key alias 비밀번호
- Play API 서비스계정 JSON

Beta 배포 자동화를 붙일 때는 GitHub Secrets 또는 이에 준하는 안전한 비밀 저장소만 사용한다.

예정 Secret 이름은 다음처럼 통일한다.

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
- 향후 자동 Play 업로드 시 `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`

## 현재 CI의 AAB 의미

현재 CI에서 만드는 `work-notebook-beta-unsigned-aab`는 **release 빌드가 깨지지 않는지 확인하는 검증용**이다.

Google Play에 실제 업로드하기 전에는 별도의 안전한 upload key로 서명된 AAB가 필요하다.

## 버전 원칙

- Beta 0.1: Local-first + 직접입력 + 음성
- Beta 0.2: 오늘 위젯 + 카메라 사진 기록
- 이후 큰 기능 묶음마다 versionCode 증가
- 같은 Beta 안의 작은 수정은 사용자가 매번 설치하지 않는다.

## 기존 웹 업무수첩

현재 실사용 `cetin072/worklog-voice-pwa`는 Android Beta와 별개로 계속 Production 유지한다.

Android Beta가 충분히 안정적이라고 확인되기 전까지 기존 웹앱을 종료하거나 이전하지 않는다.
