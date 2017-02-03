package com.xtivia.speedray.gogo.deploy

import org.gradle.api.GradleException
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.resolver.MavenResolver
import org.gradle.api.tasks.TaskAction

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.internal.component.external.model.DefaultMavenModuleResolveMetadata
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.internal.component.external.model.FixedComponentArtifacts
import org.gradle.internal.component.model.ComponentResolveMetadata
import org.gradle.internal.component.model.DefaultComponentOverrideMetadata
import org.gradle.internal.component.model.DefaultIvyArtifactName
import org.gradle.internal.component.model.IvyArtifactName
import org.gradle.internal.resolve.result.BuildableComponentArtifactsResolveResult
import org.gradle.internal.resolve.result.BuildableModuleComponentMetaDataResolveResult
import org.gradle.internal.resolve.result.DefaultBuildableComponentArtifactsResolveResult
import org.gradle.internal.resolve.result.DefaultBuildableModuleComponentMetaDataResolveResult


/**
 * Created by don on 2/2/2017.
 */
class DeployTaskMaven extends DeployTask {

    public class MavenDependency {
        DefaultModuleComponentIdentifier moduleComponentIdentifier
        DefaultIvyArtifactName defaultIvyArtifactName
    }

    @TaskAction
    void deploy() {
        if (!project.plugins.hasPlugin('java')) {
            throw new IllegalStateException("Project does not have the java plugin applied.")
        }

        def dependencies = [:]

        project.configurations.compileOnly.resolvedConfiguration.resolvedArtifacts.each {
            def dependency = new MavenDependency()
            dependency.moduleComponentIdentifier = new DefaultModuleComponentIdentifier(it.moduleVersion.id.group, it.moduleVersion.id.name, it.moduleVersion.id.version)
            dependency.defaultIvyArtifactName = new DefaultIvyArtifactName(it.name, it.type, it.extension, it.classifier)
            dependencies.put(dependency.moduleComponentIdentifier.displayName, dependency)
         }

        dependencies.each {
            println(it)
        }

        project.repositories.each {
            if(it instanceof DefaultMavenArtifactRepository) {
                DefaultMavenArtifactRepository repository = (DefaultMavenArtifactRepository)it
                MavenResolver resolver = repository.createRealResolver()
                println(repository.pomParser.class)
                if(resolver.isLocal()) {

                } else {
                   println(resolver.root)
                   def pattern = resolver.artifactPatterns.toString()
                   dependencies.each {
                       def dependency = (MavenDependency)it.value
                       def result = new DefaultBuildableModuleComponentMetaDataResolveResult()
                       def overrides = new DefaultComponentOverrideMetadata()
                       resolver.remoteAccess.resolveComponentMetaData(dependency.moduleComponentIdentifier, overrides, result)
                       if (!result.failure) {
                           def metadata = (DefaultMavenModuleResolveMetadata)result.metaData
                           def url = pattern.replace('[organisation]', metadata.componentId.group.replace('.','/'))
                            .replace('[module]', metadata.componentId.module)
                            .replace('[revision]', metadata.componentId.version)
                            .replace('[artifact]', dependency.defaultIvyArtifactName.name)
                            .replace('[revision]', metadata.componentId.version)
                            .replace('(-[classifier])','')
                            .replace('[ext]',dependency.defaultIvyArtifactName.extension)
                           println(url)
                       }
                   }
                }
                println(it)
            }
        }
    }
}
