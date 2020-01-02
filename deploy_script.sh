sudo chmod -R 600 ./
sudo chmod +x gradlew
sudo chown -R travis ./
./gradlew bintrayUpload
