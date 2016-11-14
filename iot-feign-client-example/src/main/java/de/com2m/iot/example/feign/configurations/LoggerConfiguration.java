/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerConfiguration {

	@Bean
	@Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
	public Logger expose(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}

}
