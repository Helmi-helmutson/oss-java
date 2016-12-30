/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api;

import de.openschoolserver.api.auth.OSSAuthorizer;
import de.openschoolserver.api.auth.OSSTokenAuthenticator;
import de.openschoolserver.api.health.TemplateHealthCheck;
import de.openschoolserver.api.resourceimpl.*;
import de.openschoolserver.api.resources.*;
import de.openschoolserver.dao.Session;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ServerApplication extends Application<ServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new ServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "ClaXss Infoline API";
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<ServerConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServerConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(ServerConfiguration configuration, Environment environment) {

        AuthFilter tokenAuthorizer = new OAuthCredentialAuthFilter.Builder<Session>()
                .setAuthenticator(new OSSTokenAuthenticator())
                .setAuthorizer(new OSSAuthorizer())
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(tokenAuthorizer));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Session.class));
        // TODO required? environment.jersey().register(RolesAllowedDynamicFeature.class);

        final SessionsResource sessionsResource = new SessionsResourceImpl();
        environment.jersey().register(sessionsResource);

        final RoomResource roomsResource = new RoomRescourceImpl();
        environment.jersey().register(roomsResource);
   
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

    }

}
