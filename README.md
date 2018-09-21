# topeka-perf
This project is a fork of [Topeka](https://github.com/googlesamples/android-topeka) for build performance benchmarking. Specifically, we are using this project to compare Java and Kotlin build performance.


## Project and Methodology
* Started with the Java version of Topeka [here](https://github.com/googlesamples/android-topeka/tree/java).
* Updated to use Android Gradle plugin com.android.tools.build:gradle:3.2.0-rc03. Make sure you also update the databinding version to match.
* For Kotlin version, I configured the project with Kotlin 1.2.60, then used Java to Kotlin converter to convert all the Java files. Fix up all the build errors just so it builds :)
* For each project, use the [Gradle Profiler](https://github.com/gradle/gradle-profiler) to run benchmark.
  For example,
  
    ```> gradle-profiler --benchmark --project-dir --scenario-file performance.scenarios incremental_build --output-dir ./profile-out-incremental_build```
    
    There is a ```performance.scenarios``` file in topeka-java and topeka-kotlin folders. Each ```performance.scenario``` file contains 2 scenarios: a clean build and an incremental build.

