sudo chown -R travis:travis ./
chmod -R 600 ./
chmod +x gradlew
./gradlew bintrayUpload
