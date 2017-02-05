package com.xtivia.speedray.gogo.deploy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.osgi.framework.dto.BundleDTO;


class DeployLocalTask extends DefaultTask {
    @Input
    public File getJarFile() {
        return project.tasks.jar.archivePath
    }

    @TaskAction
    def deploy() {
        Bundle bundle = new Bundle(getJarFile())

        def client = new GogoTelnetClient(_host, _port);

        List<BundleDTO> bundles = getBundles(client);

        long hostId = getBundleId(bundles, bundle.hostBSN);

        long existingId = getBundleId(bundles, bundle.bsn);

        String bundleURL = getJarFile().toURI().toASCIIString();

        if (existingId > 0) {
            if (bundle.isFragment && hostId > 0) {
                String response = client.send("update " + existingId + " " + bundleURL);

                log.info(response);

                response = client.send("refresh " + hostId);

                log.info(response);
            }
            else {
                String response = client.send("stop " + existingId);

                log.info(response);

                response = client.send("update " + existingId + " " + bundleURL);

                log.info(response);

                response = client.send("start " + existingId);

                log.info(response);
            }

            log.info("Updated bundle " + existingId);
        }
        else {
            String response = client.send("install " + bundleURL);

            log.info(response);

            if (bundle.isFragment && hostId > 0) {
                response = client.send("refresh " + hostId);

                log.info(response);
            }
            else {
                existingId = getBundleId(getBundles(client), bundle.bsn);

                if(existingId > 1) {
                    response = client.send("start " + existingId);
                    log.info(response);
                }
                else {
                    log.error("Error: fail to install " + bundle.bsn);
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

    private static final org.gradle.api.logging.Logger log = org.gradle.api.logging.Logging.getLogger(DeployLocalTask.class)
}
