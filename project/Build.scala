import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "travel_agency"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    cache,
    jdbc,
    filters,
    "com.typesafe.slick" % "slick_2.10" % "1.0.1",
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
    "org.scalatest" % "scalatest_2.10" % "2.0",
    "com.typesafe.play" %% "play-slick" % "0.5.0.5",
    "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.3")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    scalacOptions += "-feature" 
    )

}
            
