val scalaVer = "2.12.1"

cancelable in Global := true

val projectName = "scala-js-preact"

scalaVersion := scalaVer

val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },
  publishArtifact := true,
  publishArtifact in Test := false,
  licenses := Seq("MIT License" -> url("https://opensource.org/licenses/mit-license.php")),
  homepage := Some(url("https://github.com/lmnet/scala-js-preact")),
  pomExtra := {
    <scm>
      <url>git@github.com:lmnet/scala-js-preact.git</url>
      <connection>scm:git:git@github.com:lmnet/scala-js-preact.git</connection>
    </scm>
    <developers>
      <developer>
        <id>lmnet</id>
        <name>Yuriy Badalyantc</name>
        <email>lmnet89@gmail.com</email>
      </developer>
    </developers>
  }
)

val commonSettings = Seq(
  organization := "com.github.lmnet",
  scalaVersion := scalaVer,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Ywarn-numeric-widen",
    "-Xlint:missing-interpolator"
  ),
  jsEnv in Test := PhantomJSEnv().value,
  publishArtifact := false
)

val lodashVersion = "4.17.3"

lazy val core = project
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    publishSettings,
    name := s"$projectName-core",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "org.scalatest" %%% "scalatest" % "3.0.1" % "test"
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "preact" % "7.2.0" / "dist/preact.min.js",
      "org.webjars.npm" % "lodash" % lodashVersion / s"$lodashVersion/lodash.js" % "test"
    )
  )

lazy val symbolDsl = project.in(file("./dsl/symbol"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(core)
  .settings(
    commonSettings,
    publishSettings,
    name := s"$projectName-dsl-symbol",
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.0.1" % "test"
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "lodash" % lodashVersion / s"$lodashVersion/lodash.js" % "test"
    )
  )


lazy val basicExample = project.in(file("./examples/basic"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(symbolDsl)
  .settings(commonSettings)

lazy val todomvcExample = project.in(file("./examples/todomvc"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(symbolDsl)
  .settings(
    commonSettings,
    libraryDependencies += "com.github.fomkin" %%% "pushka-json" % "0.8.0",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val examples = project
  .aggregate(basicExample, todomvcExample)

lazy val root = project.in(file("."))
  .aggregate(core, symbolDsl, examples)
