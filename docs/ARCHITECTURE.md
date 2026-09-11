# Architecture

## 기본 구조

```text
Android App
  ├─ UI
  ├─ Local Repository
  │   └─ Room / SQLite
  ├─ Media Storage
  │   ├─ Voice
  │   └─ Photos
  ├─ Widget
  └─ Optional Connectors (future)
      ├─ AI
      ├─ Notion
      └─ Calendar
```

## 핵심 원칙

- 로컬 DB가 기본 Source of Truth다.
- 네트워크 연결 여부와 무관하게 기록·조회·수정·완료가 가능해야 한다.
- 사진/음성은 로컬 파일로 저장하고 DB에는 참조 경로와 메타데이터를 저장한다.
- 위젯은 로컬 DB를 읽어 오늘 업무를 표시한다.
- 외부 서비스 연결은 코어 데이터 모델과 분리된 Adapter/Connector 계층으로 추가한다.

## 권장 Android 기술 방향

초기 상용화 버전은 **Kotlin + Jetpack Compose + Room** 기반 네이티브 앱을 권장한다.

이유:
- 홈 화면 위젯 구현이 중요함
- 카메라/파일/오프라인 저장이 핵심임
- 장기적으로 Android 시스템 기능과의 결합이 많음
- 기존 웹앱을 그대로 감싼 단순 WebView보다 로컬 기능 확장성이 좋음

기존 `worklog-voice-pwa`는 별도 운영 안정판으로 유지하며 이 저장소로 이전하지 않는다.
