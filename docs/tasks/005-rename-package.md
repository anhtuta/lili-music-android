# 005 — Rename application package to com.anhtuta.lilimusic

## Goal

The Android applicationId, Gradle namespaces, Kotlin packages, and source directories use `com.anhtuta.lilimusic` instead of `com.nenmongvietnam.lilimusic`. The Lumen backend URL `https://blog.nenmongvietnam.com/laravel/public` is unchanged.

## Approach

Mechanical rename on the open Phase 1 branch (PR #14). `git mv` source trees (`java/com/nenmongvietnam` → `java/com/anhtuta`) so Git keeps history. Then update `package`/`import` statements, `applicationId`, every module `namespace`, Hilt/Room/manifest-relative names (most stay `.ClassName`), and docs that state the package.

No convention plugins exist in this repo. No new features. Do not rewrite `blog.nenmongvietnam.com`.

## Files

- `app/` and every `:core:*` / `:feature:*` `build.gradle.kts` with `namespace`
- `**/src/{main,test,androidTest}/java/com/nenmongvietnam/` → `.../com/anhtuta/`
- Kotlin `package` / `import` lines
- `AGENTS.md`, `docs/PLAN.md`, `docs/tasks/001-project-init.md` (package mentions)

## Tests

Existing unit tests must keep passing. `BuildConfigTest` asserts the new `APPLICATION_ID`. After the move, `./gradlew check` must pass and `com.nenmongvietnam.lilimusic` must not appear as a package/namespace.

## Done when

- Task plan committed before the rename.
- No `com.nenmongvietnam.lilimusic` package/namespace/`applicationId` remains.
- Backend URL still points at `blog.nenmongvietnam.com`.
- `./gradlew check` passes.
