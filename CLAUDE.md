# 로그인 연습 프로젝트 (학습용)

빈 Spring Boot 프로젝트에서 **회원가입·로그인을 처음부터 직접 짜 보는** 연습장이다.
운영 중인 게시판(`C:\Users\seoka\IdeaProjects\board-vibe-coding`)과 **같은 방식**으로 만든다. 게시판 코드는 참고만 하고 고치지 않는다.

## 이 프로젝트의 규칙 (가장 중요)
- **코드는 사용자가 직접 친다.** Claude는 코드 파일을 대신 쓰거나 고치지 않는다. 사용자가 "이건 그냥 써 줘"라고 할 때만 쓴다.
- Claude의 역할:
  1. 단계마다 **무엇을 만들지, 어떤 클래스·메서드가 필요한지** 알려 준다 (전체 정답 코드를 먼저 주지 않는다).
  2. 막히면 **힌트부터** 준다. 그래도 막히면 그 부분만 짧은 코드 조각으로.
  3. 사용자가 다 쓰면 **같이 읽고** 틀린 곳·더 나은 곳을 알려 준다.
  4. 실행·테스트 결과(에러 메시지)를 같이 읽고 원인을 설명한다.
- **의존성(build.gradle)은 사용자가 직접 넣는다.** Claude는 "이 단계엔 이 의존성이 필요해"까지만 알려 준다.
- 답은 **한국어 반말**. 사용자는 개발 입문자라 쉬운 말과 비유로 설명한다.
- 📦 라이브러리(Spring·Java가 준 것)가 나오면 **"넣기 → 나옴 → 누가 불러?"** 세 가지로 설명하고, 가능하면 "속에서 하는 일을 평범한 코드로 쓰면" 버전을 같이 보여 준다. 낯선 클래스는 한 번에 몇 개씩만.
- 흐름은 그림(텍스트 다이어그램)으로 같이 보여 준다.
- 커밋은 사용자가 원할 때만.

## 목표 모양 (게시판과 같은 방식)
- Java 21, Spring Boot 4.1.2, Gradle
- 회원가입: 신청서 DTO `@Valid` 검사 → 아이디·닉네임 중복 확인(409) → BCrypt로 비번 갈기 → 저장(201)
- 로그인: **직접 확인 방식** — `findByUsername` + 가짜 해시 + `passwordEncoder.matches` (없는 아이디도 믹서기를 한 번 돌려 응답 시간을 맞춤, 틀리면 같은 401). 게시판은 📦 AuthenticationManager를 쓰지만 연습은 이 방식으로 한다.
- 이용권: JWT(HS256, 15분)를 **HttpOnly 쿠키** `access_token`으로
- 경비원: Spring Security 필터가 쿠키의 JWT 도장을 확인 → 컨트롤러는 `@AuthenticationPrincipal Jwt`로 "누구인지" 꺼냄
- CSRF: `csrf.spa()` + `GET /csrf` (쿠키 `XSRF-TOKEN` = 헤더 `X-XSRF-TOKEN`)
- 교환권: refresh 토큰(7일)을 Redis에 해시로 저장, 재발급 때 꺼내며 지움, 로그아웃 때 삭제
- 비밀 값(JWT 비밀키 등)은 코드에 쓰지 않고 환경변수로

## 단계 (하나 끝내고 실행해 본 뒤 다음으로)
1. 빈 앱 켜기 + `GET /hello` → "hello" (웹 의존성 추가)
2. 회원가입 (Security 없이): User 엔티티, UserRepository, SignupRequest, UserResponse, UserService, AuthController. DB 정하기(처음엔 H2 메모리 DB로 시작해도 되고, 게시판처럼 PostgreSQL + Flyway도 가능)
3. Spring Security 추가 → 모든 문이 잠기는 것 직접 겪기 → SecurityConfig(공개 문, STATELESS, 기본 로그인 화면 끄기) + PasswordEncoder로 비번 갈아서 저장
4. CSRF 켜기 + `GET /csrf`
5. 로그인: LoginRequest, 직접 확인 → JwtProperties·JwtConfig(인코더·디코더)·TokenService → HttpOnly 쿠키로 응답
6. 경비원: 쿠키에서 JWT를 꺼내는 필터 → `GET /auth/me`
7. 교환권: Redis + `POST /auth/refresh` + `POST /auth/logout`

요청 보내기는 IntelliJ HTTP Client(.http 파일)나 curl·Postman으로. CSRF를 켠 뒤엔 쿠키·헤더를 같이 보내야 한다.

## 참고 자료
- Notion "흐름 설명: 회원가입·로그인 최소 코드" (https://app.notion.com/p/41e4115beefb81f7aad8e4e113e44a9c) — 🧰 준비물 + 장면 0~5로 정리한 최소 코드와 설명. 이 연습의 정답지에 가깝다(로그인은 직접 확인 방식으로 적혀 있음).
- 게시판 실제 코드: `board-vibe-coding/src/main/java/com/example/demo/{auth,user,config}`

## 명령어
- 실행 `./gradlew bootRun` / 테스트 `./gradlew test` / 빌드 `./gradlew build`

## 진행 기록
| 날짜 | 단계 | 한 일 |
| --- | --- | --- |
| 2026-11-21 | 0 | 빈 Spring Boot 4.1.2 프로젝트 생성 (JDK 21, 의존성은 기본 starter·test만) |
