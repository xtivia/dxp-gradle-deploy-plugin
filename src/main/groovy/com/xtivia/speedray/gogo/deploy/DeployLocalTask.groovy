package com.xtivia.speedray.gogo.deploy

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
        def bundles = [new DeployableBundle(new Bundle(getJarFile()), getJarFile().toURI().toASCIIString())]
        def deployer = new BundleDeployer(bundles)
        def client = new GogoTelnetClient(_host, _port);
        try {
            deployer.deploy(client);
        } finally {
            client.close()
        }
    }

    private static final Logger log = Logging.getLogger(DeployLocalTask.class)
}
