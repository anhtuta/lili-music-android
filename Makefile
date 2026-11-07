# CLI build helpers for an environment without Android Studio.
# Run `make` or `make help`. GNU Make 3.81+ (macOS / Linux).

.DEFAULT_GOAL := help

GRADLEW := ./gradlew
APP := :app
DIST := dist
APP_GRADLE := app/build.gradle.kts
VERSION_FILE := version.properties

ifndef VERSION_NAME
VERSION_NAME := $(shell sed -n 's/^VERSION_NAME=//p' $(VERSION_FILE) | head -n1)
endif
ifeq ($(strip $(VERSION_NAME)),)
VERSION_NAME := 0.0.0
endif

ifndef VERSION_CODE
VERSION_CODE := $(shell sed -n 's/^VERSION_CODE=//p' $(VERSION_FILE) | head -n1)
endif

DEBUG_APK_SRC := app/build/outputs/apk/debug/app-debug.apk
DEBUG_APK_DST := $(DIST)/lili-music-$(VERSION_NAME)-debug.apk
RELEASE_APK_DIR := app/build/outputs/apk/release
RELEASE_AAB_SRC := app/build/outputs/bundle/release/app-release.aab

HAS_SIGNING := $(shell if [ -f keystore.properties ] || { [ -n "$$LILI_STORE_FILE" ] && [ -n "$$LILI_STORE_PASSWORD" ] && [ -n "$$LILI_KEY_ALIAS" ] && [ -n "$$LILI_KEY_PASSWORD" ]; }; then echo yes; else echo no; fi)

COMPILE_SDK := $(shell sed -n 's/^[[:space:]]*compileSdk = \([0-9][0-9]*\).*/\1/p' $(APP_GRADLE) | head -n1)
ifeq ($(strip $(COMPILE_SDK)),)
COMPILE_SDK := 37
endif

SDKMANAGER_INSTALL := sdkmanager "platforms;android-$(COMPILE_SDK).0" "build-tools;$(COMPILE_SDK).0.0" "platform-tools"
SDKMANAGER_DEPRECATION := sdkmanager may print a deprecation warning pointing to the new android sdk CLI; that warning is harmless.

.PHONY: help doctor build apk release-apk bundle install test lint check clean

help: ## Show this help
	@awk 'BEGIN {FS = ":.*##"; printf "Usage: make <target>\n\n"} /^[a-zA-Z0-9_-]+:.*?##/ { printf "  %-14s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

doctor: ## Check JDK 21+ and the Android SDK
	@sdk_dir=""; \
	if [ -n "$$ANDROID_HOME" ]; then sdk_dir="$$ANDROID_HOME"; \
	elif [ -n "$$ANDROID_SDK_ROOT" ]; then sdk_dir="$$ANDROID_SDK_ROOT"; \
	elif [ -f local.properties ]; then sdk_dir=$$(sed -n 's/^sdk.dir=//p' local.properties | tail -n1); \
	fi; \
	jdk_info=$$(sh scripts/find-jdk.sh) || jdk_info=""; \
	if [ -z "$$jdk_info" ]; then \
		echo "Need JDK 21 or newer to run Gradle (25 is fine; bytecode stays 17)."; \
		echo "  java -version"; \
		echo "  macOS:  JAVA_HOME can be an Oracle/Temurin 21+ install on PATH"; \
		echo "          brew install --cask temurin@21   # only if no 21+ JDK exists"; \
		echo "  Debian: sudo apt install openjdk-21-jdk"; \
		exit 1; \
	fi; \
	jdk_home=$$(printf '%s\n' "$$jdk_info" | cut -f1); \
	jdk_major=$$(printf '%s\n' "$$jdk_info" | cut -f2); \
	jdk_ver=$$(printf '%s\n' "$$jdk_info" | cut -f3); \
	echo "OK  JDK $$jdk_major ($$jdk_ver) $$jdk_home"; \
	if [ -z "$$sdk_dir" ] || [ ! -d "$$sdk_dir" ]; then \
		echo "Android SDK not found (ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties sdk.dir)."; \
		echo "Install command-line tools, then:"; \
		echo "  export ANDROID_HOME=\"\$$HOME/Android/Sdk\""; \
		echo "  export PATH=\"\$$ANDROID_HOME/cmdline-tools/latest/bin:\$$ANDROID_HOME/platform-tools:\$$PATH\""; \
		echo '  $(SDKMANAGER_INSTALL)'; \
		echo "  yes | sdkmanager --licenses"; \
		echo "$(SDKMANAGER_DEPRECATION)"; \
		echo "Or set sdk.dir in local.properties (Gradle reads that too)."; \
		exit 1; \
	fi; \
	echo "OK  SDK $$sdk_dir"; \
	platform_dir=$$(sh scripts/find-android-platform.sh "$$sdk_dir" "$(COMPILE_SDK)") || platform_dir=""; \
	if [ -z "$$platform_dir" ]; then \
		echo "Missing platforms/android-$(COMPILE_SDK) or android-$(COMPILE_SDK).0 (compileSdk $(COMPILE_SDK))."; \
		echo "There is no sdkmanager package platforms;android-$(COMPILE_SDK); API $(COMPILE_SDK) is platforms;android-$(COMPILE_SDK).0."; \
		echo "Install with:"; \
		echo '  $(SDKMANAGER_INSTALL)'; \
		echo "$(SDKMANAGER_DEPRECATION)"; \
		exit 1; \
	fi; \
	echo "OK  platform $$platform_dir"

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
		echo "Unsigned release APK (no keystore.properties / LILI_* env)."; \
		echo "See docs/play/signing.md."; \
	fi

bundle: ## Assemble the Play upload AAB and copy it to dist/
	$(GRADLEW) $(APP):bundleRelease
	mkdir -p $(DIST)
	@if [ ! -f "$(RELEASE_AAB_SRC)" ]; then echo "No release AAB at $(RELEASE_AAB_SRC)"; exit 1; fi
	@if [ "$(HAS_SIGNING)" = "yes" ]; then \
		dst="$(DIST)/lili-music-$(VERSION_NAME).aab"; \
	else \
		dst="$(DIST)/lili-music-$(VERSION_NAME)-unsigned.aab"; \
	fi; \
	cp "$(RELEASE_AAB_SRC)" "$$dst"; \
	echo "$$dst"; \
	if [ "$(HAS_SIGNING)" != "yes" ]; then \
		echo "Unsigned release AAB (no keystore.properties / LILI_* env)."; \
		echo "See docs/play/signing.md."; \
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
