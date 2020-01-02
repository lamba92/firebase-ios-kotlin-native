sudo chmod -R 600 ./
sudo chmod +x gradlew
sudo chown -R travis:travis ./
./gradlew bintrayUpload
