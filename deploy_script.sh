sudo chown -R "$USER" ./*
chmod -R 600 .
chmod +x gradlew
./gradlew bintrayUpload