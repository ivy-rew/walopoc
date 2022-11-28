package com.microsoft.auth;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.ivy.bpm.error.BpmPublicErrorBuilder;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.FeatureConfig;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2RedirectErrorBuilder;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2TokenRequester.AuthContext;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2CallbackUriBuilder;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProperty;

/**
 * Microsoft Graph auth flow implementation.
 *
 * <ul>
 * <li>Requires a registered application:
 * https://docs.microsoft.com/en-us/graph/tutorials/java?tutorial-step=2</li>
 * <li>Resolves accessTokens as described here:
 * https://docs.microsoft.com/en-us/graph/auth-v2-user</li>
 * </ul>
 *
 * @since 9.2
 */
public class OAuth2FeatureC implements Feature {

  public static interface Default {
    String APP_SCOPE = "https://graph.microsoft.com/.default";
    String AUTH_URI = "https://login.microsoftonline.com/common/oauth2/v2.0";
    String SCOPE = "user.read";
  }

  public static interface Property {
    String APP_ID = "AUTH.appId";
    String CLIENT_SECRET = "AUTH.secretKey";
    String SCOPE = "AUTH.scope";
    String AUTH_BASE_URI = "AUTH.baseUri";
    String USE_APP_PERMISSIONS = "AUTH.useAppPermissions";
    String USE_USER_PASS_FLOW = "AUTH.userPassFlow";

    String USER = "AUTH.user";
    String PASS = "AUTH.password";
  }

  @Override
  public boolean configure(FeatureContext context) {
    var config = new FeatureConfig(context.getConfiguration(), OAuth2FeatureC.class);
    var graphUri = new OAuth2UriProperty(config, Property.AUTH_BASE_URI,
            Default.AUTH_URI);
    var oauth2 = new OAuth2BearerFilter2(
            ctxt -> requestToken(ctxt, graphUri),
            graphUri);
    context.register(oauth2, Priorities.AUTHORIZATION);
    Ivy.log().info("oauth2 ms");
    return true;
  }

  private static Response requestToken(AuthContext ctxt, OAuth2UriProperty uriFactory) {
    Ivy.log().info("request token");
    FeatureConfig config = ctxt.config;
    var authCode = ctxt.authCode();
    var refreshToken = ctxt.refreshToken();
    boolean isAppAuth = isAppAuth(config);
    if (authCode.isEmpty() && refreshToken.isEmpty() &&
            !(isAppAuth || isUserPassAuth(config))) {
      Ivy.log().info("request token 2: appAuth="+isAppAuth);
      authError(config, uriFactory)
              .withMessage("missing permission from user to act in his name.")
              .throwError();
    }

    Form form = createTokenPayload(config, authCode, refreshToken);
    var response = ctxt.target.request()
            .accept(MediaType.WILDCARD)
            .post(Entity.form(form));
    return response;
  }

  private static boolean isAppAuth(FeatureConfig config) {
    return bool(config.read(Property.USE_APP_PERMISSIONS));
  }

  private static boolean isUserPassAuth(FeatureConfig config) {
    return bool(config.read(Property.USE_USER_PASS_FLOW));
  }

  private static boolean bool(Optional<String> value) {
    return "true".equals(value.orElse(null));
  }

  static Form createTokenPayload(FeatureConfig config, Optional<String> authCode,
          Optional<String> refreshToken) {
    Ivy.log().info("token payload");
    Form form = new Form();
    form.param("client_id", config.readMandatory(Property.APP_ID));
    if (authCode.isPresent()) {
      // use user permissions
      form.param("scope", getScope(config));
      form.param("redirect_uri", OAuth2CallbackUriBuilder.create().toUri().toASCIIString());
      form.param("code", authCode.get());
      form.param("grant_type", "authorization_code");
    } else {
      if (isUserPassAuth(config)) {
        // weak security: app acts as personal user!
        form.param("scope", getScope(config));
        form.param("username", config.readMandatory(Property.USER));
        form.param("password", config.readMandatory(Property.PASS));
        form.param("grant_type", "password");
      } else {
        // use app permission
        form.param("scope", getScope(config));
        form.param("grant_type", "client_credentials");
      }
    }

    if (refreshToken.isPresent()) {
      form.param("scope", getScope(config));
      form.param("redirect_uri", OAuth2CallbackUriBuilder.create().toUri().toASCIIString());
      form.param("refresh_token", refreshToken.get());
      form.param("grant_type", "refresh_token");
    }
    form.param("client_secret", config.readMandatory(Property.CLIENT_SECRET));
    Ivy.log().debug("authenticating: " + form.asMap());
    return form;
  }

  private static BpmPublicErrorBuilder authError(FeatureConfig config, OAuth2UriProperty uriFactory) {
    var uri = createMsAuthCodeUri(config, uriFactory);
    return OAuth2RedirectErrorBuilder
            .create(uri)
            .withMessage("Missing permission from user to act in his name.");
  }

  private static URI createMsAuthCodeUri(FeatureConfig config, OAuth2UriProperty uriFactory) {
    return UriBuilder.fromUri(uriFactory.getUri("authorize"))
            .queryParam("client_id", config.readMandatory(Property.APP_ID))
            .queryParam("scope", getScope(config))
            .queryParam("redirect_uri", OAuth2CallbackUriBuilder.create().toUri())
            .queryParam("response_type", "code")
            .queryParam("response_mode", "query")
            .build();
  }

  private static String getScope(FeatureConfig config) {
    return config.read(Property.SCOPE).orElse(Default.SCOPE);
  }
}
