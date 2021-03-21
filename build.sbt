
val doobieVersion = "0.12.1"
val quillVersion = "3.6.1"

libraryDependencies ++= Seq (
      // Database things
      "io.getquill" %% "quill-jdbc" % quillVersion, 
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-quill" % doobieVersion,
      "com.beachape" %% "enumeratum-doobie" % "1.6.0",
      "com.beachape" %% "enumeratum-quill" % "1.6.0",
       "org.scalatest" %% "scalatest" % "3.2.5" % "test"
)