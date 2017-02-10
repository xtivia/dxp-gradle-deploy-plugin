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

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Requires
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.*

/**
 * Created by don on 2/2/2017.
 */
class DeployPluginTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File settingsFile


    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        settingsFile = testProjectDir.newFile('settings.gradle')
    }

    @Requires({ env.SSH_HOST != null && env.SSH_USER != null && env.SSH_PASSWD != null })
    def "maven targets from dependencies"() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'com.xtivia.speedray.gogo.deploy'
             
            }
            
            repositories {
                mavenCentral()
                jcenter()
                maven {
                    url "http://stl-dnexus-10/repository/maven-snapshots"
                }
                maven {
                    url "http://stl-dnexus-10/repository/maven-releases"
                }
            }
        
            version = '0.0.1-SNAPSHOT'
            dependencies {
            }
            
            gogo {
                useSsh true
                ssh {
                    host = '${System.env.SSH_HOST}'
                    user = '${System.env.SSH_USER}'
                    password = '${System.env.SSH_PASSWD}'
                    identityFile = file('C:/Users/91040/Projects/cc/dxp-gradle-deploy-plugin/build/id_rsa')
                }
                dependencies  'com.spire:billing-ui:'+version            
            }
            
        """
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('deployDependencies', '--stacktrace', '--info')
            .withPluginClasspath()
            .build()
        then:
        result.task(':deployDependencies').outcome == SUCCESS
        println(result.tasks)
        println(result.output)
    }
}
