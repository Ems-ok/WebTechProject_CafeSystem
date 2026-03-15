pipeline {
  agent any

  tools {
    jdk 'JDK17'
    maven 'Maven 3.9.12'
  }

  environment {
    GITHUB_TOKEN = credentials('Github Credentials')
    NGROK_TOKEN = credentials('NGROK_AUTHTOKEN')
  }

  parameters {
    booleanParam(
      name: 'RUN_UI_TESTS',
      defaultValue: false,
      description: 'Run Selenium UI tests'
    )
    booleanParam(
      name: 'RUN_API_TESTS',
      defaultValue: true,
      description: 'Run API (Karate) tests'
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

    stage('Build') {
      steps {
        sh 'mvn clean compile'
      }
    }

stage('Run App and Tunnel') {
      steps {
        script {

            sh 'mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083 &'

            echo "Waiting for Cafe System to start..."
            sleep 20

            sh "ngrok config add-authtoken ${NGROK_TOKEN}"
            sh 'ngrok http 8083 --domain=daily-lenient-kiwi.ngrok-free.app --log=stdout > ngrok.log &'

            sleep 5
            echo "Tunnel and App are both live."
        }
      }
    }

    stage('Unit Tests (JUnit)') {
      when {
        expression { params.RUN_UI_TESTS }
      }
      steps {
        sh 'mvn test'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('UI Tests (Selenium)') {
      when {
        expression { params.RUN_UI_TESTS }
      }

      steps {
        sh 'mvn verify -Pselenium'
      }
      post {
        always {
          archiveArtifacts allowEmptyArchive: true,
            artifacts: 'target/*.jar, target/screenshots/**'
        }
      }
    }

    stage('API Tests (Karate)') {
      when {
        expression { params.RUN_API_TESTS }
      }
      steps {
       sh 'mvn test -Dtest=KarateRunnerTestIT'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/karate-reports/**'
          publishHTML(target: [
            reportDir: 'target/karate-reports',
            reportFiles: 'karate-summary.html',
            reportName: 'Karate Test Report',
            keepAll: true,
            alwaysLinkToLastBuild: true
          ])
        }
      }
    }

    stage('Coverage') {
      steps {
        sh 'mvn jacoco:report'
      }
      post {
        always {
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
          sh 'mvn sonar:sonar -Dsonar.projectKey=Cafe-System'
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
  }

  post {
    always {
      echo "Cleaning up background processes..."
            sh 'pkill ngrok || true'
            sh 'pkill -f spring-boot || true'
            sh 'pkill -f maven || true'
    }
    success {
      emailext(
        subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: "Build success.\nURL: ${env.BUILD_URL}",
        to: "emma.okeeffe.25@gmail.com"
      )
    }

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
