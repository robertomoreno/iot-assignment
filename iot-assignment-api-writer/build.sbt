
name := "iot-assignment-api-writer"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"                  %  Version.AKKA_HTTP,
  "com.typesafe.akka" %% "akka-http-spray-json"       %  Version.SPRAY_JSON,

  "com.typesafe.akka" %% "akka-http-testkit"     % Version.AKKA_HTTP    % Test,
)

val mainClassName = "iot.assignment.Boot"

mainClass in (Compile,run) := Option(mainClassName)

// --- Assembly
mainClass in assembly := Option(mainClassName)

// --- Docker
dockerfile in docker := {
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  val rootPath = baseDirectory.value.getParentFile
  val scriptFile = new File(rootPath,"docker/run.sh")
  val scriptTargetPath = "/app/run.sh"

  new Dockerfile {
    from("openjdk:8-jre-alpine")
    runRaw("apk update && apk upgrade")
    runRaw("apk add curl")
    add(artifact, artifactTargetPath)
    add(scriptFile,scriptTargetPath)
    expose(8080, 2551)
    entryPoint("." + scriptTargetPath, artifactTargetPath)
  }
}