/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.configurations;

import de.com2m.iot.example.feign.oauth.OAuth2FeignRequestInterceptor;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Feign.class})
@ConditionalOnProperty(value = "feign.oauth2.enabled", matchIfMissing = true)
public class FeignClientConfiguration {

	@Bean
	public RequestInterceptor oauth2FeignRequestInterceptor() {
		return new OAuth2FeignRequestInterceptor();
	}

}
