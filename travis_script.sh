sudo ./gradlew extractFirebaseIosZip -Dorg.gradle.console=plain -PskipBuild=true
sudo chmod -R a+rwx .
sudo chown -R travis .
./gradlew build publishToMavenLocal --parallel -Dorg.gradle.console=plain