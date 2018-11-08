/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.services;

import de.com2m.iot.example.feign.model.Building;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "asset-service", url = "${com2m.iot-cloud.url}", path = "/api/asset-service/items")
public interface BuildingService {

	@RequestMapping(value = "Building", method = RequestMethod.GET)
	PagedResources<Building> getAll();

	@RequestMapping(value = "Building:Group", method = RequestMethod.POST)
	Building create(Building building);

}
