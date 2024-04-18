// See README.md for license details.

//coverageEnabled := true
//logBuffered in Test := false

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "io.github.substate-tech"

val basterVersion = "0.1.0"
val circeVersion = "0.14.1"
val chiselVersion = "6.0.0"
val chiseltestVersion = "6.0.0"
//val chiselVerifyVersion = "0.2.0"
val scalatestVersion = "3.2.16"

lazy val root = (project in file("."))
  .settings(
    name := "ambel",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % chiseltestVersion,
      //"io.github.chiselverify" % "chiselverify" % chiselVerifyVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.github.substate-tech" %% "baster" % basterVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.13",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      // do not warn about firrtl imports, once the firrtl repo is removed, we will need to import the code
      "-Wconf:cat=deprecation&msg=Importing from firrtl is deprecated:s",
      // do not warn about firrtl deprecations
      "-Wconf:cat=deprecation&msg=will not be supported as part of the migration to the MLIR-based FIRRTL Compiler:s"
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full)
  )

enablePlugins(SiteScaladocPlugin)
publishSite

enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:substate-tech/ambel.git"
