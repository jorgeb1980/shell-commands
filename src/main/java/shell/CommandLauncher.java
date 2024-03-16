package shell;

import jodd.io.StreamGobbler;
import lombok.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommandLauncher {

    @NonNull
    private String program;
    protected final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @Singular
    protected List<String> parameters;
    @Singular
    private Map<String, String> envs;
    protected File cwd;

    private String buildString(byte[] content) {
        return new String(content, isWindows ? Charset.forName("cp1252") : StandardCharsets.UTF_8);
    }

    public ExecutionResults launch() throws ShellException {
        try {
            List<String> command = new LinkedList<>();
            command.add(program);
            command.addAll(parameters);

            ProcessBuilder pb = new ProcessBuilder(
                command.stream().map(s -> {
                    if (s.contains(" ")) 
                        return "\"" + s + "\""; 
                    else return s;
                }).collect(Collectors.toList())
            );
            if (cwd == null) cwd = new File(System.getProperty("user.dir"));
            else {
                // Some sanity check on cwd
                if (!cwd.exists()) {
                    throw new ShellException(format("Directory %s does not exist", cwd));
                } else if (!cwd.isDirectory()) {
                    throw new ShellException(format("%s is not a directory", cwd));
                }
            }
            pb.directory(cwd);
            if (envs != null && !envs.isEmpty()) {
                var environment = pb.environment();
                environment.putAll(envs);
            }
            ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            Process p = pb.start();
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), standardOutputStream);
            outputGobbler.start();
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), errorOutputStream);
            errorGobbler.start();

            Integer exitCode = p.waitFor();
            outputGobbler.waitFor();
            errorGobbler.waitFor();
            return ExecutionResults.builder().
                exitCode(exitCode).
                errorOutput(buildString(errorOutputStream.toByteArray())).
                standardOutput(buildString(standardOutputStream.toByteArray())).
                build();
        } catch (IOException | InterruptedException e) {
            throw new ShellException(e);
        }
    }
}
