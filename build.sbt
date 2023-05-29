ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "VSAufgabe1"
  )

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor-typed
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0"
// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.7"
// https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.7"
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-cluster-typed
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-typed" % "2.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-serialization-jackson" % "2.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding-typed" % "2.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.8.0"
// https://mvnrepository.com/artifact/com.thesamet.scalapb/scalapb-runtime
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.13"
// https://mvnrepository.com/artifact/com.thesamet.scalapb/scalapb-runtime-grpc
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.13"
// https://mvnrepository.com/artifact/io.grpc/grpc-netty
libraryDependencies += "io.grpc" % "grpc-netty" % "1.55.1"


Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)
