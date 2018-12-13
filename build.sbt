name := "MSAS"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.1"

libraryDependencies += "io.argonaut" %% "argonaut" % "6.2.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.0"