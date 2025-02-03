package com.trackswiftly.keycloak_userservice.middlewares;

import java.util.List;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;

import com.trackswiftly.keycloak_userservice.dtos.TrackSwiftlyRoles;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;

public class AuthenticateMiddleware {


    private AuthenticateMiddleware () {}

    public static AuthResult checkAuthentication(KeycloakSession session) {
        AuthResult auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        
        if (auth == null) {
			throw new NotAuthorizedException("Bearer");
		}

        return auth ;
    }


    public static void checkRealm(KeycloakSession session) {
        RealmModel realm = session.getContext().getRealm();
        String realmName = realm.getName();

        if (!realmName.toLowerCase().matches(".*?(track|swiftly).*")) {
            throw new ForbiddenException("This Endpoint is only available in realms containing 'track' or 'swiftly' in their name");
        }
    }


    public static void checkRole(
        AuthResult auth ,
        KeycloakSession session ,
        List<TrackSwiftlyRoles> roleNames
    ) {

        UserModel authenticatedUser = auth.getUser();

        if (!userHasAnyRole(session, authenticatedUser, roleNames)) {
            throw new ForbiddenException("You are not allowed") ;
        }
    }





    /**
     * Checks if the user has any of the specified roles.
     *
     * @param session the Keycloak session
     * @param user the user to check
     * @param roleNames the list of role names to check against
     * @return true if the user has any of the roles, false otherwise
     */
    private static boolean userHasAnyRole(KeycloakSession session, UserModel user, List<TrackSwiftlyRoles> roleNames) {
        for (TrackSwiftlyRoles roleName : roleNames) {
            RoleModel role = session.getContext().getRealm().getRole(roleName.toString());
            if (role != null && user.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}