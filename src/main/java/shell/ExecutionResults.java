package shell;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExecutionResults {
    
    private String standardOutput;
    private String errorOutput;
    private Integer exitCode;

}
