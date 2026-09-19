# auth-service

The login/authentication microservice split out of the original `backend`
monolith. Owns `/api/auth/**` only (`POST /api/auth/login`,
`POST /api/auth/change-password`) and issues the JWTs that `core-service`
validates.

## What's inside
- `auth.*` — controller, service, dto, repository, entity (`User`), and
  `DefaultAdminInitializer` (creates the default `principal` / `admin123`
  account on first boot, same as before).
- `security.*` — JWT generation + validation, Spring Security config,
  CORS config. Trimmed down to only what login needs (no `CurrentUserService`,
  no per-role URL matchers for the rest of the app).
- `common.*` — copied as-is (exceptions, `ApiResponse`, enums, etc.).
- `branch.entity.Branch` and `faculty.entity.Faculty` — kept **only as JPA
  entity classes** (no controller/service/repository) because `User` has
  `@ManyToOne` relations to both. This is required for Hibernate to map the
  `users` table correctly; it does not add any Branch/Faculty business logic
  to this service.

## Database
Same MySQL database as `core-service` (`smartsched`), same tables
(`users`, `branches`, `faculties`, ...). Nothing was changed in the schema —
both services just read/write the same DB, as requested.

**Important:** `jwt.secret` in `application.properties` must stay identical
between `auth-service` and `core-service`, since tokens are minted here and
validated over there.

## Run
```
Port: 8081
mvn spring-boot:run
```
or run `AuthServiceApplication` from IntelliJ.

## Frontend note
Your React app currently points every API call (including `/api/auth/**`)
at one base URL. After this split you'll need to point `authService.js` /
`axios.js`'s login & change-password calls at `http://localhost:8081`,
while everything else still goes to `core-service` on `http://localhost:8080`
(or put both behind a gateway/reverse proxy on one host later).
