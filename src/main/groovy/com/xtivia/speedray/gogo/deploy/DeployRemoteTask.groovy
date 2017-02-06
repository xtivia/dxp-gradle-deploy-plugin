package com.xtivia.speedray.gogo.deploy

import com.xtivia.speedray.gogo.ssh.GogoBridge
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactResolutionException
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import static AetherUtil.*

/**
 * Created by don on 2/2/2017.
 */
class DeployRemoteTask extends DefaultTask {

    DeployableBundle[] deployableBundles = []

    void installUrlsViaGogo(DeployExtension config) {
        def deployer = new BundleDeployer(deployableBundles)
        def client = new GogoTelnetClient(config.host, config.port)
        try {
            deployer.deploy(client);
        } finally {
            client.close()
        }
    }

    void installBundles() {
        def gogoBridge = new GogoBridge()
        def config = (DeployExtension)project.extensions.gogo
        if(config) {
            if(config.useSsh) {
                if(config.ssh) {
                    println("${config.ssh.user}, ${config.ssh.password}, ${config.ssh.host}, ${config.ssh.port}")
                    gogoBridge.setup(config.ssh)
                    gogoBridge.ssh.run {
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
        installBundles()
    }

    void getDependencies() {
        def localRepoDir = new File('build/localrepo')

        if(localRepoDir.exists()) {
            localRepoDir.deleteDir()
        }

        ConsoleTransferListener consoleTransferListener = new ConsoleTransferListener()

        project.extensions.gogo.dependencies.each {
            deployableBundles = deployableBundles + new DeployableBundle(it)
        }

        project.repositories.each {
            if(it instanceof DefaultMavenArtifactRepository) {
                DefaultMavenArtifactRepository repository = (DefaultMavenArtifactRepository)it
                RemoteRepository remoteRepo = newRemoteRepository(repository.url.toString(),null,null)
                LocalRepository localRepository = new LocalRepository('build/localrepo')
                deployableBundles.each {
                    def dependency = (DeployableBundle)it
                    if(dependency.artifact == null) {
                        try {
                            ArtifactResolver artifactResolver = new ArtifactResolver(dependency.name, remoteRepo, localRepository, consoleTransferListener)
                            dependency.artifact = artifactResolver.resolve(true)
                        } catch(ArtifactResolutionException e) {
                            // log.debug(e.message);
                        }
                    }
                }
            }
        }

        deployableBundles.each {
            def transferred = consoleTransferListener.downloadTransfers[it.name]
            if (transferred != null) {
                it.url = (transferred.repositoryUrl + transferred.resourceName)
                it.bundle = new Bundle(transferred.file)
            }
        }
    }

    private static final Logger log = Logging.getLogger(DeployRemoteTask.class)
}
