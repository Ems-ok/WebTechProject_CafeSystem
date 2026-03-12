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

    stage('Unit Tests (JUnit)') {
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
        sh 'mvn test -Dtest=TestRunner'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/karate-reports/**'
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