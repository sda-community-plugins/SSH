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
    String fileContent = props.notNull('fileContent')
    String password = props.optional('password')
    String privateKeyFile = props.optional('privateKeyFile')
    String passphrase = props.optional('passphrase')
    Boolean useSudo = props.optionalBoolean('sudo', false)
    // hidden properties
    String tmpDirectoryPath = props.optional('tmpDirectoryPath')
    String postUploadCmd = props.optional('postUploadCmd')
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
    if (!SshUtils.isEmpty(tmpDirectoryPath)) {
        options.scpOptions {
            uploadToDirectory = tmpDirectoryPath
            postUploadCommand = postUploadCmd
        }
    }
    if (useSudo) {
        options.execOptions {
            prefix = sudoCommandPrefix
        }
    }

    options.setVerbose(verbose)
    options.setSshDebug(debug)

    if (verbose) {
        println ">>> Creating remote file ${remoteFileName}..."
    }
    try {
        remoteSession {
            connect()
            remoteFile(remoteFileName).text = fileContent
            disconnect()
        }
    } catch (SshException e) {
        throw new StepFailedException("SshException: ${e.message}")
    }
    System.exit 0

} catch (StepFailedException e) {
    SshUtils.error "${e.message}"
    System.exit 1
}