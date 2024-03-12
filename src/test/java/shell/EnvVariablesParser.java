package shell;

import java.util.HashMap;
import java.util.Map;

public class EnvVariablesParser {

    // parses the output of set or env
    public Map<String, String> parse(String output) {
        var ret = new HashMap<String, String>();
        output.lines().forEach(s -> {
            if (s.contains("=")) {
                String parts[] = s.split("=");
                if (parts.length == 2 && parts[0] != null && parts[1] != null) ret.put(parts[0], parts[1]);
            }
        });
        return ret;
    }
}
