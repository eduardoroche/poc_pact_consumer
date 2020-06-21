#!groovy
pipeline {

  agent any

  environment {
      BRANCH_NAME=env.GIT_BRANCH.replace("origin/", "")
  }

  tools {
	maven 'maven'
  }

  properties([
        parameters ([
           string(name: 'pactConsumerTags', defaultValue: '')
         ])
     ])

  stages {
    stage('Build') {
      steps {
	   sh "mvn clean verify"
      }
    }
    stage('Publish Pacts') {
        //-- set prod if want to deploy prod tag
         if(env.pactConsumerTags) {
            sh 'mvn pact:publish -Dpactbroker.url=http://pact_broker:80 -Dpact.consumer.version=${GIT_COMMIT} -Dpact.tag=${env.pactConsumerTags}'
        }
    }
    stage('Check Pact Verifications') {
        if(env.pactConsumerTags) {
            sh 'curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.61.1/pact-1.61.1-linux-x86_64.tar.gz'
            sh 'tar xzf pact-1.61.1-linux-x86_64.tar.gz'
            dir('pact/bin') {
              sh "./pact-broker can-i-deploy --retry-while-unknown=12 --retry-interval=10 -a person-consumer -b http://${PACT_BROKER_URL} -e ${GIT_COMMIT}"
            }
        }
    }
    stage('Deploy') {
      when {
        branch 'master'
      }
      steps {
        echo 'Deploying to prod now...'
      }
    }
    stage('Tag Pact') {
      steps {
          // -t prod -- after the version
        dir('pact/bin') {
       //   sh "./pact-broker create-version-tag -a person-consume -b http://pact_broker -e ${GIT_COMMIT}"
         // sh "./pact-broker create-version-tag -a messaging-app2 -b http://pact_broker -e ${GIT_COMMIT}"
       //   sh "./pact-broker create-version-tag -a messaging-app3 -b http://pact_broker -e ${GIT_COMMIT}"
        //  sh "./pact-broker create-version-tag -a userclient -b http://pact_broker -e ${GIT_COMMIT}"
        }
      }
    }
  }

}