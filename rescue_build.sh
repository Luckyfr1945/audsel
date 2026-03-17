#!/bin/bash
# Rescue script to fix build on Gradle 9.x

echo "Fixing build for Gradle 9.x..."

# Create local.properties if not exists
if [ ! -f local.properties ]; then
    echo "sdk.dir=$HOME/Android/Sdk" > local.properties
    echo "Created local.properties"
fi

# Try to run wrapper with a clean state (ignoring the broken project files temporarily)
echo "Generating Gradle Wrapper 8.4..."
# We move the build files temporarily so gradle wrapper doesn't try to evaluate them
mkdir -p temp_build
mv build.gradle temp_build/
mv app/build.gradle temp_build/app_build.gradle

gradle wrapper --gradle-version 8.4

# Move them back
mv temp_build/build.gradle ./
mv temp_build/app_build.gradle app/build.gradle
rm -rf temp_build

if [ -f gradlew ]; then
    chmod +x gradlew
    echo "Success! Sekarang jalankan: ./gradlew assembleDebug"
else
    echo "Gagal membuat gradlew. Pastikan internet aktif dan 'gradle' terinstal."
fi
