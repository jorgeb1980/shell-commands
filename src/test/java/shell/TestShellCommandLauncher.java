package shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TestShellCommandLauncher {

    @DisabledOnOs({OS.WINDOWS})
    @Test
    public void testUnixLikeLaunchCommand() {
        var launcher = ShellCommandLauncher.builder()
            .command("ls")
            .cwd(new File(System.getProperty("java.io.tmpdir")))
            .parameter("-lah")
            .build();
        try {
            var results = launcher.launch();
            System.out.println(results.getExitCode());
            System.out.println(results.getStandardOutput());
        } catch(ShellException e) {
            e.printStackTrace();
            fail();
        }
    }

    @DisabledOnOs({OS.WINDOWS})
    @Test
    public void testUnixLikeErrorConditions() {
        assertThrows(ShellException.class, () -> {
            ShellCommandLauncher.builder()
                .command("ls")
                .cwd(new File("/this/directory/should/not/exist"))
                .parameter("-lah")
                .build().launch();
        });
        assertThrows(ShellException.class, () -> {
            ShellCommandLauncher.builder()
                .command("ls")
                // Should exist, but not a directory
                .cwd(new File("/bin/sh"))
                .parameter("-lah")
                .build().launch();
        });
    }


    @EnabledOnOs({OS.WINDOWS})
    @Test
    public void testWindowsLaunchCommand() {
        var launcher = ShellCommandLauncher.builder()
            .command("dir")
            .parameter("/A")
            .cwd(new File(System.getProperty("java.io.tmpdir")))
            .build();
        try {
            var results = launcher.launch();
            System.out.println(results.getExitCode());
            System.out.println(results.getStandardOutput());
        } catch(ShellException e) {
            e.printStackTrace();
            fail();
        }
    }

    @EnabledOnOs({OS.WINDOWS})
    @Test
    public void testWindowsErrorConditions() {
        assertThrows(ShellException.class, () -> {
            ShellCommandLauncher.builder()
                .command("dir")
                .parameter("/A")
                .cwd(new File("c:\\this\\directory\\should\\not\\exist"))
                .build().launch();
        });
        assertThrows(ShellException.class, () -> {
            ShellCommandLauncher.builder()
                .command("dir")
                .parameter("/A")
                // Should exist, but not a directory
                .cwd(new File("c:\\windows\\explorer.exe"))
                .build().launch();
        });
    }

    @Test
    public void testNonNull() {
        assertThrows(NullPointerException.class, () -> {
            ShellCommandLauncher.builder()
                // missing command parameter
                .parameter("dir")
                .cwd(new File(System.getProperty("java.io.tmpdir")))
                .build();
        });
    }
}
