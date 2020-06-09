package uk.nhs.ctp.caseSearch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
public class SearchSort {

	@RequiredArgsConstructor
	@Getter
	protected enum SortField {

		DATE_CREATED("createdDate"),
		FIRST_NAME("firstName"),
		LAST_NAME("lastName");

		private final String name;
	}
	
	private SortField sortField;
	private Direction direction;

}
