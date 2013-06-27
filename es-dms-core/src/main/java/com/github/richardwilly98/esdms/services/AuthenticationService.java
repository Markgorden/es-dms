package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-core
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


import java.util.Set;

import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface AuthenticationService extends BaseService<SessionImpl> {
	
	public abstract String login(Credential credential)
			throws ServiceException;

	public abstract void logout(String token)
			throws ServiceException;
	
	public abstract void validate(String token)
			throws ServiceException;
	
	public boolean hasRole(String token, String role)
			throws ServiceException;

	public boolean hasPermission(String token, String permission)
			throws ServiceException;

	public abstract SessionImpl get(String id) throws ServiceException;

	public abstract Set<SessionImpl> getItems(String name) throws ServiceException;
	
	public abstract SessionImpl create(SessionImpl item) throws ServiceException;

	public abstract void delete(SessionImpl item) throws ServiceException;

	public abstract SessionImpl update(SessionImpl item) throws ServiceException;

}