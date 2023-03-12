// See README.md for license details.

//coverageEnabled := true
//logBuffered in Test := false

ThisBuild / scalaVersion     := "2.12.13"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "substate.tech"

val circeVersion = "0.14.1"
val chisel3Version = "3.6.0-RC2"
val chiseltestVersion = "0.6.0-RC2"
val chiselVerifyVersion = "0.2.0"
val scalatestVersion = "3.2.15"

lazy val root = (project in file("."))
  .settings(
    name := "substate.ambel",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chisel3Version,
      "edu.berkeley.cs" %% "chiseltest" % chiseltestVersion,
      "io.github.chiselverify" % "chiselverify" % chiselVerifyVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      // do not warn about firrtl imports, once the firrtl repo is removed, we will need to import the code
      "-Wconf:cat=deprecation&msg=Importing from firrtl is deprecated:s",
      // do not warn about firrtl deprecations
      "-Wconf:cat=deprecation&msg=will not be supported as part of the migration to the MLIR-based FIRRTL Compiler:s"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chisel3Version cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

enablePlugins(SiteScaladocPlugin)
publishSite

enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:substate-tech/ambel.git"
