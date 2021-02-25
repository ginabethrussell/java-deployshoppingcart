package com.lambdaschool.shoppingcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer // normally this is run by a 3rd party, we are setting up our own
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{
    private static String CLIENT_ID = System.getenv("OAUTHCLIENTID");
    private static String CLIENT_SECRET = System.getenv("OAUTHCLIENTSECRET");

    // means our users will use a username and password
    private static String GRANT_TYPE_PASSWORD = "password";
    // front end can sign on with client id and secret or an authorization code
    private static String AUTHORIZATION_CODE = "authorization_code";
    private static String SCOPE_READ = "read";
    private static String SCOPE_WRITE = "write";
    private static String SCOPE_TRUST = "trust";
    // -1 means forever, time in seconds before user needs to sign back on
    private static int ACCESS_TOKEN_VALIDITY_SECONDS = -1;

    // need a place to store the tokens we send after we create them
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        // we do all this in memory, just faster to access, and more secure, allows easy resets
        clients.inMemory()
            .withClient(CLIENT_ID)
            .secret(encoder.encode(CLIENT_SECRET))
            .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE)
            .scopes(SCOPE_WRITE,SCOPE_READ,SCOPE_TRUST)
            .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
    {
        endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager);
        // optional
        endpoints.pathMapping("/oauth/token", "/login");
    }
}
