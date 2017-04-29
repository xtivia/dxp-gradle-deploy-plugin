/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.speedray.gogo.deploy

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Created by don on 2/2/2017.
 */
class DeployExtension {
    private Project project

    String host = "localhost"
    Integer port = 11311
    Boolean useSsh = false
    DeploySshExtension ssh
    String[] dependencies = []

    DeployExtension(Project project) {
        this.project = project
    }

    DeploySshExtension ssh(Closure closure) {
        def ssh = project.configure(new DeploySshExtension(), closure)
        this.ssh = ssh
        return ssh
    }
}
