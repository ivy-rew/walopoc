package com.microsoft.auth;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.reflect.MethodUtils;

import ch.ivyteam.api.PublicAPI;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2BearerFilter;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2TokenRequester;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProvider;

/**
 * Filter to simplify OAUTH2 authorization flows.
 *
 * <p>
 * It initiates OAUTH2 authentication requests to resolve a 'Bearer' token which
 * is sub-sequently added as 'Authorization' request header.
 * </p>
 *
 *
 * @since 9.2
 */
@Provider
@PublicAPI
public class OAuth2BearerFilter2 extends OAuth2BearerFilter {
  private static final String AUTHORIZATION = "Authorization";
  private static final String SUBSCRIPTION = "Ocp-Apim-Subscription-Key";

  //private static final String BEARER = "Bearer ";
  private OAuth2UriProvider uriFactory;
  private OAuth2TokenRequester getToken;

  /**
   * @param getToken a {@link OAuth2TokenRequester} implementation that actually does the 'accessToken' call to the OAUTH2 authority
   * in case the current session has not already a valid accessToken.
   * @param uriFactory an {@link OAuth2UriProvider} to setup OAUTH2 authority URIs for the various requests
   * that may must be fired (.e.g /auth, /token, /userinfo, ...)
   */
  @PublicAPI
  public OAuth2BearerFilter2(OAuth2TokenRequester getToken, OAuth2UriProvider uriFactory) {
    super(getToken, uriFactory);
    this.uriFactory = uriFactory;
    this.getToken = getToken;
  }

  @Override
  public void filter(ClientRequestContext context) throws IOException {
    if (uriFactory.isAuthRequest( context.getUri())) { // already in token request: avoid stackOverflow
      return;
    }

    if (context.getHeaders().containsKey(AUTHORIZATION)) { // already set by other feature or explicit header
      return;
    }

    String accessToken = getAccessTokenC(context);
    context.getHeaders().add(AUTHORIZATION, accessToken);
    context.getHeaders().add(SUBSCRIPTION, "7e6e94f3391a4d66a845f2258940e176");
  }

  private String getAccessTokenC(ClientRequestContext context) {
    try {
      return (String) MethodUtils.invokeMethod(this, true, "getAccessToken", context);
    } catch (Exception ex) {
      Ivy.log().error("failed to get access token ", ex);
    }
    return null;
  }

}