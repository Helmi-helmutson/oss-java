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

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class ServerApplication extends Application<ServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new ServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "OSS API";
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

        //Das ist das bescheuertste was ich je in meinem Leben gesehen habe.
        AuthFilter tokenAuthorizer = new OAuthCredentialAuthFilter.Builder<Session>()
                .setAuthenticator(new OSSTokenAuthenticator())
                .setAuthorizer(new OSSAuthorizer())
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(tokenAuthorizer));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Session.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        final SystemResource systemResource = new SystemResourceImpl();
        environment.jersey().register(systemResource);
        
        final SessionsResource sessionsResource = new SessionsResourceImpl();
        environment.jersey().register(sessionsResource);

        final RoomResource roomsResource = new RoomRescourceImpl();
        environment.jersey().register(roomsResource);
   
        final UserResource usersResource = new UserResourceImpl();
        environment.jersey().register(usersResource);
        
        final GroupResource groupsResource = new GroupResourceImpl();
        environment.jersey().register(groupsResource);
        
        final DeviceResource devicesResource = new DeviceResourceImpl();
        environment.jersey().register(devicesResource);
        
        final CloneToolResource cloneToolResource = new CloneToolRescourceImpl();
        environment.jersey().register(cloneToolResource);
        
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

    }

}
