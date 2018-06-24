import sbt.Keys._
import sbt._

import scala.language.postfixOps

name := "iot-assignment"

val SCALA_VERSION = "2.12.6"

version := "0.1.0-SNAPSHOT"

scalaVersion := SCALA_VERSION

fork in run := true

lazy val commonSettings = Seq(
  scalaVersion := SCALA_VERSION,
  fork in run := true
)

lazy val commonsAssembly = Seq(
  assemblyJarName in assembly := name.value,
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(appendContentHash = true),
  assemblyMergeStrategy in assembly := {
    //Error generating jar for akka-persistence-cassandra
    case PathList(ps @ _*) if ps.last endsWith "io.netty.versions.properties" => MergeStrategy.first
    case x                              =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val commonsDocker =
  imageNames in docker := Seq(
    ImageName(s"${name.value}:${version.value}"),
    ImageName(s"${name.value}:latest")
  )

lazy val commons = project in file("iot-assignment-commons")

lazy val storer =
  (project in file("iot-assignment-storer"))
    .dependsOn(commons)
    .settings(commonSettings, commonsAssembly, commonsDocker)
    .enablePlugins(DockerPlugin, AssemblyPlugin)

lazy val reader =
  (project in file("iot-assignment-reader"))
    .dependsOn(commons)
    .settings(commonSettings, commonsAssembly, commonsDocker)
    .enablePlugins(DockerPlugin, AssemblyPlugin)

lazy val apiWriter =
  (project in file("iot-assignment-api-writer"))
    .dependsOn(commons)
    .settings(commonSettings, commonsAssembly, commonsDocker)
    .enablePlugins(DockerPlugin, AssemblyPlugin)

lazy val apiStats =
  (project in file("iot-assignment-api-stats"))
    .dependsOn(commons)
    .settings(commonSettings, commonsAssembly, commonsDocker)
    .enablePlugins(DockerPlugin, AssemblyPlugin)

lazy val simulation =
  (project in file("iot-assignment-simulation"))
    .dependsOn(commons)
    .settings(commonSettings)