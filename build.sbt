name := """mrBlack"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SbtWeb)

scalaVersion := "2.11.7"

resolvers += Resolver.jcenterRepo

includeFilter in (Assets, LessKeys.less) := "*.less"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  evolutions,
  "io.swagger" %% "swagger-play2" % "1.5.1"
)

libraryDependencies += "org.pac4j" % "play-pac4j-java" % "2.0.1"
libraryDependencies += "org.pac4j" % "pac4j-core" % "1.8.7"
libraryDependencies += "org.pac4j" % "pac4j-oauth" % "1.8.7"
libraryDependencies += "org.pac4j" % "pac4j-http" % "1.8.7"
libraryDependencies += "com.twilio.sdk" % "twilio-java-sdk" % "6.3.0"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
libraryDependencies += "org.avaje.ebeanorm" % "avaje-ebeanorm" % "6.10.4"
libraryDependencies += "junit" % "junit" % "4.11"
libraryDependencies += "commons-validator" % "commons-validator" % "1.5.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.10.56"
libraryDependencies += "org.apache.tika" % "tika-core" % "1.10"
libraryDependencies += "com.restfb" % "restfb" % "1.21.1"
libraryDependencies += "com.google.zxing" % "core" % "3.2.1"
libraryDependencies += "com.google.zxing" % "javase" % "3.2.1"
libraryDependencies += "com.notnoop.apns" % "apns" % "1.0.0.Beta6"

routesGenerator := InjectedRoutesGenerator

fork in run := true