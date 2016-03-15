import scalariform.formatter.preferences._


name:="spray-cookies"
organization:="de.postlab.from.martijnhoekstra"

version:="0.1-SNAPSHOT"

scalaVersion:="2.11.7"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-target:jvm-1.8",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-client" % "1.3.2",
  "io.spray" %%  "spray-json" % "1.3.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)