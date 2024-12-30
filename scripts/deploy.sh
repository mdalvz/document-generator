#!/usr/bin/env bash

set -e

echo "Executing gradle clean"

./gradlew clean

echo "Executing gradle build"

./gradlew build

echo "Executing cdk deploy"

cdk deploy
