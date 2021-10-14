import groovy.json.JsonOutput

pipeline {
    agent any
    stages {
        stage('Update version in package.json') {
            steps {
                git 'https://github.com/simozzer/Junkins'
                script {
                    
                    def packageJSON = readJSON file:  "${env.WORKSPACE}/package.json"
                    def versionFromJSON = packageJSON.version
                    println "Version number from package.json is ${packageJSON.version}"
                    
                    def versionRegEx = ~'([0-9]+).([0-9]+).([0-9]+)'
                    def jsonVersionMatcher = versionFromJSON =~ versionRegEx
                    if (jsonVersionMatcher.find()) {
                        def major = jsonVersionMatcher.group(1) as Integer
                        def minor = jsonVersionMatcher.group(2) as Integer
                        def build = jsonVersionMatcher.group(3) as Integer
                        jsonVersionMatcher = null
                        
                        def nextMinorVersion = "${major}.${minor}.${build+1}"
                        println "Next minor version: ${nextMinorVersion}"
                        
                        packageJSON.version = nextMinorVersion as String
                        
                        writeJSON file: 'package.json', 
                            json: packageJSON,
                            pretty: 4
                            
                        packageJSON = null
                    }
                }
            } 
        }
    }
}
    
