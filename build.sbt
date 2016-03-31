import scalariform.formatter.preferences._


name:="spray-cookies"
organization:="net.spraycookies"

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
  "io.spray"            %% "spray-client"  % "1.3.2",
  "io.spray"            %% "spray-json"    % "1.3.0",
  "com.typesafe.akka"   %% "akka-actor"    % "2.4.0",
  "org.scalacheck"      %% "scalacheck"    % "1.11.3" % "test"
)

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

lazy val releaseSettings = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false }
)
pomExtra :=
  <licenses>
    <license>
      <name> GNU Lesser General Public License</name>
      <url>http://www.gnu.org/licenses/lgpl-3.0.en.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
    <scm>
      <connection>scm:git:https://github.com/innoq/spray-cookies.git</connection>
      <url>https://github.com/innoq/spray-cookies</url>
    </scm>
