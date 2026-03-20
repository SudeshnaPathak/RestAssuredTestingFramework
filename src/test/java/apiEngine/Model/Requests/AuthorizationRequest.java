package apiEngine.Model.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizationRequest {
	private String UserName;
	private String password;
}
