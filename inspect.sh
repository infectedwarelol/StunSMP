#!/usr/bin/env bash
set -uo pipefail

cd "$(dirname "$0")" || exit 1

java_sources="src/main/java"
plugin_template="src/main/templates/plugin.yml"
errors=0
warnings=0

error() {
    echo "error: $*"
    errors=$((errors + 1))
}

warning() {
    echo "warning: $*"
    warnings=$((warnings + 1))
}

require_field() {
    if ! grep -qxF "$1" "$plugin_template"; then
        error "plugin.yml is missing '$1'"
    fi
}

forbid_field() {
    if grep -qE "^$1:" "$plugin_template"; then
        error "plugin.yml must not declare $1"
    fi
}

build_log="$(mktemp)"
trap 'rm -f "$build_log"' EXIT

echo "==> Building"
if ! ./gradlew clean build --console=plain >"$build_log" 2>&1; then
    error "gradle build failed: $(grep -m1 -E 'error:|FAILURE|What went wrong' "$build_log" || echo 'see gradle output')"
fi
while IFS= read -r line; do
    warning "javac: $line"
done < <(grep -E '\.java:[0-9]+: warning:' "$build_log")

echo "==> Checking jar"
version="$(sed -n 's/^version=//p' gradle.properties)"
jar="build/libs/StunSMP-${version}.jar"
if [[ -z "$version" ]]; then
    error "gradle.properties has no version"
elif [[ ! -f "$jar" ]]; then
    error "$jar was not built"
else
    jar_version="$(unzip -p "$jar" plugin.yml 2>/dev/null | sed -n "s/^version: '\(.*\)'$/\1/p")"
    if [[ "$jar_version" != "$version" ]]; then
        error "plugin.yml version '$jar_version' does not match gradle.properties version '$version'"
    fi
fi

echo "==> Checking plugin.yml"
require_field "name: StunSMP"
require_field "main: dev.infectedware.stunsmp.StunSMP"
require_field "api-version: '1.21.11'"
require_field "author: infectedware"
require_field "website: https://infectedware.dev/"
forbid_field description
forbid_field folia-supported

echo "==> Checking commands"
declared_commands="$(awk '/^commands:/{inside=1; next} /^[a-z]/{inside=0} inside && /^  [a-z0-9_-]+:$/{gsub(/[ :]/, ""); print}' "$plugin_template")"
for command in $declared_commands; do
    if ! grep -rqF "\"$command\"" "$java_sources"; then
        error "/$command is declared in plugin.yml but never registered in Java"
    fi
done

echo "==> Checking permissions"
declared_permissions="$(awk '/^permissions:/{inside=1; next} /^[a-z]/{inside=0} inside && /^  [a-z0-9_.*-]+:$/{gsub(/[ :]/, ""); print}' "$plugin_template")"
while IFS= read -r permission; do
    if ! grep -qxF "$permission" <<<"$declared_permissions"; then
        warning "permission $permission is used in Java but not declared in plugin.yml"
    fi
done < <(grep -rhoE '"stunsmp\.[a-z0-9_.-]*[a-z0-9_-]"' "$java_sources" | tr -d '"' | sort -u)

echo "==> Checking sources"
while IFS= read -r line; do
    warning "comment: $line"
done < <(grep -rnE '^\s*(//|/\*|\*)|[^:"]//|/\*' "$java_sources")
while IFS= read -r line; do
    warning "unfinished: $line"
done < <(grep -rnE 'TODO|FIXME|XXX' src)

echo "==> $errors error(s), $warnings warning(s)"
[[ $errors -eq 0 && $warnings -eq 0 ]]
