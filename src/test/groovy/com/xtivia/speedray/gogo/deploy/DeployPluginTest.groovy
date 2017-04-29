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

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
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
            }
        
            dependencies {
            }
            
            gogo {
                useSsh false
                dependencies  'org.slf4j:slf4j-log4j12:1.7.22',
                              'org.slf4j:slf4j-api:1.7.22',
                              'org.jboss.logging:jboss-logging:3.3.0.Final',
                              'org.osgi:org.osgi.service.jdbc:1.0.0',
                              'org.jboss:jandex:2.0.3.Final',
                              'org.hibernate.common:hibernate-commons-annotations:5.0.1.Final',
                              'net.bytebuddy:byte-buddy:1.6.7',
                              'org.javassist:javassist:3.21.0-GA',
                              'com.fasterxml:classmate:1.3.3',
                              'org.apache.servicemix.bundles:org.apache.servicemix.bundles.antlr:2.7.7_5',
                              'javax.security.jacc:javax.security.jacc-api:1.5',
                              'org.eclipse.persistence:javax.persistence:2.1.0',
                              'javax.interceptor:javax.interceptor-api:1.2',
                              'org.apache.servicemix.bundles:org.apache.servicemix.bundles.javax-inject:1_2',
                              'javax.enterprise:cdi-api:1.2',
                              'org.slf4j:slf4j-api:1.7.22',
                              'javax.transaction:javax.transaction-api:1.2',
                              'org.hibernate.hibernate-core:5.2.6.Final',
                              'org.hibernate:hibernate-osgi:5.2.6.Final',
                              'io.swagger:swagger-annotations:1.5.10'
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
