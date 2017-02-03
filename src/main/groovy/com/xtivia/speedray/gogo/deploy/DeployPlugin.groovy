package com.xtivia.speedray.gogo.deploy

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by don on 2/2/2017.
 */
class DeployPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('gogo', DeployExtension)
        project.tasks.create('deploy', DeployTaskMaven.class).dependsOn 'jar'
    }
}
