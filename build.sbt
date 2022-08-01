// See README.md for license details.

//coverageEnabled := true
//logBuffered in Test := false

ThisBuild / scalaVersion     := "2.12.13"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "substate.tech"

val circeVersion = "0.7.0"
val chisel3Version = "3.5.4"
val chiseltestVersion = "0.5.1"
val chiselVerifyVersion = "0.2.0"

libraryDependencies += "io.github.chiselverify" % "chiselverify" % "0.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "substate.ambel",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chisel3Version,
      "edu.berkeley.cs" %% "chiseltest" % chiseltestVersion % "test",
      "io.github.chiselverify" % "chiselverify" % chiselVerifyVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chisel3Version cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

enablePlugins(SiteScaladocPlugin)
publishSite

enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:richmorj/ambel.git"
