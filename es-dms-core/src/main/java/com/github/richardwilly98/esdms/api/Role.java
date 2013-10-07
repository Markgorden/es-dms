package com.github.richardwilly98.esdms.api;

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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.RoleImpl;

@JsonDeserialize(as = RoleImpl.class)
public interface Role extends ItemBase {

    public enum RoleType {
        SYSTEM(Constants.SYSTEM), USER_DEFINED(Constants.USER_DEFINED), PROCESS(Constants.PROCESS);

        private String type;

        RoleType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static class Constants {
            // system role type cannot be updated or deleted
            public static final String SYSTEM = "system";
            // user-defined role type can be used in es-dms for permissions
            public static final String USER_DEFINED = "user-defined";
            // process role type are only used for process integration
            public static final String PROCESS = "process";
        }

        @JsonValue
        public static RoleType fromValue(String value) {
            switch (value) {
            case Constants.SYSTEM:
                return RoleType.SYSTEM;
            case Constants.USER_DEFINED:
                return RoleType.USER_DEFINED;
            case Constants.PROCESS:
                return RoleType.PROCESS;
            }
            throw new IllegalArgumentException("Invalid role type: " + value);
        }
    }

    @NotNull(message = "type is required")
    public abstract RoleType getType();

    public abstract void setType(RoleType type);

    // methods on scope
    public abstract Set<String> getScopes();

    public abstract void setScopes(Set<String> scopes);

    public abstract void addScope(String scope);

    public abstract void removeScope(String scope);

    // methods on permissions
    public abstract Set<Permission> getPermissions();

    public abstract void setPermissions(Set<Permission> permissions);

    public abstract void addPermission(Permission permission);

    public abstract void removePermission(Permission permission);

}