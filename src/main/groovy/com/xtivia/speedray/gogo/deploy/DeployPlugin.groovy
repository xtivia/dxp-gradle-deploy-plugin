package com.xtivia.speedray.gogo.deploy

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by don on 2/2/2017.
 */
class DeployPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('gogo', DeployExtension, project)
        project.configurations.create('deploy')
        project.tasks.create('deploy', DeployLocalTask.class).dependsOn 'jar'
        project.tasks.create('deployDependencies', DeployRemoteTask.class).dependsOn
    }
}
