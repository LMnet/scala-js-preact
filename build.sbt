
val scalaVer = "2.12.0"

cancelable in Global := true

name := "scala-js-preact"

scalaVersion := scalaVer

val commonSettings = Seq(
  organization := "com.lmnet",
  scalaVersion := scalaVer,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-language:implicitConversions"
  )
)

lazy val core = project
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    jsDependencies += "org.webjars.npm" % "preact" % "7.0.3" / "dist/preact.min.js"
  )

lazy val todomvcExample = project.in(file("./examples/todomvc"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val basicExample = project.in(file("./examples/basic"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val examples = project
  .aggregate(basicExample, todomvcExample)

lazy val root = project.in(file("."))
  .aggregate(core, examples)
