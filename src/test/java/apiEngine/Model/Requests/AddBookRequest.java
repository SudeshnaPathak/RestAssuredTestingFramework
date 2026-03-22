package apiEngine.Model.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddBookRequest {
    private UUID userId;
    private List<Map<String, String>> collectionOfIsbns;
}
