package com.xtivia.speedray.gogo.deploy

import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.RequestTrace
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.resolution.VersionRangeResult
import org.eclipse.aether.version.Version

import static AetherUtil.*

public class ArtifactResolver {

    private String artifactName

    private RemoteRepository remote

    private LocalRepository local

    final static String OPEN_RANGE = '[0,)'

    private ConsoleTransferListener consoleTransferListener

    ArtifactResolver(String artifactName, RemoteRepository remote, LocalRepository local, ConsoleTransferListener consoleTransferListener1) {
        this.artifactName = artifactName
        this.remote = remote
        this.local = local
        this.consoleTransferListener = consoleTransferListener1
    }

    Artifact resolve(boolean download) {

        // missing version == highest version, default classifier, default extension
        if (artifactName.split(':').length == 2) {
            artifactName = "$artifactName:$OPEN_RANGE"
        }

        // create artifact first to verify artifact coordinates
        Artifact artifact = new DefaultArtifact(artifactName)

        boolean releaseWanted = 'RELEASE' == artifact.getVersion()
        if (['LATEST', 'RELEASE'].contains(artifact.getVersion())) {
            artifact.setVersion(OPEN_RANGE)
        }

        RepositorySystem system = newRepositorySystem()
        RepositorySystemSession session = newRepositorySystemSession(system, local, this.consoleTransferListener)
        List<RemoteRepository> remotes = newRepositories(remote)

        VersionRangeRequest rangeRequest = new VersionRangeRequest()
        rangeRequest.setArtifact(artifact)
        rangeRequest.setRepositories(remotes)

        VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest)


        Version highestVersion
        if (releaseWanted) {
            // a 'release' means anything but SNAPSHOT - a version that is not temporary
            // this may include versions such as -alpha, -beta, etc, which some people may find is not semantically a release
            highestVersion = rangeResult.getVersions().reverse().find { v -> !v.toString().endsWith('-SNAPSHOT') }
        }
        else {
            highestVersion = rangeResult.getHighestVersion()
        }

        ArtifactRequest artifactRequest = new ArtifactRequest()

        Artifact toDownload = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(),
                artifact.getClassifier(), artifact.getExtension(), highestVersion.toString())
        artifactRequest.setArtifact(toDownload)
        artifactRequest.setRepositories(remotes)
        artifactRequest.setTrace(new RequestTrace(artifactName))

        ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest)
        if (download) {

            artifact = artifactResult.getArtifact()

        }

        return artifact
    }
}
