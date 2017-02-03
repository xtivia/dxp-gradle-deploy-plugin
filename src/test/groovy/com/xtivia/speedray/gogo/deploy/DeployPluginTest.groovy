package com.xtivia.speedray.gogo.deploy

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
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
    def "maven targets from dependencies"() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'com.xtivia.speedray.gogo.deploy'
             
            }
            
            repositories {
                mavenCentral()
            }
        
            dependencies {
                compileOnly 'org.codehaus.groovy:groovy-all:2.4.8'
            }
        """
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('deploy')
            .withPluginClasspath()
            .build()
        then:
        result.task(':deploy').outcome == SUCCESS
        println(result.tasks)
        println(result.output)
    }
}
