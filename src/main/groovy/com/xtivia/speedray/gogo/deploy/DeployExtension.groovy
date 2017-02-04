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
