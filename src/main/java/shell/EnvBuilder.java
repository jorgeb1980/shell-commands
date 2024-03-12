package shell;

import java.util.LinkedList;
import java.util.List;

public abstract class EnvBuilder {

    private List<String[]> envs;

    public EnvBuilder env(String env, String value) {
        if (envs == null) envs = new LinkedList<>();
        envs.add(new String[]{env, value});
        return this;
    }
}
