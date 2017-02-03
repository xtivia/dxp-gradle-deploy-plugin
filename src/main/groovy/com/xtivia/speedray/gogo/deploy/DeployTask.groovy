package com.xtivia.speedray.gogo.deploy

import org.gradle.api.DefaultTask
import org.gradle.internal.impldep.aQute.bnd.header.Parameters
import org.gradle.jvm.tasks.Jar
import org.osgi.framework.Bundle
import org.osgi.framework.dto.BundleDTO;

import java.util.jar.Attributes
import java.util.jar.Manifest

class DeployTask extends DefaultTask {
    def deploy(Jar bundle) {
        boolean isFragment = false;
        String fragmentHost = null;
        String bsn = null;
        String hostBSN = null;

        try {
            Manifest manifest = bundle.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();

            fragmentHost = mainAttributes.getValue("Fragment-Host");

            isFragment = fragmentHost != null;

            bsn = bundle.getBsn();

            if(isFragment) {
                hostBSN =
                        new Parameters(fragmentHost).keySet().iterator().next();
            }
        } finally {

        }

        def client = new GogoTelnetClient(_host, _port);

        List<BundleDTO> bundles = getBundles(client);

        long hostId = getBundleId(bundles, hostBSN);

        long existingId = getBundleId(bundles,bsn);

        String bundleURL = getJarFile().toURI().toASCIIString();

        if (existingId > 0) {
            if (isFragment && hostId > 0) {
                String response =
                        client.send("update " + existingId + " " + bundleURL);

                log.info(response);

                response = client.send("refresh " + hostId);

                log.info(response);
            }
            else {
                String response = client.send("stop " + existingId);

                log.info(response);

                response =
                        client.send("update " + existingId + " " + bundleURL);

                log.info(response);

                response = client.send("start " + existingId);

                log.info(response);
            }

            log.info("Updated bundle " + existingId);
        }
        else {
            String response = client.send("install " + bundleURL);

            log.info(response);

            if (isFragment && hostId > 0) {
                response = client.send("refresh " + hostId);

                log.info(response);
            }
            else {
                existingId = getBundleId(getBundles(client),bsn);

                if(existingId > 1) {
                    response = client.send("start " + existingId);
                    log.info(response);
                }
                else {
                    log.error("Error: fail to install "+bsn);
                }
            }
        }

        client.close();
    }

    private long getBundleId(List<BundleDTO> bundles, String bsn)
            throws IOException {
        long existingId = -1;

        if(bundles != null && bundles.size() > 0 ) {
            for (BundleDTO bundle : bundles) {
                if (bundle.symbolicName.equals(bsn)) {
                    existingId = bundle.id;
                    break;
                }
            }
        }

        return existingId;
    }

    private List<BundleDTO> getBundles(GogoTelnetClient client)
            throws IOException {

        List<BundleDTO> bundles = new ArrayList<>();

        String output = client.send("lb -s -u");

        String[] lines = output.split("\\r?\\n");

        for (String line : lines) {
            try {
                String[] fields = line.split("\\|");

                //ID|State|Level|Symbolic name
                BundleDTO bundle = new BundleDTO();

                bundle.id = Long.parseLong(fields[0].trim());
                bundle.state = getState(fields[1].trim());
                bundle.symbolicName = fields[3];

                bundles.add(bundle);
            }
            catch (Exception e) {
            }
        }

        return bundles;
    }

    private int getState(String state) {
        String bundleState = state.toUpperCase();

        if ("ACTIVE".equals(bundleState)) {
            return Bundle.ACTIVE;
        }
        else if ("INSTALLED".equals(Bundle.INSTALLED)) {
            return Bundle.INSTALLED;
        }
        else if ("RESOLVED".equals(Bundle.RESOLVED)) {
            return Bundle.RESOLVED;
        }
        else if ("STARTING".equals(Bundle.STARTING)) {
            return Bundle.STARTING;
        }
        else if ("STOPPING".equals(Bundle.STOPPING)) {
            return Bundle.STOPPING;
        }
        else if ("UNINSTALLED".equals(Bundle.UNINSTALLED)) {
            return Bundle.UNINSTALLED;
        }

        return 0;
    }

    private static final org.gradle.api.logging.Logger log = org.gradle.api.logging.Logging.getLogger(DeployTask.class)
}
