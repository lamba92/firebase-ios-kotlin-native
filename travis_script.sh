sudo ./gradlew extractFirebaseIosZip
sudo chmod -R a+rwx .
sudo chown -R travis .
./gradlew publishToMavenLocal --parallel