package shell;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ExecutionResults {
    
    @Getter
    private String standardOutput;
    @Getter
    private String errorOutput;
    @Getter
    private Integer exitCode;

}
