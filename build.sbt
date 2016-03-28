lazy val root = (project in file(".")).settings(
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "io.getquill"     %%  "quill-jdbc"  % "0.5.0",
    "com.h2database"  %   "h2"          % "1.4.190",
    "org.specs2"      %%  "specs2"      % "2.4.2"     % "test"
  )
)
