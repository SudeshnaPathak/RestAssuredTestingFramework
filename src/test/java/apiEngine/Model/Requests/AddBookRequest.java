package apiEngine.Model.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AddBookRequest {
	private String userId;
	private List<Map<String, String>> collectionOfIsbns;
}
