/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.services;

import de.com2m.iot.example.feign.model.Building;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "asset-service", url = "${com2m.iot-cloud.url}", path = "/api/asset-service/items/Building:Group")
public interface BuildingService {

	@RequestMapping(method = RequestMethod.GET)
	Resources<Building> getAll();

	@RequestMapping(method = RequestMethod.POST)
	Building create(Building building);

}
