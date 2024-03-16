package shell;


import lombok.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShellCommandLauncher {

    @NonNull
    private String command;
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @Singular
    private List<String> parameters;
    @Singular
    private Map<String, String> envs;
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
