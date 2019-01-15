name := "scp1819"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.0"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))

libraryDependencies += "com.danielasfregola" %% "twitter4s" % "5.5"

libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"

