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

import org.eclipse.aether.AbstractRepositoryListener
import org.eclipse.aether.RepositoryEvent

/**
 * A simplistic repository listener that logs events to the console.
 */
class ConsoleRepositoryListener
        extends AbstractRepositoryListener
{

    private PrintStream out

    ConsoleRepositoryListener()
    {
        this( null )
    }

    ConsoleRepositoryListener(PrintStream out )
    {
        this.out = ( out != null ) ? out : System.out
    }

    void artifactDeployed(RepositoryEvent event )
    {
        out.println( "Deployed " + event.getArtifact() + " to " + event.getRepository() )
    }

    void artifactDeploying(RepositoryEvent event )
    {
        out.println( "Deploying " + event.getArtifact() + " to " + event.getRepository() )
    }

    void artifactDescriptorInvalid(RepositoryEvent event )
    {
        out.println( "Invalid artifact descriptor for " + event.getArtifact() + ": "
                + event.getException().getMessage() )
    }

    void artifactDescriptorMissing(RepositoryEvent event )
    {
        out.println( "Missing artifact descriptor for " + event.getArtifact() )
    }

    void artifactInstalled(RepositoryEvent event )
    {
        out.println( "Installed " + event.getArtifact() + " to " + event.getFile() )
    }

    void artifactInstalling(RepositoryEvent event )
    {
        out.println( "Installing " + event.getArtifact() + " to " + event.getFile() )
    }

    void artifactResolved(RepositoryEvent event )
    {
        out.println( "Resolved artifact " + event.getArtifact() + " from " + event.getRepository() )
    }

    void artifactDownloading(RepositoryEvent event )
    {
        out.println( "Downloading artifact " + event.getArtifact() + " from " + event.getRepository() )
    }

    void artifactDownloaded(RepositoryEvent event )
    {
        out.println( "Downloaded artifact " + event.getArtifact() + " from " + event.getRepository() )
    }

    void artifactResolving(RepositoryEvent event )
    {
        out.println( "Resolving artifact " + event.getArtifact() )
    }

    void metadataDeployed(RepositoryEvent event )
    {
        out.println( "Deployed " + event.getMetadata() + " to " + event.getRepository() )
    }

    void metadataDeploying(RepositoryEvent event )
    {
        out.println( "Deploying " + event.getMetadata() + " to " + event.getRepository() )
    }

    void metadataInstalled(RepositoryEvent event )
    {
        out.println( "Installed " + event.getMetadata() + " to " + event.getFile() )
    }

    void metadataInstalling(RepositoryEvent event )
    {
        out.println( "Installing " + event.getMetadata() + " to " + event.getFile() )
    }

    void metadataInvalid(RepositoryEvent event )
    {
        out.println( "Invalid metadata " + event.getMetadata() )
    }

    void metadataResolved(RepositoryEvent event )
    {
        out.println( "Resolved metadata " + event.getMetadata() + " from " + event.getRepository() )
    }

    void metadataResolving(RepositoryEvent event )
    {
        out.println( "Resolving metadata " + event.getMetadata() + " from " + event.getRepository() )
    }

}