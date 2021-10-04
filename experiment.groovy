import groovy.json.JsonSlurper
import static groovy.io.FileType.FILES


@NonCPS
def getFileContent(filename) {
    def file = "${WORKSPACE}/" + filename
    def content = readFile(file)
    return content    
}

@NonCPS
def getTextFiles() {

        def dir = new File("$WORKSPACE")
        def files = [];
        dir.traverse(type: FILES, maxDepth: 0) { 
            if (it.name.endsWith(".txt")) {
                files.add(it) 
            }
            
        };
        
        return files
}


pipeline {
    agent any
    stages {
        stage('Hello') {
            steps {
                echo "Hello World ${BUILD_NUMBER}"
                sh 'rm -f *.txt'
                sh 'touch me.txt'
                sh 'echo "this is a test" >> me.txt'
                sh 'echo "this is still a test" >> me.txt'
                sh 'echo "this is a test to find the correct bob" >> me.txt'
                //sh 'echo ':{"suiteName" : "adminapp", "performanceMetricsUrl" : "https://api.eu-central-1.saucelabs.com/v2/performance/metrics/5dedc8323e0e4d189eb35cf7b14c416d/", "sauceLabsVideo" : "https://app.eu-central-1.saucelabs.com/tests/5dedc8323e0e4d189eb35cf7b14c416d" }' >> me.txt
                sh 'echo /"Fred is not bob/" >> me.txt'

                echo 'THE CONTENTS OF ME.TXT ARE:'
                sh 'cat me.txt'
                
                git 'https://github.com/simozzer/Junkins'
                echo 'git files'
                sh 'ls'
                echo 'ABOUT TO RUN NODE'
                
                // sh '/usr/local/bin/node ./index.js'

            } 
        }
        stage("POST BUILD") {
            steps {
               echo "POST BUILD"
                    script {
                        /*
                    def consoleTextUrl = "http://192.168.0.31:8086/job/Hello%20Pipeline/${BUILD_NUMBER}/consoleText"
                    println(consoleTextUrl)
                   // def response = httpRequest consoleTextUrl
                    //println("Status: "+response.status)
                    //println("Content: "+response.content)
                    sh "wget ${BUILD_URL}consoleText"
                    */
                    
                    def files = getTextFiles()
                    
                    for(item in files){
                        println "FILE:::" + item.name
                        def file = readFile "$WORKSPACE/$item.name"
                        def lines = file.readLines()
                        for(line in lines) {
                            if (line.contains('suiteResultsObject:')) {
                                echo "LINE:" + line
                            }
                        }
                    }

                   
                }
            }
        }
    }
}
