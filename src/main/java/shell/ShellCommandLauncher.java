package shell;


import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.io.File;
import java.util.List;

@Builder
public class ShellCommandLauncher {

    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @NonNull
    private String command;
    @Singular
    private List<String> parameters;
    @Singular
    private List<String> envs;
    private File cwd;


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
