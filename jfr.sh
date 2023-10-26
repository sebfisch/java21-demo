#!/bin/bash

java \
  -cp target/classes \
  -XX:StartFlightRecording:filename=$1.jfr,duration=60s \
  $1
