package com.xtivia.speedray.gogo.deploy

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

/**
 * Created by don on 2/2/2017.
 */
class DeployTaskJar extends DeployTask {
    @Input
    public File getJarFile() {
        return project.tasks.jar.archivePath
    }

    @TaskAction
    def deploy() {
        super.deploy(new Jar(getJarFile()))
    }

}
