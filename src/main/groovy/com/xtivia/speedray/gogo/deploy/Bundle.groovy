package com.xtivia.speedray.gogo.deploy

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Jar

import java.util.jar.Attributes
import java.util.jar.Manifest;

/**
 * Created by 91040 on 2/5/2017.
 */
class Bundle {
    boolean isFragment = false;
    boolean isSnapshot = false;
    String fragmentHost = null;
    String bsn = null;
    String hostBSN = null;
    Jar bundle = null

    Bundle(File jarFile) {
        bundle = new Jar(jarFile)

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
}
