import com.microfocus.air.plugin.ssh.SshUtils

import static com.aestasit.infrastructure.ssh.DefaultSsh.*
import com.aestasit.infrastructure.ssh.SshException

import com.serena.air.StepFailedException
import com.serena.air.StepPropertiesHelper

import com.urbancode.air.AirPluginTool

def apTool = new AirPluginTool(args[0], args[1])
def props = new StepPropertiesHelper(apTool.stepProperties, true)

try {
    String hostAddress = props.notNull('hostAddress')
    Integer port = props.notNullInt('port')
    String username = props.notNull('username')
    String remoteFileName = props.notNull('remoteFile')
    String propertyName = props.notNull('propertyName')
    String password = props.optional('password')
    String privateKeyFile = props.optional('privateKeyFile')
    String passphrase = props.optional('passphrase')
    Boolean useSudo = props.optionalBoolean('sudo', false)
    // hidden properties
    String sudoCommandPrefix = props.optional('sudoCommandPrefix')
    Boolean verbose = props.optionalBoolean('verbose', false)
    Boolean debug = props.optionalBoolean('debug', false)

    options.setTrustUnknownHosts(true)
    options.setDefaultHost(hostAddress)
    options.setDefaultPort(port)
    options.setDefaultUser(username)
    if (!SshUtils.isEmpty(password)) {
        options.setDefaultPassword(password)
    }
    if (!SshUtils.isEmpty(privateKeyFile)) {
        options.setDefaultKeyFile(new File(privateKeyFile))
    }
    if (!SshUtils.isEmpty(passphrase)) {
        options.setDefaultPassPhrase(passphrase)
    }
    if (useSudo) {
        options.execOptions {
            prefix = sudoCommandPrefix
        }
    }

    options.setVerbose(verbose)
    options.setSshDebug(debug)

    def remoteContent = ""
    try {
        remoteSession {
            connect()
            remoteContent = remoteFile(remoteFileName).text
            disconnect()
        }
    } catch (SshException e) {
        throw new StepFailedException("SshException: ${e.message}")
    }
    if (verbose) {
        println ">>> Setting property ${propertyName} to \"${remoteContent}\"..."
    }
    apTool.setOutputProperty(propertyName, remoteContent)
    apTool.storeOutputProperties()
    System.exit 0

} catch (StepFailedException e) {
    SshUtils.error "${e.message}"
    System.exit 1
}