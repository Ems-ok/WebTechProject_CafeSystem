pipeline {
  agent any

  tools {
    jdk 'JDK17'
    maven 'Maven 3.9.12'
  }

  environment {
    GITHUB_TOKEN = credentials('Github Credentials')
  }

  parameters {
    booleanParam(
      name: 'RUN_UI_TESTS',
      defaultValue: false,
      description: 'Run Selenium UI tests'
    )
  }

  stages {
    stage('Secure Step') {
      steps {
        sh '''
          if [ -z "$GITHUB_TOKEN" ]; then
            echo "TOKEN EMPTY"
            exit 1
          else
            echo "TOKEN SET"
          fi
        '''
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build, Test & Coverage') {
      steps {
        sh 'java -version'
        sh 'mvn -v'
        sh 'mvn -B clean verify'
      }
      post {
        always {
          junit allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'

          publishHTML(target: [
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Code Coverage',
            keepAll: true,
            alwaysLinkToLastBuild: true
          ])
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('LocalSonar') {
          sh 'mvn -B sonar:sonar -Dsonar.projectKey=Cafe-System'
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('UI Tests (Selenium)') {
      when {
        expression { params.RUN_UI_TESTS }
      }
      steps {
        sh 'mvn -B verify -DskipUnitTests=true'
      }
      post {
        always {
          archiveArtifacts allowEmptyArchive: true,
                artifacts: 'target/*.jar, target/screenshots/**'
        }
      }
    }
  }
  post {
    failure {
      emailext(
        subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: "Build failed.\nURL: ${env.BUILD_URL}",
        to: "emma.okeeffe.25@gmail.com"
      )
    }
    unstable {
      emailext(
        subject: "UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: "Build unstable (tests failing).\nURL: ${env.BUILD_URL}",
        to: "emma.okeeffe.25@gmail.com"
      )
    }
  }
}