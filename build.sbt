// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.1"

name := "cats-practise"
organization := "ch.epfl.scala"
version := "1.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0"
