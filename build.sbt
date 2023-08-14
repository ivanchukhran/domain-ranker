ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "domain-ranker"
  )

val AkkaVersion = "2.8.3"
val AkkaHttpVersion = "10.5.2"
val PlayJsonVersion = "2.9.4"
val JsoupVersion = "1.15.4"
val ScRedisVersion = "2.4.3"
val Args4JVersion = "2.33"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.play" %% "play-json" % PlayJsonVersion,
  "org.jsoup" % "jsoup" % JsoupVersion,
  "com.github.scredis" %% "scredis" % ScRedisVersion,
  "args4j" % "args4j" % Args4JVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.7" % Runtime
)