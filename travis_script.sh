sudo ./gradlew extractFirebaseIosZip -Dorg.gradle.console=plain
sudo chmod -R a+rwx .
sudo chown -R travis .
./gradlew publishToMavenLocal --parallel -Dorg.gradle.console=plain