package gr.abiss.calipso.tiers.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import gr.abiss.calipso.web.spring.ParameterMapBackedPageRequest;

public interface BuildPageable {
	
	public default Pageable buildPageable(Integer page, Integer size, String sort,
			String direction, Map<String, String[]> paramsMap) {
		Assert.isTrue(page >= 0, "Page index must be greater than, or equal to, 0");

		List<Order> orders = null;
		Sort pageableSort = null;
		if(sort != null && direction != null){
			Order order = new Order(
					direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
							: Sort.Direction.DESC, sort);
			orders = new ArrayList<Order>(1);
			orders.add(order);
			pageableSort = new Sort(orders);
		}
		Pageable pageable = new ParameterMapBackedPageRequest(paramsMap, page /*- 1*/, size, pageableSort);
		return pageable;
	}
}
