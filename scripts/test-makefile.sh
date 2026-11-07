#!/usr/bin/env bash
# Dry-run checks for the root Makefile. Does not assemble or need an SDK.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

fail() {
  echo "test-makefile: $*" >&2
  exit 1
}

help_out="$(make help)"
echo "$help_out" | grep -q 'Usage: make <target>' || fail "help missing usage line"

targets="help doctor build apk release-apk bundle install test lint check clean"
for target in $targets; do
  echo "$help_out" | grep -Eq "^  ${target} " || fail "help missing target ${target}"
done

phony="$(grep -E '^\.PHONY:' Makefile)"
for target in $targets; do
  echo "$phony" | grep -Eq "(^|[[:space:]])${target}([[:space:]]|$)" || fail ".PHONY missing ${target}"
done

make -n doctor | grep -Eq 'java|ANDROID_HOME|JDK 21' || fail "make -n doctor should mention java, ANDROID_HOME, or JDK 21"
make -n doctor | grep -q '21 or newer' || fail "make -n doctor should require JDK 21 or newer"
make -n doctor | grep -q '25 is fine' || fail "make -n doctor should say Java 25 is fine"
make -n doctor | grep -q 'temurin@21' || fail "make -n doctor should suggest brew install --cask temurin@21 if no 21+ exists"
make -n doctor | grep -q 'openjdk-21-jdk' || fail "make -n doctor should suggest apt openjdk-21-jdk"
make -n doctor | grep -q 'platforms;android-37.0' || fail "make -n doctor should suggest platforms;android-37.0"
make -n doctor | grep -q 'build-tools;37.0.0' || fail "make -n doctor should suggest build-tools;37.0.0"
make -n doctor | grep -q 'android sdk CLI' || fail "make -n doctor should mention the android sdk CLI deprecation warning"
make -n build | grep -q 'assembleDebug' || fail "make -n build should run assembleDebug"
make -n apk | grep -q 'assembleDebug' || fail "make -n apk should run assembleDebug"
make -n apk | grep -q 'dist/lili-music-' || fail "make -n apk should copy into dist/"
make -n release-apk | grep -q 'assembleRelease' || fail "make -n release-apk should run assembleRelease"
make -n release-apk | grep -q 'docs/play/signing.md' || fail "make -n release-apk should point unsigned builds at docs/play/signing.md"
make -n bundle | grep -q 'bundleRelease' || fail "make -n bundle should run bundleRelease"
make -n bundle | grep -q 'docs/play/signing.md' || fail "make -n bundle should point unsigned builds at docs/play/signing.md"
make -n install | grep -q 'adb install -r' || fail "make -n install should adb install -r"
make -n test | grep -q 'testDebugUnitTest' || fail "make -n test should run testDebugUnitTest"
make -n lint | grep -q 'lintDebug' || fail "make -n lint should run lintDebug"
make -n check | grep -q './gradlew check' || fail "make -n check should run ./gradlew check"
make -n clean | grep -q './gradlew clean' || fail "make -n clean should run ./gradlew clean"
make -n clean | grep -q 'dist' || fail "make -n clean should remove dist/"

jdk_finder="scripts/find-jdk.sh"
[ -f "$jdk_finder" ] || fail "missing $jdk_finder"

write_fake_java() {
  dir="$1"
  version_line="$2"
  mkdir -p "$dir/bin"
  cat > "$dir/bin/java" <<EOF
#!/bin/sh
echo '${version_line}' >&2
if echo "\$*" | grep -q XshowSettings; then
  echo '    java.home = ${dir}' >&2
fi
exit 0
EOF
  chmod +x "$dir/bin/java"
}

expect_jdk() {
  label="$1"
  want_major="$2"
  want_ver="$3"
  shift 3
  out="$("$@" 2>/dev/null)" || fail "$label: find-jdk.sh failed"
  got_major="$(printf '%s\n' "$out" | cut -f2)"
  got_ver="$(printf '%s\n' "$out" | cut -f3)"
  [ "$got_major" = "$want_major" ] || fail "$label: major want $want_major got $got_major"
  [ "$got_ver" = "$want_ver" ] || fail "$label: version want $want_ver got $got_ver"
}

reject_jdk() {
  label="$1"
  shift
  if "$@" >/dev/null 2>&1; then
    fail "$label: find-jdk.sh should have failed"
  fi
}

finder="scripts/find-android-platform.sh"
[ -f "$finder" ] || fail "missing $finder"
fake="$(mktemp -d)"
trap 'rm -rf "$fake"' EXIT
write_fake_java "$fake/jdk25" 'java version "25.0.1" 2025-10-21 LTS'
write_fake_java "$fake/jdk21" 'openjdk version "21.0.8" 2025-07-15 LTS'
write_fake_java "$fake/jdk17" 'openjdk version "17.0.15" 2025-04-15'
expect_jdk "oracle 25.0.1" 25 25.0.1 env JAVA_HOME="$fake/jdk25" PATH="/usr/bin:/bin" sh "$jdk_finder"
expect_jdk "temurin 21.0.8" 21 21.0.8 env JAVA_HOME="$fake/jdk21" PATH="/usr/bin:/bin" sh "$jdk_finder"
expect_jdk "25 on PATH" 25 25.0.1 env -u JAVA_HOME PATH="$fake/jdk25/bin:/usr/bin:/bin" sh "$jdk_finder"
reject_jdk "java 17" env JAVA_HOME="$fake/jdk17" PATH="$fake/jdk17/bin" sh "$jdk_finder"
mkdir -p "$fake/only-dot-zero/platforms/android-37.0"
dot_zero="$("$finder" "$fake/only-dot-zero" 37)"
[ "$dot_zero" = "$fake/only-dot-zero/platforms/android-37.0" ] || fail "should accept platforms/android-37.0, got: ${dot_zero:-empty}"
mkdir -p "$fake/plain/platforms/android-37"
plain="$("$finder" "$fake/plain" 37)"
[ "$plain" = "$fake/plain/platforms/android-37" ] || fail "should accept platforms/android-37, got: ${plain:-empty}"
mkdir -p "$fake/minor/platforms/android-37.2"
minor="$("$finder" "$fake/minor" 37)"
[ "$minor" = "$fake/minor/platforms/android-37.2" ] || fail "should accept platforms/android-37.*, got: ${minor:-empty}"
if "$finder" "$fake" 37 >/dev/null 2>&1; then
  fail "empty platforms dir should not match"
fi

echo "test-makefile: ok"
