#!/bin/bash

java \
  -cp target/classes \
  -XX:StartFlightRecording:filename=$1.jfr \
  $1
