package shell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jodd.io.StreamGobbler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLauncher {
    
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    @NonNull
    private String program;
    @Singular
    private List<String> parameters;
    @Singular
    private List<String> envs;
    @NonNull
    private File cwd;

    private String buildString(byte[] content) {
        return new String(content, isWindows ? Charset.forName("cp1252") : Charset.forName("UTF-8"));
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
            pb.directory(cwd);
            if (!envs.isEmpty()) {
                var environment = pb.environment();
                for (var envVar: envs) {
                    // Will only work if the env has the format KEY=VALUE
                    String[] parts = envVar.split("=");
                    if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                        environment.put(parts[0], parts[1]);
                    }
                }
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
