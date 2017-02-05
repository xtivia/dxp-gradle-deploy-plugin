package com.xtivia.speedray.gogo.deploy

import org.gradle.internal.impldep.aQute.bnd.header.Parameters
import org.gradle.jvm.tasks.Jar

import java.util.jar.Attributes
import java.util.jar.Manifest

/**
 * Created by 91040 on 2/5/2017.
 */
class Bundle {
    boolean isFragment = false;
    String fragmentHost = null;
    String bsn = null;
    String hostBSN = null;
    Jar bundle = null

    Bundle(File jarFile) {
        bundle = new Jar(getJarFile())

        try {
            Manifest manifest = bundle.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();

            fragmentHost = mainAttributes.getValue("Fragment-Host");

            isFragment = fragmentHost != null;

            bsn = bundle.getBsn();

            if(isFragment) {
                hostBSN = new Parameters(fragmentHost).keySet().iterator().next();
            }
        } finally {

        }
    }

    private int getState(String state) {
        String bundleState = state.toUpperCase();

        if ("ACTIVE".equals(bundleState)) {
            return org.osgi.framework.Bundle.ACTIVE;
        }
        else if ("INSTALLED".equals(Bundle.INSTALLED)) {
            return org.osgi.framework.Bundle.INSTALLED;
        }
        else if ("RESOLVED".equals(Bundle.RESOLVED)) {
            return org.osgi.framework.Bundle.RESOLVED;
        }
        else if ("STARTING".equals(Bundle.STARTING)) {
            return org.osgi.framework.Bundle.STARTING;
        }
        else if ("STOPPING".equals(Bundle.STOPPING)) {
            return org.osgi.framework.Bundle.STOPPING;
        }
        else if ("UNINSTALLED".equals(Bundle.UNINSTALLED)) {
            return org.osgi.framework.Bundle.UNINSTALLED;
        }

        return 0;
    }

}
