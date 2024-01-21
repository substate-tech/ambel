addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
// sbt-sonatype plugin used to publish artifact to maven central via sonatype nexus
// sbt-pgp plugin used to sign the artifcat with pgp keys
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")


logLevel := Level.Warn
