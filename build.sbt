val ScalaVer = "2.12.7"

lazy val sonatypeSettings = Seq(
  name         := "deepcopy"
, organization := "com.functortech"
, version      := "0.0.1"

// Publish to Sonatype
, useGpg := true
, pgpSecretRing := file("/Users/anatolii/.gnupg/secring.gpg")
, pgpPublicRing := file("/Users/anatolii/.gnupg/pubring.kbx")

, pomIncludeRepository := { _ => false }
, publishMavenStyle := true
, publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }

, licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))
, homepage := Some(url("https://github.com/anatoliykmetyuk/thera"))

, scmInfo := Some(
    ScmInfo(
      url("https://github.com/anatoliykmetyuk/deepcopy"),
      "scm:git@github.com/anatoliykmetyuk/deepcopy.git"
    )
  )

, developers := List(
    Developer(
      id    = "anatoliykmetyuk",
      name  = "Anatolii Kmetiuk",
      email = "anatolii@functortech.com",
      url   = url("http://akmetiuk.com/")
    )
  )
)

lazy val commonSettings = sonatypeSettings ++ Seq(
  name    := "deepcopy"
, version := "0.1.0"
, scalaVersion := ScalaVer
, libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

, crossScalaVersions := Seq("2.11.8", "2.12.7")

, scalacOptions ++= Seq(
      "-deprecation"
    , "-encoding", "UTF-8"
    , "-feature"
    , "-language:existentials"
    , "-language:higherKinds"
    , "-language:implicitConversions"
    , "-language:experimental.macros"
    , "-unchecked"
    // , "-Xfatal-warnings"
    // , "-Xlint"
    // , "-Yinline-warnings"
    , "-Ywarn-dead-code"
    , "-Xfuture")
    // , "-Ypartial-unification")

  , testOptions in Test += Tests.Argument("-oF")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import deepcopy._"
  )
