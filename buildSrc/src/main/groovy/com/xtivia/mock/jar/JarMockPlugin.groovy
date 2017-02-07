package com.xtivia.mock.jar

import org.apache.tools.ant.Project
import org.gradle.api.Plugin

class JarMockPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.create('jar', JarMockTask.class)
    }
}