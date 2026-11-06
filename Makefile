# CLI build helpers for an environment without Android Studio.
# Run `make` or `make help`. GNU Make 3.81+ (macOS / Linux).

.DEFAULT_GOAL := help

GRADLEW := ./gradlew
APP := :app
DIST := dist
APP_GRADLE := app/build.gradle.kts

VERSION_NAME := $(shell sed -n 's/^[[:space:]]*versionName = "\([^"]*\)".*/\1/p' $(APP_GRADLE) | head -n1)
ifeq ($(strip $(VERSION_NAME)),)
VERSION_NAME := 0.0.0
endif

DEBUG_APK_SRC := app/build/outputs/apk/debug/app-debug.apk
DEBUG_APK_DST := $(DIST)/lili-music-$(VERSION_NAME)-debug.apk
RELEASE_APK_DIR := app/build/outputs/apk/release

HAS_SIGNING := $(shell grep -E 'signingConfigs[[:space:]]*\{' $(APP_GRADLE) >/dev/null 2>&1 && echo yes || echo no)

COMPILE_SDK := $(shell sed -n 's/^[[:space:]]*compileSdk = \([0-9][0-9]*\).*/\1/p' $(APP_GRADLE) | head -n1)
ifeq ($(strip $(COMPILE_SDK)),)
COMPILE_SDK := 37
endif

.PHONY: help doctor build apk release-apk install test lint check clean

help: ## Show this help
	@awk 'BEGIN {FS = ":.*##"; printf "Usage: make <target>\n\n"} /^[a-zA-Z0-9_-]+:.*?##/ { printf "  %-14s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

doctor: ## Check JDK 17+ and the Android SDK
	@sdk_dir=""; \
	if [ -n "$$ANDROID_HOME" ]; then sdk_dir="$$ANDROID_HOME"; \
	elif [ -n "$$ANDROID_SDK_ROOT" ]; then sdk_dir="$$ANDROID_SDK_ROOT"; \
	elif [ -f local.properties ]; then sdk_dir=$$(sed -n 's/^sdk.dir=//p' local.properties | tail -n1); \
	fi; \
	if ! command -v java >/dev/null 2>&1; then \
		echo "java not found on PATH."; \
		echo "Install JDK 17+ (Temurin) and put it on PATH, or set JAVA_HOME."; \
		echo "  macOS:  brew install --cask temurin@17"; \
		echo "  Debian: sudo apt install openjdk-17-jdk"; \
		exit 1; \
	fi; \
	java_raw=$$(java -version 2>&1 | sed -n 's/.*version "\([^"]*\)".*/\1/p' | head -n1); \
	java_major=$$(echo "$$java_raw" | awk -F. '{ if ($$1 == "1") print $$2; else print $$1 }'); \
	if [ -z "$$java_major" ] || [ "$$java_major" -lt 17 ]; then \
		echo "Need JDK 17+, found: $$java_raw"; \
		echo "Install JDK 17+ and put it first on PATH, or set JAVA_HOME."; \
		echo "  macOS:  brew install --cask temurin@17"; \
		echo "  Debian: sudo apt install openjdk-17-jdk"; \
		exit 1; \
	fi; \
	echo "OK  java $$java_raw (need 17+)"; \
	if [ -z "$$sdk_dir" ] || [ ! -d "$$sdk_dir" ]; then \
		echo "Android SDK not found (ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties sdk.dir)."; \
		echo "Install command-line tools, then:"; \
		echo "  export ANDROID_HOME=\"\$$HOME/Android/Sdk\""; \
		echo "  export PATH=\"\$$ANDROID_HOME/cmdline-tools/latest/bin:\$$ANDROID_HOME/platform-tools:\$$PATH\""; \
		echo "  sdkmanager \"platform-tools\" \"platforms;android-$(COMPILE_SDK)\" \"build-tools;35.0.0\""; \
		echo "  yes | sdkmanager --licenses"; \
		echo "Or set sdk.dir in local.properties (Gradle reads that too)."; \
		exit 1; \
	fi; \
	echo "OK  SDK $$sdk_dir"; \
	if [ ! -d "$$sdk_dir/platforms/android-$(COMPILE_SDK)" ]; then \
		echo "Missing platforms;android-$(COMPILE_SDK). Install with:"; \
		echo "  sdkmanager \"platforms;android-$(COMPILE_SDK)\""; \
		exit 1; \
	fi; \
	echo "OK  platforms;android-$(COMPILE_SDK)"

build: ## Assemble the debug APK
	$(GRADLEW) $(APP):assembleDebug

apk: build ## Build and copy the debug APK to dist/
	mkdir -p $(DIST)
	cp "$(DEBUG_APK_SRC)" "$(DEBUG_APK_DST)"
	@echo "$(DEBUG_APK_DST)"

release-apk: ## Assemble release APK and copy it to dist/
	$(GRADLEW) $(APP):assembleRelease
	mkdir -p $(DIST)
	@src=""; \
	if [ -f "$(RELEASE_APK_DIR)/app-release-unsigned.apk" ]; then \
		src="$(RELEASE_APK_DIR)/app-release-unsigned.apk"; \
		dst="$(DIST)/lili-music-$(VERSION_NAME)-release-unsigned.apk"; \
	elif [ -f "$(RELEASE_APK_DIR)/app-release.apk" ]; then \
		src="$(RELEASE_APK_DIR)/app-release.apk"; \
		if [ "$(HAS_SIGNING)" = "yes" ]; then \
			dst="$(DIST)/lili-music-$(VERSION_NAME)-release.apk"; \
		else \
			dst="$(DIST)/lili-music-$(VERSION_NAME)-release-unsigned.apk"; \
		fi; \
	else \
		echo "No release APK under $(RELEASE_APK_DIR)"; \
		exit 1; \
	fi; \
	cp "$$src" "$$dst"; \
	echo "$$dst"; \
	if [ "$(HAS_SIGNING)" != "yes" ]; then \
		echo "Unsigned release APK (no signingConfigs in $(APP_GRADLE))."; \
		echo "Play Store signing is task 064."; \
	fi

install: apk ## adb install -r the debug APK
	adb install -r "$(DEBUG_APK_DST)"

test: ## JVM unit tests
	$(GRADLEW) testDebugUnitTest

lint: ## Android Lint (debug)
	$(GRADLEW) lintDebug

check: ## Tests + lint + ktlint + detekt
	$(GRADLEW) check

clean: ## Remove Gradle outputs and dist/
	$(GRADLEW) clean
	rm -rf $(DIST)
