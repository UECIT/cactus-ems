package uk.nhs.ctp.service.search;

import org.springframework.data.domain.Sort.Direction;

public class SearchSort {

	protected enum SortField {
		
		TIMESTAMP("timestamp"),
		FIRST_NAME("firstName"),
		LAST_NAME("lastName");
		
		private String fieldName;
		
		private SortField(String fieldName) {
			this.fieldName = fieldName;
		}
		
		public String getFieldName() {
			return fieldName;
		}
	}
	
	private SortField sortField;
	private Direction direction;
	
	public SortField getSortField() {
		return sortField;
	}
	public void setSortField(SortField sortField) {
		this.sortField = sortField;
	}
	
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
