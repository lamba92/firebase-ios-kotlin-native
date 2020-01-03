sudo ./gradlew extractFirebaseIosZip -Dorg.gradle.console=plain -PskipBuild=true
sudo chmod -R a+rwx .
sudo chown -R travis .
./gradlew publishToMavenLocal -Dorg.gradle.console=plain