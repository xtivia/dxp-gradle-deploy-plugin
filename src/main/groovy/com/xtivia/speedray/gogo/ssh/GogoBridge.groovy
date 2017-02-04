package com.xtivia.speedray.gogo.ssh

import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Service

/**
 * Created by don on 2/2/2017.
 */
class GogoBridge {

    public host = 'localhost'
    public port = 22
    public user
    public password

    public Service ssh

    def setup() {
        ssh = Ssh.newService();
        ssh.settings {
            knownHosts = allowAnyHosts
        }
        ssh.remotes {
            remote {
                host = this.host
                port = this.port
                user = this.user
                password = this.password
            }
        }
    }
}
