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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class UserProvider extends ProviderBase<User> implements UserService {

    private final static String type = "user";
    private final HashService hashService;
    private final RoleService roleService;

    @Inject
    UserProvider(Client client, BootstrapService bootstrapService, HashService hashService, RoleService roleService)
            throws ServiceException {
        super(client, bootstrapService, null, UserProvider.type, User.class);
        this.hashService = hashService;
        this.roleService = roleService;
    }

    @Override
    protected void loadInitialData() throws ServiceException {
        User adminUser = UserService.DefaultUsers.ADMINISTRATOR.getUser();
        create(adminUser);
    }

    @Override
    protected String getMapping() {
        return null;
    }

    private String computeBase64Hash(char[] password) {
        String hash = hashService.toBase64(ByteSource.Util.bytes(password).getBytes());
        if (log.isDebugEnabled()) {
            log.debug(String.format("computeBase64Hash - %s", hash));
        }
        return hash;
    }

    @RequiresPermissions(UserPermissions.Constants.USER_CREATE)
    @Override
    public User create(User user) throws ServiceException {
        try {
            if (user.getId() == null) {
                user.setId(generateUniqueId(user));
            }
            if (user.getPassword() != null) {
                String encodedHash = computeBase64Hash(user.getPassword());
                if (log.isTraceEnabled()) {
                    log.trace(String.format("From service - hash: %s for login %s", encodedHash, user.getLogin()));
                }
                user.setHash(encodedHash);
                // user.setPassword(null);

            }

            if (user.getRoles().isEmpty()) {
                Role role = roleService.get(RoleService.DefaultRoles.DEFAULT.getRole().getId());
                if (role != null) {
                    user.addRole(role);
                } else {
                    throw new ServiceException(String.format("Could not find default role %s", RoleService.DefaultRoles.DEFAULT.getRole()
                            .getId()));
                }
            }

            User newUser = super.create(user);
            return newUser;
        } catch (Throwable t) {
            log.error("create failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @RequiresPermissions(UserPermissions.Constants.USER_EDIT)
    @Override
    public User update(User item) throws ServiceException {
        if (item.getPassword() != null) {
            String encodedHash = computeBase64Hash(item.getPassword());
            if (log.isTraceEnabled()) {
                log.trace(String.format("From service - hash: %s for login %s", encodedHash, item.getLogin()));
            }
            item.setHash(encodedHash);
            // item.setPassword(null);
        }
        return super.update(item);
    }

}
