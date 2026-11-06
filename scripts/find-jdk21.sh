#!/bin/sh
# Print the home of an installed JDK 21, or exit 1.
# Checks JAVA_HOME, macOS /usr/libexec/java_home -v 21, then java on PATH.
set -eu

java_major() {
	bin="$1"
	if [ ! -x "$bin" ]; then
		return 1
	fi
	raw="$("$bin" -version 2>&1 | sed -n 's/.*version "\([^"]*\)".*/\1/p' | head -n1)"
	echo "$raw" | awk -F. '{ if ($1 == "1") print $2; else print $1 }'
}

if [ -n "${JAVA_HOME:-}" ] && [ "$(java_major "${JAVA_HOME}/bin/java")" = "21" ]; then
	printf '%s\n' "$JAVA_HOME"
	exit 0
fi

if [ -x /usr/libexec/java_home ]; then
	home="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
	if [ -n "$home" ] && [ -d "$home" ]; then
		printf '%s\n' "$home"
		exit 0
	fi
fi

if command -v java >/dev/null 2>&1 && [ "$(java_major "$(command -v java)")" = "21" ]; then
	# Resolve JAVA_HOME from the java binary when possible.
	java_bin="$(command -v java)"
	java_bin="$(readlink -f "$java_bin" 2>/dev/null || printf '%s' "$java_bin")"
	home="$(dirname "$(dirname "$java_bin")")"
	printf '%s\n' "$home"
	exit 0
fi

exit 1
