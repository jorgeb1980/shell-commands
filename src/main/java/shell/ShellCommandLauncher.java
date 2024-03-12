package shell;


import lombok.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShellCommandLauncher {

    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @NonNull
    private String command;
    @Singular
    private List<String> parameters;
    private List<String[]> envs;
    private File cwd;

    // Customization of lombok builder leads to certain code repetition I have not been able to solve
    public static class ShellCommandLauncherBuilder {
        public ShellCommandLauncherBuilder env(String env, String value) {
            if (envs == null) envs = new LinkedList<>();
            envs.add(new String[]{env, value});
            return this;
        }
    }

    public ExecutionResults launch() throws ShellException {
        var builder = CommandLauncher.builder();
        if (isWindows) {
            builder.program("cmd.exe")
                .parameter("/c")
                .parameter(command);
        } else {
            builder.program("/bin/sh")
                .parameter("-c")
                .parameter(command);
        }
        builder.parameters(parameters).envs(envs).cwd(cwd);
        return builder.build().launch();
    }
}
