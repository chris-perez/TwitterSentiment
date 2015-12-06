name := "untitled"

version := "1.0"

lazy val `untitled` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq( javaJdbc , javaEbean , cache , javaWs )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  