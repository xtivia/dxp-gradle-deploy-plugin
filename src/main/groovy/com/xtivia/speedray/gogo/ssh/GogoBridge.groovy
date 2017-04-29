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
package com.xtivia.speedray.gogo.ssh

import com.xtivia.speedray.gogo.deploy.DeploySshExtension
import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.core.Service

/**
 * Created by don on 2/2/2017.
 */
class GogoBridge {

    public Service ssh

    def setup(DeploySshExtension config) {
        ssh = Ssh.newService();
        ssh.settings {
            knownHosts = allowAnyHosts
        }
        ssh.remotes {
            remote {
                host = config.host
                port = config.port
                user = config.user
                password = config.password
            }
        }
    }
}
