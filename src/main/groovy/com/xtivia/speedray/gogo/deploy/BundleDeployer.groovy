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

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.osgi.framework.dto.BundleDTO

/**
 * Created by 91040 on 2/5/2017.
 */
class BundleDeployer {
    DeployableBundle[] deployableBundles

    BundleDeployer(DeployableBundle[] deployableBundles) {
        this.deployableBundles = deployableBundles
    }

    def deploy(GogoTelnetClient client) {

        List<BundleDTO> bundles = getBundles(client);

        deployableBundles.each {
            if (it.bundle != null) {
                deploy(it, bundles, client)
            }
        }

    }

    def deploy(DeployableBundle deployableBundle, List<BundleDTO> bundles, GogoTelnetClient client) {

        long hostId = getBundleId(bundles, deployableBundle.bundle.hostBSN);

        long existingId = getBundleId(bundles, deployableBundle.bundle.bsn);

        if (existingId > 0) {
            if (deployableBundle.bundle.isFragment && hostId > 0) {
                String response = client.send("update " + existingId + " " + deployableBundle.url);

                log.info(response);

                response = client.send("refresh " + hostId);

                log.info(response);
            } else if(deployableBundle.artifact.isSnapshot()) {
                String response = client.send("uninstall " + existingId);

                log.info(response);

                response = client.send("equinox:install -start " + deployableBundle.url);

                log.info(response);
            } else {
                String response = client.send("stop " + existingId);

                log.info(response);

                response = client.send("update " + existingId + " " + deployableBundle.url);

                log.info(response);

                response = client.send("start " + existingId);

                log.info(response);
            }

            log.info("Updated bundle " + existingId);
        }
        else {
            String response = client.send("install " + deployableBundle.url);

            log.info(response);

            if (deployableBundle.bundle.isFragment && hostId > 0) {
                response = client.send("refresh " + hostId);

                log.info(response);
            }
            else {
                existingId = getBundleId(getBundles(client), deployableBundle.bundle.bsn);

                if(existingId > 1) {
                    response = client.send("start " + existingId);
                    log.info(response);
                }
                else {
                    log.error("Error: fail to install " + deployableBundle.bundle.bsn);
                }
            }
        }

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

    private static final Logger log = Logging.getLogger(BundleDeployer.class)

}
