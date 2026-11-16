import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / scalacOptions += "-Wconf:msg=Flag.*repeatedly:s"
ThisBuild / semanticdbEnabled := true

lazy val microservice = Project("sdec-fileservice-api", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    PlayKeys.playDefaultPort := 4004,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:s",
    pipelineStages := Seq(gzip),
    Compile / scalafmtOnCompile := true,
    Test / scalafmtOnCompile := true,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value,
    Test / unmanagedResourceDirectories := Seq(
      baseDirectory.value / "test-resources"
    ),
    Test / unmanagedSourceDirectories += baseDirectory.value / "test-utils"
  )
  .settings(CodeCoverageSettings.settings: _*)

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)

// @formatter:off
addCommandAlias("prePrChecks", "; scalafmtCheckAll; it/scalafmtCheckAll; scalafmtSbtCheck; scalafixAll --check; it/scalafixAll --check")
addCommandAlias("checkCodeCoverage", "; clean; coverage; test; it/test; coverageReport")
addCommandAlias("lint", "; scalafmtAll; it/scalafmtAll; scalafmtSbt; it/scalafixAll; scalafixAll")
addCommandAlias("prePush", "; reload; clean; compile; test; it/test; lint;")
// @formatter:on
