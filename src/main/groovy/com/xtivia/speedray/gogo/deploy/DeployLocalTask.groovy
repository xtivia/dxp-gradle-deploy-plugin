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

import org.eclipse.aether.artifact.DefaultArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


class DeployLocalTask extends DefaultTask {
    @Input
    public File getJarFile() {
        return project.tasks.jar.archivePath
    }

    @TaskAction
    def deploy() {
        log.info("Installing ${project.group}:${project.name}:${project.version}")
        def bundle = new DeployableBundle(project.group+':'+project.name+':'+project.version)
        log.info("Installing ${bundle.name}")
        bundle.artifact = new DefaultArtifact(bundle.name)
        bundle.url = getJarFile().toURI().toASCIIString()
        DeployableBundle[] bundles = [] + bundle
        def deployer = new BundleDeployer(bundles)
        def client = new GogoTelnetClientImpl(_host, _port);
        try {
            deployer.deploy(client);
        } finally {
            client.close()
        }
    }

    private final String _host = 'localhost'
    private final int _port = 11311
    private static final Logger log = Logging.getLogger(DeployLocalTask.class)
}
