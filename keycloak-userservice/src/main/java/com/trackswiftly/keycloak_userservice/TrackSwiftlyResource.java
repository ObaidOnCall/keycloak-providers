package com.trackswiftly.keycloak_userservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.OrganizationModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.organization.OrganizationProvider;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;
import org.keycloak.services.resources.KeycloakOpenAPI;

import com.trackswiftly.keycloak_userservice.middlewares.AuthenticateMiddleware;
import com.trackswiftly.keycloak_userservice.services.OrganizationInvitationService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


public class TrackSwiftlyResource {

    private final KeycloakSession session;
    private final RealmModel realm;
    private final OrganizationProvider provider;


    public TrackSwiftlyResource(
		KeycloakSession session
	) {
        this.session = session;
        this.realm = session.getContext().getRealm();
        this.provider = session.getProvider(OrganizationProvider.class);
    }


    @GET
	@Path("hello")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Public hello endpoint",
		description = "This endpoint returns hello and the name of the requested realm."
	)
    @APIResponse(
		responseCode = "200",
		description = "",
		content = {@Content(
			schema = @Schema(
				implementation = Response.class,
				type = SchemaType.OBJECT
			)
		)}
	)
    public Response helloAnonymous() {
		return Response.ok(Map.of("hello", session.getContext().getRealm().getName())).build();
	}




	@Path("invite-user")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Tag(name = KeycloakOpenAPI.Admin.Tags.ORGANIZATIONS)
    @Operation(summary = "Invites an existing user or sends a registration link to a new user, based on the provided e-mail address.",
            description = "If the user with the given e-mail address exists, it sends an invitation link, otherwise it sends a registration link.")
    public Response inviteUser(@FormParam("email") String email,
                               @FormParam("firstName") String firstName,
                               @FormParam("lastName") String lastName) {
        
        
        AuthenticateMiddleware.checkRealm(session);
        AuthResult authResult = AuthenticateMiddleware.checkAuthentication(session) ;
        AuthenticateMiddleware.checkRole(authResult, session, List.of("ADMIN" , "MANAGER")) ;

        OrganizationModel organization = provider.getById("eadeaa80-5b4e-45c5-ba99-224cdf81cb87") ;

        return new OrganizationInvitationService(session, organization).inviteUser(email, firstName, lastName);
    }


    @GET
	@Path("myorg")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Oragnization endpoint",
		description = "This endpoint returns Oranization of the current user ."
	)
    @APIResponse(
		responseCode = "200",
		description = "",
		content = {@Content(
			schema = @Schema(
				implementation = Response.class,
				type = SchemaType.OBJECT
			)
		)}
	)
    public Response myOrg() {

        AuthenticateMiddleware.checkRealm(session);
        AuthResult authResult = AuthenticateMiddleware.checkAuthentication(session) ;


        UserModel authenticatedUser = authResult.getUser();

        Stream<OrganizationModel> organizations = provider.getByMember(authenticatedUser);

        Optional<OrganizationModel> firstOrganization = organizations.findFirst();

        if (firstOrganization.isPresent()) {
            OrganizationModel organization = firstOrganization.get();

            return Response.ok(
                Map.of(
                    "name", organization.getName() ,
                    "id" , organization.getId()
                )
            ).build();
        } else {

            return Response.status(Response.Status.NOT_FOUND)
                           .entity("No organization found for the user.")
                           .build();
        }

	}










    
}
