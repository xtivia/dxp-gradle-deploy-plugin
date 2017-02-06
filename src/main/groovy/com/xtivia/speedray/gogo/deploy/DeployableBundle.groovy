package com.xtivia.speedray.gogo.deploy

import org.eclipse.aether.artifact.Artifact

/**
 * Created by 91040 on 2/5/2017.
 */
class DeployableBundle {
    String name
    Artifact artifact
    Bundle bundle
    String url

    DeployableBundle(String name) {
        this.name = name
    }
}
