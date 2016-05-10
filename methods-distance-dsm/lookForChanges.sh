#!/bin/bash

fileName="$1"

while true; do
  change=$(inotifywait -e close_write,moved_to,create "$(dirname $fileName)")
  java -jar target/method-call-graph-1.0-SNAPSHOT-jar-with-dependencies.jar "$fileName"
  date
done
