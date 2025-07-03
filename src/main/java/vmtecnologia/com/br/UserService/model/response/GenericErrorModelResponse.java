package vmtecnologia.com.br.UserService.model.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericErrorModelResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
