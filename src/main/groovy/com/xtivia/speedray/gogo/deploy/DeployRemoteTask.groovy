package com.xtivia.speedray.gogo.deploy

import com.xtivia.speedray.gogo.ssh.GogoBridge
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactResolutionException
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.tasks.TaskAction

import static AetherUtil.*

/**
 * Created by don on 2/2/2017.
 */
class DeployRemoteTask extends DefaultTask {

    public class MavenDependency {
        String dependency
    }

    boolean isSnapshot(MavenDependency dependency) {
        return dependency.dependency.endsWith('-SNAPSHOT')
    }

    String[] installUrls = []

    void installUrlsViaGogo(DeployExtension config) {
        def client = new GogoTelnetClient(config.host, config.port)
        try {
            installUrls.each {
                println("Installing ${it}")
                String response = client.send("equinox:install -start " + it)
                println(response)
            }
        } finally {
            client.close()
        }
    }

    void installUrls() {
        def gogoBridge = new GogoBridge()
        def config = (DeployExtension)project.extensions.gogo
        if(config) {
            if(config.useSsh) {
                if(config.ssh) {
                    gogoBridge.setup(config.ssh)
                    gogoBridge.run {
                        session(gogoBridge.ssh.remotes.remote) {
                            forwardLocalPort port:config.port, hostPort:config.port
                            installUrlsViaGogo(config)
                        }
                    }
                } else {
                    throw new IllegalArgumentException("ssh section is required when useSsh is true")
                }
            } else {
                installUrlsViaGogo(config)
            }
         }
    }

    @TaskAction
    void deploy() {
        getDependencies()
        installUrls()
    }

    void getDependencies() {
        def localRepoDir = new File('build/localrepo')

        if(localRepoDir.exists()) {
            localRepoDir.deleteDir()
        }

        def unresolved = [:]
        def resolved = [:]
        ConsoleTransferListener consoleTransferListener = new ConsoleTransferListener()

        project.extensions.gogo.dependencies.each {
            def dependency = new MavenDependency()
            dependency.dependency = it
            unresolved.put(it, dependency)
        }

        project.repositories.each {
            if(it instanceof DefaultMavenArtifactRepository) {
                DefaultMavenArtifactRepository repository = (DefaultMavenArtifactRepository)it
                RemoteRepository remoteRepo = newRemoteRepository(repository.url.toString(),null,null)
                LocalRepository localRepository = new LocalRepository('build/localrepo')
                unresolved.each {
                    def dependency = (MavenDependency)it.value
                    if(resolved[dependency.dependency] == null) {
                        try {
                            ArtifactResolver artifactResolver = new ArtifactResolver(dependency.dependency, remoteRepo, localRepository, consoleTransferListener)
                            artifactResolver.resolve(true)
                            resolved.put(dependency.dependency, dependency)
                        } catch(ArtifactResolutionException e) {
                            println(e.message);
                        }
                    }
                }
            }
        }

        consoleTransferListener.downloadTransfers.each {
            def url = (it.value.repositoryUrl + it.value.resourceName)
            it.value.file
            installUrls = installUrls + url
        }
    }
}
