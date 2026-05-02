# Test Results Fix

## What Failed
Three main issues caused the tests to fail globally:
1. **BadCredentialsException / BCrypt Password Mismatch:** Seeded demo user entries contained password hashes starting with `$2a$10$7EqJ...`, which contained a `+` character. The standard BCrypt Base64 alphabet does not support `+`, resulting in `Encoded password does not look like BCrypt` alongside auth failures.
2. **Duplicate Bean Definition:** `NoUniqueBeanDefinitionException` on `UserDetailsService`. `PipelineXUserDetailsService` was correctly annotated with `@Service`, making it auto-discoverable. Still, `SecurityConfig` manually exposed an identical `@Bean`, confusing the context.
3. **PostgreSQL Function `lower(bytea) does not exist`:** `LeadRepositoryIntegrationTest` failed because the `lower(concat('%', :query, '%'))` expression caused PostgreSQL JDBC to treat null arguments as untyped `bytea` rather than strings passing it to `lower()`.

## Root Cause
1. **Invalid BCrypt hashing format** stored in database seed data.
2. **Redundant component declarations** in Spring configuration class over-registering `UserDetailsService`.
3. **JPQL function type inference errors** passing null parameters inside nested string functions in native DB implementation (PostgreSQL).

## Files Changed
- `src/main/resources/db/migration/V2__seed_demo_data.sql`: Replaced invalid password hashes with verified BCrypt representations for "password" (`$2a$12$XE7LKBkBq706NqUBnEoZL.6vnQzcP2BPf/TRqMaC0TY2.wlGIaS9O`).
- `src/main/java/com/pipelinex/shared/config/SecurityConfig.java`: Removed redundant `userDetailsService` bean declaration.
- `src/main/java/com/pipelinex/leads/LeadRepository.java`: Removed `lower()` and `concat()` wrappers around `@Query` search parameters. Expected pre-lowercased/pre-concatenated patterns instead.
- `src/main/java/com/pipelinex/leads/LeadService.java`: Standardized all queries to map search patterns via backend (`"%" + query.toLowerCase() + "%"` and `.toLowerCase()`).

## How to Rerun Tests
```bash
mvn clean test
```

## Expected Output
All 8 integration tests should now execute without failures.

## Where Surefire Reports Are Stored
Surefire execution reports are stored in:
- `target/surefire-reports/`
- Look for `.txt` and `.xml` formats containing test class outputs.