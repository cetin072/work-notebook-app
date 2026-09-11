# Local Data Model

## WorkItem

필수/기본 필드 제안:

- `id`: UUID
- `title`: 문자열
- `content`: 문자열
- `workDate`: 날짜, nullable
- `workTime`: 시간, nullable
- `isCompleted`: boolean
- `createdAt`: datetime
- `updatedAt`: datetime

## Attachment

- `id`: UUID
- `workItemId`: WorkItem 참조
- `type`: `PHOTO` | `VOICE`
- `localUri`: 로컬 파일 경로/URI
- `mimeType`: 문자열
- `createdAt`: datetime

## 설계 원칙

- 날짜와 시간은 없어도 저장 가능하다.
- AI가 없어도 모든 필드는 사용자가 직접 수정할 수 있다.
- 사용자 원문은 덮어쓰지 않는 방향을 우선한다.
- 외부 동기화용 ID는 나중에 별도 테이블/필드로 확장한다.
- 현재 웹앱의 Notion 스키마를 그대로 복제하지 않는다. 상용 앱 코어는 더 단순하게 시작한다.

## 추후 확장 후보

- `rawText`
- `aiCleanedText`
- `priority`
- `reminderAt`
- `externalLinks`
- `syncState`
- `briefingTags`

초기 Beta에서는 필요성이 확인되기 전까지 추가하지 않는다.
