pipeline {
    agent LINUX_RHEL  // Runs on LINUX_RHEL available agent

    tools {
        maven 'Maven_3.9.9'
        jdk 'JDK_21' 
    }

    environment {
        ARTIFACT_NAME = "my-app.jar"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/java-maven-project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'  // Build the project without running tests
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'  // Run JUnit test cases
            }
        }

        stage('Code Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.projectKey=java-app -Dsonar.host.url=http://your-sonarqube-url:9000 -Dsonar.login=your-sonar-token'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'  // Package the application (e.g., JAR or WAR)
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Dev') {
            steps {
                sh 'scp target/${ARTIFACT_NAME} user@dev-server:/opt/app/'
                sh 'ssh riyaz@192.168.1.2 "systemctl restart my-java-app.service"'
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'  // Deploy to production only from the main branch
            }
            steps {
                input message: "Deploy to Production?"
                sh 'scp target/${ARTIFACT_NAME} user@prod-server:/opt/app/'
                sh 'ssh root@192.168.1.2 "systemctl restart my-java-app.service"'
            }
        }
    }

    post {
        success {
            echo 'Build, Test, and Deployment completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Check the logs.'
        }
    }
}
