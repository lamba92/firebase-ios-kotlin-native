sudo chown -R travis ./
chmod -R 600 ./
chmod +x gradlew
./gradlew bintrayUpload
