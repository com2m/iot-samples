/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.controllers;

import de.com2m.iot.example.feign.model.Building;
import de.com2m.iot.example.feign.services.BuildingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

	@Autowired
	private BuildingService buildingService;
	@Autowired
	private Logger logger;


	@RequestMapping(method = RequestMethod.POST)
	public void create(@RequestParam("name") String name) {
		Building building = new Building();
		building.setName(name);
		Building responseBuilding = buildingService.create(building);

		logger.info("Created Building with name: {}", responseBuilding.getName());
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public int count() {
		Resources<Building> buildings = buildingService.getAll();

		return buildings.getContent().size();
	}

	@RequestMapping(value = "/names", method = RequestMethod.GET)
	public List<String> getNames() {
		Resources<Building> buildings = buildingService.getAll();

		return buildings.getContent()
				.stream()
				.map(Building::getName)
				.collect(Collectors.toList());
	}

}
