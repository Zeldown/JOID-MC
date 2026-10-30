#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -eq 0 ]; then
    echo "[JOID] Usage: scripts/update-libs.sh <checkout of a version branch>..." >&2
    exit 1
fi

root="$(cd "$(dirname "$0")/.." && pwd)"
version="$(sed -n 's/^version=//p' "$root/gradle.properties" | tr -d '\r')"

for target in "$@"; do
    if [ ! -d "$target/libs" ] || [ ! -f "$target/gradle.properties" ]; then
        echo "[JOID] $target is not the checkout of a version branch: it has no libs folder or no gradle.properties" >&2
        exit 1
    fi
done

(cd "$root" && ./gradlew build)

for target in "$@"; do
    rm -f "$target"/libs/joid-backend-minecraft-common-*.jar
    for kind in dev prod sources demo; do
        cp "$root/build/libs/joid-backend-minecraft-common-$version-$kind.jar" "$target/libs/"
    done

    if grep -q '^common_version=' "$target/gradle.properties"; then
        sed -i "s/^common_version=.*/common_version=$version/" "$target/gradle.properties"
    else
        echo "[JOID] $target/gradle.properties has no common_version: add common_version=$version to it" >&2
    fi

    echo "[JOID] $target now embeds joid-backend-minecraft-common $version"
done
