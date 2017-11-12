val scalaVer = "2.12.4"

cancelable in Global := true

val projectName = "scala-js-preact"

scalaVersion := scalaVer

val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
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
    "-Xlint:missing-interpolator",
    "-P:scalajs:sjsDefinedByDefault"
  ),
  jsEnv in Test := PhantomJSEnv().value,
  publishArtifact := false
)

val withMacroParadise = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
)

val lodashJsTestDep = "org.webjars.npm" % "lodash" % "4.17.3" / s"4.17.3/lodash.js" % "test"
val scalajsDomDep = Def.setting("org.scala-js" %%% "scalajs-dom" % "0.9.2")
val scalatestTestDep = Def.setting("org.scalatest" %%% "scalatest" % "3.0.4" % "test")

lazy val raw = project
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    publishSettings,
    name := s"$projectName-raw",
    scalacOptions -= "-Ywarn-unused",
    libraryDependencies ++= Seq(
      scalajsDomDep.value,
      scalatestTestDep.value
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "preact" % "8.2.5" / "dist/preact.min.js", // TODO: 8.2.6
      lodashJsTestDep
    )
  )

lazy val core = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(raw % "compile->compile;test->test")
  .aggregate(raw)
  .settings(
    commonSettings,
    publishSettings,
    name := s"$projectName-core",
    resolvers += Resolver.bintrayIvyRepo("scalameta", "maven"),
    addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % "0.5"),
    scalacOptions += "-Xplugin-require:macroparadise",
    withMacroParadise,
    libraryDependencies ++= Seq(
      "com.github.ghik" %% "silencer-lib" % "0.5",
      "org.scalameta" %%% "scalameta" % "1.8.0"
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
      scalatestTestDep.value
    ),
    jsDependencies ++= Seq(
      lodashJsTestDep
    )
  )

lazy val tagsDsl = project.in(file("./dsl/tags"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(core)
  .settings(
    commonSettings,
    publishSettings,
    name := s"$projectName-dsl-tags",
    libraryDependencies ++= Seq(
      scalatestTestDep.value
    ),
    jsDependencies ++= Seq(
      lodashJsTestDep
    )
  )

lazy val basicExample = project.in(file("./examples/basic"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(symbolDsl)
  .settings(
    commonSettings,
    withMacroParadise
  )

lazy val todomvcExample = project.in(file("./examples/todomvc"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(symbolDsl)
  .settings(
    commonSettings,
    libraryDependencies += "com.github.fomkin" %%% "pushka-json" % "0.8.0",
    withMacroParadise
  )

lazy val examples = project
  .aggregate(basicExample, todomvcExample)

lazy val root = project.in(file("."))
  .aggregate(core, symbolDsl, tagsDsl, examples)
