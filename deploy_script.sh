sudo chmod -R a+rwx ./
sudo chown -R travis .
./gradlew bintrayUpload
