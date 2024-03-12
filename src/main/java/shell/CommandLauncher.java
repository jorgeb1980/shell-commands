package shell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jodd.io.StreamGobbler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommandLauncher {
    
    protected final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @NonNull
    private String program;
    @Singular
    protected List<String> parameters;
    private Map<String, String> envs;
    protected File cwd;

    // Customization of lombok builder leads to certain code repetition I have not been able to solve
    public static class CommandLauncherBuilder {
        public CommandLauncher.CommandLauncherBuilder env(String env, String value) {
            if (envs == null) envs = new HashMap<>();
            envs.put(env, value);
            return this;
        }
    }

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
                    throw new Exception(format("Directory %s does not exist", cwd));
                } else if (!cwd.isDirectory()) {
                    throw new Exception(format("%s is not a directory", cwd));
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
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
