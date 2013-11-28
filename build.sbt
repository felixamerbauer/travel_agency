// In order to avoid Eclipse and sbt working on the same files: At least in theory there could be race conditions and such
EclipseKeys.eclipseOutput := Some(".target")

// Add source entries to library dependencies
EclipseKeys.withSource := true

// Use Java 7 (change to JavaSE16 if you don't have Java 7 installed)
EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE17)
