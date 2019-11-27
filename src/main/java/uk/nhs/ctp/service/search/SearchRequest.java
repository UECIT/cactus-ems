package uk.nhs.ctp.service.search;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public abstract class SearchRequest extends PageRequest {

	private static final long serialVersionUID = 1L;

	public SearchRequest(Integer page, Integer size, Collection<SearchSort> sorts) {
		super(page, size, createSort(sorts));	
	}
	
	private static Sort createSort(Collection<SearchSort> sorts) {
		return Sort.by(sorts.stream().map(sort ->
			new Order(sort.getDirection(), sort.getSortField().getFieldName()))
				.collect(Collectors.toList()));
	}
}
