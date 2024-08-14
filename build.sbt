ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

lazy val jsoupVersion = "1.18.1"
lazy val catsEffectVersion = "3.3.14"
lazy val http4sVersion = "0.23.15"
lazy val pureConfigVersion = "0.17.1"
lazy val log4catsVersion = "2.4.0"
lazy val scalaTestVersion = "3.2.12"
lazy val scalaTestCatsEffectVersion = "1.4.0"
lazy val logbackVersion = "1.4.0"
lazy val slf4jVersion = "2.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "internet-crawler",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.jsoup" % "jsoup" % jsoupVersion,
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "org.slf4j" % "slf4j-simple" % slf4jVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % scalaTestCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
    )
  )
