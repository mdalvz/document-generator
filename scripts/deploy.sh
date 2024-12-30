#!/usr/bin/env bash

set -e

echo "Executing git pull"

git pull

echo "Executing gradle clean"

./gradlew clean

echo "Executing gradle build"

./gradlew build

echo "Executing cdk deploy"

cd packages/DocumentGeneratorCDK

cdk deploy

cd ../..
