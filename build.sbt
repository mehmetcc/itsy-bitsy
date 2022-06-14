ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.mehmetcc"
ThisBuild / organizationName := "example"

val ZioVersion = "2.0.0-RC6"
val PureconfigVersion = "0.17.1"
val JSoupVersion = "1.15.1"

lazy val root = (project in file("."))
  .settings(
    name := "dirtyspidy",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % ZioVersion,
      "org.jsoup" % "jsoup" % JSoupVersion,
      "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
      "io.lemonlabs" %% "scala-uri" % "4.0.2",
      "dev.zio" %% "zio-test" % ZioVersion % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
