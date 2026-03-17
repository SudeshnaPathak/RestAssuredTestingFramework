package pojos;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddBookPojo {
	private String userId;
	private List<Map<String, String>> collectionOfIsbns;
}
