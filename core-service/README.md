# core-service

Everything from the original `backend` monolith **except** login
(`/api/auth/**`, now in `auth-service`). Same packages, same endpoints,
same business logic — nothing else was rewritten.

## What changed vs. the original backend
- `auth.controller`, `auth.service`, `auth.dto`, `auth.config` (the
  login endpoint + `DefaultAdminInitializer`) were removed — they live in
  `auth-service` now. `auth.entity.User` and `auth.repository.UserRepository`
  were **kept**, because principal/admin management, faculty self-service,
  scheduler, etc. still read/write users directly.
- `SecurityConfig` no longer has a `/api/auth/**` rule (that controller
  doesn't exist here). It still runs `JwtAuthenticationFilter`, so any
  valid token — including ones issued by `auth-service` — is accepted.
- Everything else (`academicyear`, `attendance`, `block`, `branch`,
  `curriculum`, `curriculumsubject`, `dashboard`, `faculty`,
  `facultyassignment`, `facultyavailability`, `leave`, `lecture`,
  `principal`, `progress`, `regulation`, `report`, `room`, `scheduler`,
  `studentclass`, `subject`, `substitute`, `workload`, `common`) is
  unchanged.

## Database
Same MySQL database as `auth-service` (`smartsched`). No schema changes.

**Important:** `jwt.secret` must be identical to `auth-service`'s, since
this service only *validates* tokens — it never issues them anymore.

## Run
```
Port: 8080
mvn spring-boot:run
```
or run `CoreServiceApplication` from IntelliJ.

## Frontend note
Point every request except login/change-password at
`http://localhost:8080` (unchanged from before). Point login and
change-password at `auth-service` on `http://localhost:8081`.
