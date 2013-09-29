package com.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;

@Path(RestAuthencationService.AUTH_PATH)
public class RestAuthencationService extends RestItemBaseService<SessionImpl> {

    public static final String LOGOUT_PATH = "logout";
    public static final String LOGIN_PATH = "login";
    public static final String AUTH_PATH = "auth";
    public static final String ES_DMS_TICKET = "ES_DMS_TICKET";

    @Inject
    public RestAuthencationService(AuthenticationService authenticationService) {
	super(authenticationService, authenticationService);
    }

    @POST
    @Path(LOGIN_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response login(Credential credential) {
	try {
	    checkNotNull(credential);
	    if (log.isTraceEnabled()) {
		log.trace(String.format("login - %s", credential.getUsername()));
	    }
	    log.trace("getHost: " + url.getBaseUri().getHost());
	    log.trace("getPath: " + url.getPath());
	    String token = authenticationService.login(credential);
	    if (log.isTraceEnabled()) {
		log.trace(String.format("Create cookie %s: [%s]", ES_DMS_TICKET, token));
	    }
	    return Response.ok().entity(new AuthenticationResponse("AUTHENTICATED", token))
		    .cookie(new NewCookie(ES_DMS_TICKET, token, "/", null, 1, url.getBaseUri().getHost(), 30000, false)).build();
	} catch (Throwable t) {
	    log.error("login failed", t);
	    throw new RestServiceException(t.getLocalizedMessage());
	}
    }

    @POST
    @Path(LOGOUT_PATH)
    public Response logout(@CookieParam(value = ES_DMS_TICKET) String token) {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("logout: %s", token));
	    }
	    authenticationService.logout(token);
	    return Response.ok().cookie(new NewCookie(ES_DMS_TICKET, "", "/", "", "", -1, false)).build();
	} catch (Throwable t) {
	    log.error("logout failed", t);
	    throw new RestServiceException(t.getLocalizedMessage());
	}
    }

    private class AuthenticationResponse {
	private final String status;
	private final String token;

	public AuthenticationResponse(String status, String token) {
	    this.status = status;
	    this.token = token;
	}

	@SuppressWarnings("unused")
	public String getToken() {
	    return token;
	}

	@SuppressWarnings("unused")
	public String getStatus() {
	    return status;
	}
    }
}
