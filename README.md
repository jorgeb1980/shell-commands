# lib-shell-commands
Small library trying to address some common trouble when launching shell commands in Java

# Samples

Launch "ls -lh" in a Unix-like environment
```java
var launcher = CommandLauncher.builder()
    .program("/bin/sh")
    .parameter("-c")
    .parameter("ls")
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
var launcher = CommandLauncher.builder()
    .program("cmd.exe")
    .parameter("/c")
    .parameter("dir")
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