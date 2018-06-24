
name := "iot-assignment-reader"

version := "0.1.0-SNAPSHOT"

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