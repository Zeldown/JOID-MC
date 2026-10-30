#!/usr/bin/env bash
set -euo pipefail

model="${MODEL:-version/26.2}"
workflow='uses: Zeldown/JOID-MC/.github/workflows/version.yml@common'
failures=0

headings() {
    git show "origin/$1:README.md" | tr -d '\r' | awk '/^```/ { fence = !fence; next } !fence && /^#+ /'
}

tests() {
    git show "origin/$1:README.md" | tr -d '\r' | awk '/^## Tests$/ { inside = 1; print; next } inside && /^##? / { inside = 0 } inside'
}

git fetch --quiet origin '+refs/heads/version/*:refs/remotes/origin/version/*'
branches="$(git for-each-ref --format='%(refname:lstrip=3)' 'refs/remotes/origin/version/')"
if [ -z "$branches" ]; then
    echo "[JOID] No version branch to check" >&2
    exit 1
fi

for branch in $branches; do
    if ! difference="$(diff <(headings "$model") <(headings "$branch"))"; then
        echo "[JOID] The README of $branch does not follow the plan of the README of $model:" >&2
        echo "$difference" >&2
        failures=$((failures + 1))
    fi

    if ! difference="$(diff <(tests "$model") <(tests "$branch"))"; then
        echo "[JOID] The Tests section of the README of $branch differs from the one of $model:" >&2
        echo "$difference" >&2
        failures=$((failures + 1))
    fi

    build="$(git show "origin/$branch:.github/workflows/build.yml" | tr -d '\r')"
    if ! grep -qF "$workflow" <<< "$build" || grep -q 'runs-on:' <<< "$build"; then
        echo "[JOID] The build.yml of $branch does not only call $workflow" >&2
        failures=$((failures + 1))
    fi

    echo "[JOID] Checked $branch against $model"
done

if [ "$failures" -gt 0 ]; then
    echo "[JOID] $failures parity checks failed" >&2
    exit 1
fi
