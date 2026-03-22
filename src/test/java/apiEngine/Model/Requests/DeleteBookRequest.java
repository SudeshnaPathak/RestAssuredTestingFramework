package apiEngine.Model.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteBookRequest {
	private String isbn;
	private UUID userId;

}
