package com.xtivia.mock.jar

import org.gradle.api.tasks.TaskAction

/**
 * Created by don on 2/7/2017.
 */
class JarMockTask {

    File archivePath = File('build/libs/dxp-gradle-deployment-plugin-1.0.3.jar')

    @TaskAction
    void jar() {

    }
}
