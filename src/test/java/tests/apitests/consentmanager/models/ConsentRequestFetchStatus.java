package tests.apitests.consentmanager.models;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentRequestFetchStatus {

  public List<Object> requestIds;
}
