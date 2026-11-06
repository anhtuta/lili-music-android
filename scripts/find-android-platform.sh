#!/bin/sh
# Print the first SDK platform dir that satisfies compileSdk = API.
# sdkmanager installs API 37 as platforms/android-37.0 (no platforms;android-37).
# Usage: find-android-platform.sh <sdk_dir> <api>
set -eu

sdk_dir="${1:?sdk dir required}"
api="${2:?api level required}"
base="${sdk_dir}/platforms"

if [ -d "${base}/android-${api}" ]; then
	printf '%s\n' "${base}/android-${api}"
	exit 0
fi
if [ -d "${base}/android-${api}.0" ]; then
	printf '%s\n' "${base}/android-${api}.0"
	exit 0
fi
for dir in "${base}"/android-"${api}".*; do
	if [ -d "${dir}" ]; then
		printf '%s\n' "${dir}"
		exit 0
	fi
done
exit 1
