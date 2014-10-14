scalaVersion := "2.10.2"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xmax-classfile-name", "100")

scalacOptions in (Compile,doc) ++= Seq("-diagrams", "-implicits")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.2",
  "org.scalatest" %% "scalatest" % "1.9" % "test",
  "net.snaq" % "dbpool" % "5.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4" % "test"
)
