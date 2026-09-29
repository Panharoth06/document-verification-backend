#!/bin/sh
set -e

# Ensure certificates storage directory exists
mkdir -p /app/certificates 2>/dev/null || true

# If the command starts with an option (e.g. -jar or -D), prepend java
if [ "${1#-}" != "$1" ]; then
    set -- java "$@"
fi

# If the command to run is 'java', inject JAVA_OPTS and execute
if [ "$1" = "java" ]; then
    shift
    # Word splitting on JAVA_OPTS is intentional to pass multiple JVM arguments
    # shellcheck disable=SC2086
    exec java $JAVA_OPTS "$@"
fi

# Otherwise, execute whatever command was supplied
exec "$@"
