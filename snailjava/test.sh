#!/bin/bash
cd "$(dirname "$0")"
./gradlew clean test jacocoTestReport
