package apiEngine.Model.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteBookRequest {
	private String isbn;
	private String userId;

}
