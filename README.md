[![.github/workflows/maven-publish.yml](https://github.com/jorgeb1980/lib-shell-commands/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/jorgeb1980/lib-shell-commands/actions/workflows/maven-publish.yml)

# lib-shell-commands
Small library trying to address some common trouble when launching shell commands in Java.
Main features:
- General command launcher and specific shell command launcher
- Hides OS specific considerations
- Retrieval of the output using an implementation of stream gobbler to solve race conditions.  Some docs on this common issue in
  - https://stackoverflow.com/questions/10031368/process-never-ends-with-processbuilder
  - https://users.tomcat.apache.narkive.com/bTX4WvSd/runtime-getruntime-exec-problem
  - https://coderanch.com/t/605311/java/race-condition-Runtime-exec

# Samples

Launch "ls -lh" in a Unix-like environment
```java
var launcher = ShellCommandLauncher.builder()
    .command("ls")
    .parameter("-lah")
    .cwd(new File("/some/directory"))
    .build();
try {
    var results = launcher.launch();
    if (results.getExitCode() == 0)
        System.out.println(results.getStandardOutput());
    else
        System.err.println(results.getErrorOutput());
} catch(ShellException e) {
    e.printStackTrace();
}
```

Launch "dir /A" in a Windows environment
```java
var launcher = ShellCommandLauncher.builder()
    .command("dir")
    .parameter("/A")
    .cwd(new File("c:\\some\\directory"))
    .build();
try {
    var results = launcher.launch();
    if (results.getExitCode() == 0)
        System.out.println(results.getStandardOutput());
    else
        System.err.println(results.getErrorOutput());
} catch(ShellException e) {
    e.printStackTrace();
}
```

Launch unrar in order to decompress some file (assuming unrar is on the path)
```java
var launcher = CommandLauncher.builder()
    .program("unrar")
    .parameter("x")
    .parameter("/path/to/some/file.rar")
    .cwd(new File("/path/to/target/directory"))
    .build();
try {
    var results = launcher.launch();
    if (results.getExitCode() == 0)
        System.out.println(results.getStandardOutput());
    else
        System.err.println(results.getErrorOutput());
} catch(ShellException e) {
    e.printStackTrace();
}
```

Launch some shell script that requires setting some env variables
```java
var launcher = CommandLauncher.builder()
    .program("c:\\tools\\maven\\mvn.cmd")
    .parameter("clean")
    .parameter("install")
    .cwd(new File("c:\\my\\java\\project"))
    .env("JAVA_HOME", "c:\\java\\jdk17")
    .build();
try {
    var results = launcher.launch();
    if (results.getExitCode() == 0)
        System.out.println(results.getStandardOutput());
    else
        System.err.println(results.getErrorOutput());
} catch(ShellException e) {
    e.printStackTrace();
}
```