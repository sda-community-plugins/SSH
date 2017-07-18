import static com.aestasit.infrastructure.ssh.DefaultSsh.*

options.setTrustUnknownHosts(true)
options.execOptions {
    prefix = 'sudo'
}

options.scpOptions {
    uploadToDirectory = '/tmp'
    postUploadCommand = 'cp -R %from%/* %to% && sudo rm -rf %from%'
}

String CR = String.valueOf(Character.toChars(0x0D))
String LF = String.valueOf(Character.toChars(0x0A))
String allCmds = "ls -la /usr/bin/*.groovy\n" +
        "cat /usr/bin/sudoUpload.groovy"
String[] cmds = [
    'ls -la /usr/bin/*.groovy',
    'cat /usr/bin/sudoUpload.groovy'
]
toTrimmedList(allCmds, "\n").eachWithIndex { item, index ->
    println item
    println index
}
println toTrimmedList(allCmds, "\n")

String remoteFileName = "/opt/upload/test2.sh"
String fileContent = "This is the content"
remoteSession {

    user = 'ec2-user'
    keyFile = new File('C:\\Users\\klee\\Downloads\\amazon-linux-openssh.key')
    host = 'ec2-54-229-111-26.eu-west-1.compute.amazonaws.com'
    verbose = true
    connect()
    remoteFile(remoteFileName).text = fileContent
    println "====================================================="
    scp {
        from { localFile "test1.txt" }
        into { remoteFile '/tmp/test.txt' }
    }
    prefix("sudo") {
        scp {
            into { remoteDir("/usr/bin") }
            from { localFile('./sudoUpload.groovy') }
        }
    }
    println "====================================================="
    scp {

        into { remoteDir("/tmp") }
        from { localDir('upload') }
    }
    println "====================================================="
    exec 'ls -la /usr/bin/*.groovy'
    println "====================================================="
    exec 'cat /usr/bin/sudoUpload.groovy'

    toTrimmedList(allCmds, "\n").eachWithIndex { item, index ->
        exec item
    }
    println "====================================================="
    remoteFile('/usr/bin/sudoUpload.groovy').text = 'It works!!!'
    println "====================================================="
    exec 'cat /usr/bin/sudoUpload.groovy'
    println "====================================================="
    remoteFile('/etc/yum.repos.d/puppet.repo').text = """
      [puppet]
      name=Puppet Labs Packages
      baseurl=http://yum.puppetlabs.com/el/6x/products/x86_64/
      enabled=1
      gpgcheck=0

      [puppet-deps]
      name=Puppet Dependencies
      baseurl=http://yum.puppetlabs.com/el/6x/dependencies/x86_64/
      enabled=1
      gpgcheck=0
    """
    println "====================================================="
    exec 'yum install nano'
    println "====================================================="
    def hostsFileContent = remoteFile("/etc/hosts").text
    def currentHosts = hostsFileContent.readLines()
    remoteFile("/etc/hosts").text = currentHosts.join(LF)
    println "====================================================="
}


/**
 *  @param list The list to trim whitespaces and remove null entries
 *  @param delimiter The string that separates each entry
 */
public static def toTrimmedList(def list, String delimiter) {
    return list.split(delimiter).findAll{ it?.trim() }.collect{ it.trim() }
}

