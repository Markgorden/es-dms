package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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


import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

	private final static String type = "permission";

	@Inject
	PermissionProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
		super(client, bootstrapService, null, PermissionProvider.type, Permission.class);
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		create(new PermissionImpl.Builder().name(DocumentService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.DELETE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(DocumentService.READ_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(UserService.DELETE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.CREATE_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.EDIT_PERMISSION).build());
		create(new PermissionImpl.Builder().name(RoleService.DELETE_PERMISSION).build());
	}

	@Override
	protected String getMapping() {
		return null;
	}

}
