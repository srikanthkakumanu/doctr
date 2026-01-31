pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'srikanthkakumanu/doctr'  // Replace with your DockerHub username
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage("CI: Build") {
            steps {
                echo "Running Doctr Build Automation"
                sh './gradlew build --no-daemon'
            }
            post {
                always {
                    echo "========always========"
                }
                success {
                    echo "========Doctr Build executed successfully========"
                }
                failure {
                    echo "========Doctr Build execution failed========"
                }
            }
        }

        stage("CI: Test") {
            steps {
                echo "Running Tests"
                sh './gradlew test jacocoTestReport --no-daemon'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage("CI: Code Quality") {
            steps {
                echo "Running SonarQube Analysis"
                withSonarQubeEnv('SonarQube') {
                    sh 'sonar-scanner'
                }
            }
        }

        stage("CI: Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("CI: Build Docker Image") {
            steps {
                echo "Building Docker Image"
                sh 'docker build -t $DOCKER_IMAGE:$DOCKER_TAG .'
            }
        }

        stage("CI: Push to DockerHub") {
            steps {
                echo "Pushing to DockerHub"
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$DOCKER_TAG'
                }
            }
        }

        stage("CD: Deploy") {
            when {
                branch 'main'  // Deploy only on main branch
            }
            steps {
                echo "Deploying to Kubernetes"
                sh 'kubectl apply -f k8s/'
            }
        }
    }
}