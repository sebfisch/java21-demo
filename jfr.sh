#!/bin/bash

/opt/java/openjdk/bin/java \
  -cp target/classes \
  -XX:StartFlightRecording:filename=$1.jfr \
  $*
