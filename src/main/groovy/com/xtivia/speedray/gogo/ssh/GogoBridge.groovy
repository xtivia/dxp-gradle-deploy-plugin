package com.xtivia.speedray.gogo.ssh

import com.xtivia.speedray.gogo.deploy.DeploySshExtension
import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Service

/**
 * Created by don on 2/2/2017.
 */
class GogoBridge {

    public Service ssh

    def setup(DeploySshExtension config) {
        ssh = Ssh.newService();
        ssh.settings {
            knownHosts = allowAnyHosts
        }
        ssh.remotes {
            remote {
                host = config.host
                port = config.port
                user = config.user
                password = config.password
            }
        }
    }

    def run(Closure closure) {
        return ssh.run(closure)
    }
}
