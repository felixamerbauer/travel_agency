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
    "mysql" % "mysql-connector-java" % "5.1.26",
    "org.scalatest" % "scalatest_2.10" % "2.0",
    "com.typesafe.play" %% "play-slick" % "0.5.0.5",
    "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.3",
    "com.h2database" % "h2" % "1.3.174")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    routesImport += "binders._",
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    scalacOptions += "-feature" 
    )
  // .settings(scalacOptions ++= Seq("-deprecation","-unchecked","-feature"))

}
            
