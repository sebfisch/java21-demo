#!/bin/bash

mvn clean compile && java \
  --enable-preview \
  -cp target/classes \
  -XX:StartFlightRecording:filename=hello.jfr,duration=5s \
  Hello
