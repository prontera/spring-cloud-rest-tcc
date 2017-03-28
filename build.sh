#!/bin/bash
#set -x
set -eo pipefail
shopt -s nullglob

echo "$(tput setaf "2")" "repackaging..." "$(tput sgr0)"
mvn clean package
for file in ./*-compose.yml; do
    echo "$(tput setaf "2")" "building $file..." "$(tput sgr0)"
    docker-compose -f "$file" build
done
