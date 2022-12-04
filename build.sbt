ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "scala-optics-monocle-workshop",
    libraryDependencies ++= Seq(
      "dev.optics" %% "monocle-core" % "3.1.0",
      "dev.optics" %% "monocle-macro" % "3.1.0",
      "io.circe" %% "circe-parser" % "0.14.1",
      "io.circe" %% "circe-optics" % "0.14.1",
      "dev.zio" %% "zio" % "2.0.2",
      "dev.zio" %% "zio-test" % "2.0.2" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.2" % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.0.2" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
