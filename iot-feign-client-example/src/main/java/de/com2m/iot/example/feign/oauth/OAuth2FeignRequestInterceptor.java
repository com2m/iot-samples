/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.example.feign.oauth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2FeignRequestInterceptor.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_TOKEN_TYPE = "Bearer";


	@Override
	public void apply(RequestTemplate template) {
		if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
			LOGGER.warn("The Authorization token has been already set");
		} else {
			String accessToken = getAccessToken();

			if (accessToken == null) {
				LOGGER.warn("Can not obtain existing token for request from security context.");
			} else {
				LOGGER.debug("Constructing Header {} for Token {}", AUTHORIZATION_HEADER, BEARER_TOKEN_TYPE);
				template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE,
						accessToken));
			}
		}
	}

	private String getAccessToken() {
		String accessToken = null;

		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			Object authenticationDetails = context.getAuthentication().getDetails();
			if (authenticationDetails instanceof OAuth2AuthenticationDetails) {
				accessToken = ((OAuth2AuthenticationDetails) authenticationDetails).getTokenValue();
			} else if (authenticationDetails instanceof OAuth2AccessToken) {
				accessToken = ((OAuth2AccessToken) authenticationDetails).getValue();
			}
		}

		return accessToken;
	}

}
