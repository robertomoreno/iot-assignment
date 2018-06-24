
name := "iot-assignment-simulation"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"                  %  Version.AKKA_HTTP,
  "com.typesafe.akka" %% "akka-http-spray-json"       %  Version.SPRAY_JSON,

  "com.typesafe.akka" %% "akka-http-testkit"     % Version.AKKA_HTTP    % Test,
)

val mainClassName = "iot.assignment.Boot"

mainClass in (Compile,run) := Option(mainClassName)