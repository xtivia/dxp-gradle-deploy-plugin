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
                jcenter()
                maven {
                    url "http://stl-dnexus-10/repository/maven-snapshots"
                }
                maven {
                    url "http://stl-dnexus-10/repository/maven-releases"
                }
            }
        
            dependencies {
            }
            
            gogo {
                useSsh true
                ssh {
                    host = 'stl-dlryapp-10'
                    user = '91040'
                    password = 'Caseyd!001'
                }
                dependencies  'io.swagger:swagger-annotations:1.5.9',
                        'com.xtivia.tools:sgdxp:1.0.0',
                        'org.hibernate:hibernate-osgi:5.2.6.Final',
                        'org.hibernate:hibernate-core:5.2.6.Final',
                        'org.javassist:javassist:3.21.0-GA',
                        'com.spire:service-providers:1.0.0-SNAPSHOT',
                        'com.spire:account-services:1.0.0-SNAPSHOT',
                        'com.spire:security-services:1.0.0-SNAPSHOT',
                        'com.spire:billing-services:1.0.0-SNAPSHOT',
                        'com.spire:payment-services:1.0.0-SNAPSHOT',
                        'com.spire:account-ui:1.0.0-SNAPSHOT',
                        'com.spire:payment-ui:1.0.0-SNAPSHOT',
                        'com.spire:billing-ui:1.0.0-SNAPSHOT',
                        'com.spire:security-ui:1.0.0-SNAPSHOT'
            }
            
        """
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('deployDependencies', '--stacktrace')
            .withPluginClasspath()
            .build()
        then:
        result.task(':deployDependencies').outcome == SUCCESS
        println(result.tasks)
        println(result.output)
    }
}
