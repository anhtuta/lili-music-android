#!/bin/sh
# Print HOME<TAB>MAJOR<TAB>VERSION for a JDK 21 or newer, or exit 1.
# Prefers JAVA_HOME, then java on PATH. Parses strings like 25.0.1 and 21.0.8.
set -eu

java_version_string() {
	bin="$1"
	if [ ! -x "$bin" ]; then
		return 1
	fi
	"$bin" -version 2>&1 | sed -n 's/.*version "\([^"]*\)".*/\1/p' | head -n1
}

java_major() {
	ver="$1"
	if [ -z "$ver" ]; then
		return 1
	fi
	echo "$ver" | awk -F. '{
		v = $1
		gsub(/[^0-9]/, "", v)
		if (v == "1") {
			v = $2
			gsub(/[^0-9]/, "", v)
		}
		if (v ~ /^[0-9]+$/) print v
	}'
}

is_at_least_21() {
	major="$1"
	[ -n "$major" ] && [ "$major" -ge 21 ]
}

emit() {
	printf '%s\t%s\t%s\n' "$1" "$2" "$3"
}

if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
	ver="$(java_version_string "${JAVA_HOME}/bin/java" || true)"
	major="$(java_major "$ver" || true)"
	if is_at_least_21 "$major"; then
		emit "$JAVA_HOME" "$major" "$ver"
		exit 0
	fi
fi

if command -v java >/dev/null 2>&1; then
	java_bin="$(command -v java)"
	ver="$(java_version_string "$java_bin" || true)"
	major="$(java_major "$ver" || true)"
	if is_at_least_21 "$major"; then
		home="$("$java_bin" -XshowSettings:properties -version 2>&1 | sed -n 's/^[[:space:]]*java.home = //p' | head -n1 || true)"
		if [ -z "$home" ]; then
			java_bin="$(readlink -f "$java_bin" 2>/dev/null || printf '%s' "$java_bin")"
			home="$(dirname "$(dirname "$java_bin")")"
		fi
		emit "$home" "$major" "$ver"
		exit 0
	fi
fi

exit 1
