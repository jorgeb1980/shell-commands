[![.github/workflows/maven-publish.yml](https://github.com/jorgeb1980/lib-shell-commands/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/jorgeb1980/lib-shell-commands/actions/workflows/maven-publish.yml)

# lib-shell-commands
Small library trying to address some common trouble when launching shell commands in Java

# Samples

Launch "ls -lh" in a Unix-like environment
```java
var launcher = ShellCommandLauncher.builder()
    .command("ls")
    .cwd(new File("/some/directory"))
    .parameter("-lah")
    .build();
try {
    var results = launcher.launch();
    if(results.getExitCode() == 0)
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
    if(results.getExitCode() == 0)
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
    if(results.getExitCode() == 0)
        System.out.println(results.getStandardOutput());
    else
        System.err.println(results.getErrorOutput());
} catch(ShellException e) {
    e.printStackTrace();
}
```