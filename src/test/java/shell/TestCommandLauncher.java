package shell;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class TestCommandLauncher {

    @DisabledOnOs({OS.WINDOWS})
    @Test
    public void testLaunchUnixLikeShellCommand() {
        var launcher = CommandLauncher.builder()
            .program("/bin/sh")
            .parameter("-c")
            .parameter("ls")
            .cwd(new File(System.getProperty("java.io.tmpdir")))
            .parameter("-lah")
            .build();
        try {
            var results = launcher.launch();
            System.out.println(results.getExitCode());
            System.out.println(results.getStandardOutput());
        } catch(ShellException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }


    @EnabledOnOs({OS.WINDOWS})
    @Test
    public void testLaunchWindowsShellCommand() {
        var launcher = CommandLauncher.builder()
            .program("cmd.exe")
            .parameter("/c")
            .parameter("dir")
            .parameter("/A")
            .cwd(new File(System.getProperty("java.io.tmpdir")))
            .build();
        try {
            var results = launcher.launch();
            System.out.println(results.getExitCode());
            System.out.println(results.getStandardOutput());
        } catch(ShellException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    public void testNonNunll() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            CommandLauncher.builder()
                .program("cmd.exe")
                .parameter("/c")
                .parameter("dir").build();
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            CommandLauncher.builder()
                .parameter("cmd.exe")
                .parameter("/c")
                .parameter("dir")
                .cwd(new File(System.getProperty("java.io.tmpdir")))
                .build();
        });
    }
}
