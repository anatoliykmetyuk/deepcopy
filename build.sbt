val ScalaVer = "2.12.7"

lazy val commonSettings = Seq(
  name    := "deepcopy"
, version := "0.1.0"
, scalaVersion := ScalaVer
, libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.4.0"
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

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
    , "-Xfuture"
    , "-Ypartial-unification")

  , testOptions in Test += Tests.Argument("-oF")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import deepcopy._"
  )
