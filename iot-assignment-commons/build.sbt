
name := "iot-assignment-commons"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster-sharding"      %  Version.AKKA,
  "org.slf4j"         % "slf4j-simple"                % Version.SLF4J,

  "com.typesafe.akka" %% "akka-testkit"          % Version.AKKA         % Test,
)
