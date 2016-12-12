name := "aobp"

version := "0.0.1"

scalaVersion := "2.12.1"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3" cross CrossVersion.binary)

// todo use "-g:notailcalls" only for dev
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-g:notailcalls", "-Xfatal-warnings", "-Ypartial-unification", "-feature", "-language:higherKinds")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-contrib" % "2.4.+",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.+",
  "io.reactivex" %% "rxscala" % "0.26.+",
  "org.scala-lang.modules" %% "scala-async" % "0.9.6",
  "org.scalatest" %% "scalatest" % "3.0.0",
  "org.typelevel" %% "cats" % "0.8.1",
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",

  "io.dropwizard.metrics" % "metrics-core" % "3.1.+",
  "io.dropwizard.metrics" % "metrics-jvm" % "3.1.+",
  "org.coursera" % "dropwizard-metrics-datadog" % "1.1.+",

  "com.google.guava" % "guava" % "20.0",
  "com.typesafe" % "config" % "1.3.+",
  "com.h2database" % "h2-mvstore" % "1.+",
  "ch.qos.logback" % "logback-classic" % "1.+",
  "ch.qos.logback" % "logback-core" % "1.+",
  // todo update to 2.12 asap
  "com.typesafe.play" % "play-json_2.11" % "2.5.9",

  // todo update to 2.12 asap
  "org.consensusresearch" %% "scrypto" % "1.2.0-RC3" exclude("com.typesafe.play", "play-json_2.11"),
  "commons-net" % "commons-net" % "3.+",

  "com.typesafe.akka" %% "akka-http" % "10.0.+",

  "com.typesafe.akka" %% "akka-testkit" % "2.4.+" % "test",
  "org.scalatest" %% "scalatest" % "3.+" % "test",
  "org.scalactic" %% "scalactic" % "3.+" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.+" % "test"
)

// todo pick packaging sbt script from original waves project
javaOptions in Universal ++= Seq(
  "-J-server",
  // tapiki 1 server only free
  // "-J-agentlib:TakipiAgent",
  // JVM memory tuning for 1g ram
  "-J-Xms128m",
  "-J-Xmx612m",

  // from https://groups.google.com/d/msg/akka-user/9s4Yl7aEz3E/zfxmdc0cGQAJ
  "-J-XX:+UseG1GC",
  "-J-XX:+UseNUMA",
  "-J-XX:+AlwaysPreTouch",
  // may be can't use with jstack and others tools
  "-J-XX:+PerfDisableSharedMem",
  "-J-XX:+ParallelRefProcEnabled",
  "-J-verbose:gc",
  // todo write to file or journald?
  s"-J-Xloggc:/var/log/${packageName.value}/gc.log",
  "-J-XX:+PrintGCDetails",
  "-J-XX:+PrintGCDateStamps",
  "-J-XX:+PrintGCTimeStamps",
  "-J-XX:+PrintGCApplicationStoppedTime",
  "-J-XX:+UseGCLogFileRotation",
  "-J-XX:NumberOfGCLogFiles=10",
  "-J-XX:GCLogFileSize=100M",
  "-J-XX:+UseStringDeduplication",
  "-J-XX:+PrintStringDeduplicationStatistics",
  "-J-XX:+UseCompressedStrings",

  // JMX
  "-J-Djava.rmi.server.hostname=127.0.0.1",
  "-J-Dcom.sun.management.jmxremote.port=9010",
  "-J-Dcom.sun.management.jmxremote.rmi.port=9010",
  "-J-Dcom.sun.management.jmxremote.authenticate=false",
  "-J-Dcom.sun.management.jmxremote.ssl=false",

  // Dump on Out of Memory
  "-J-XX:+HeapDumpOnOutOfMemoryError",
  // see bottom s"-J-XX:HeapDumpPath=/var/log/${packageName.value}/oom_heap_dump_`date`.hprof",

  "-J-Dsun.net.inetaddr.ttl=60",

  // Since play uses separate pidfile we have to provide it with a proper path
  s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

  // Use separate configuration file for production environment
  s"-Dconfig.file=/usr/share/${packageName.value}/conf/application-production.conf",

  // Use separate logger configuration file for production environment
  s"-Dlogger.file=/usr/share/${packageName.value}/conf/logback-production.xml",

  // flyway auto migration
  "-Ddb.default.migration.auto=true",

  // http server params
  "-Dhttp.port=9000",
  "-Dhttp.address=0.0.0.0"
)

val dev = Option(System.getProperty("dev")).isDefined

javaOptions in Universal ++= {
  if (dev) {
    Seq(
      "-J-Xdebug",
      "-J-Xrunjdwp:server=y,transport=dt_socket,address=4010,suspend=n"
    )
  } else Seq.empty
}

bashScriptExtraDefines += s"""addJava "-XX:HeapDumpPath=/var/log/${packageName.value}/oom_heap_dump_started_`date`.hprof""""

import com.typesafe.sbt.packager.archetypes.systemloader._
import sbt.Keys.libraryDependencies

enablePlugins(JavaServerAppPackaging, JDebPackaging, SystemdPlugin)
