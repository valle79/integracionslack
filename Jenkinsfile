pipeline {
    agent any
    
    environment {
        PROJECT_NAME = 'Jenkins-Slack Integration Demo'
        MAVEN_HOME = tool 'Maven'
        JAVA_HOME = tool 'JDK17'
    }
    
    stages {
        stage('📢 Inicio') {
            steps {
                script {
                    def startTime = new Date().format('dd/MM/yyyy HH:mm:ss')
                    slackSend(
                        channel: '#notificaciones-dev',
                        color: '#439FE0',
                        message: """
🚀 *INICIO DE BUILD*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 *Proyecto:* ${PROJECT_NAME}
🔢 *Build:* #${BUILD_NUMBER}
👤 *Iniciado por:* ${BUILD_USER_ID ?: 'Jenkins'}
🕐 *Hora:* ${startTime}
🌿 *Branch:* ${GIT_BRANCH ?: 'main'}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                        """.stripIndent()
                    )
                }
            }
        }
        
        stage('🧹 Limpieza') {
            steps {
                echo '🧹 Limpiando workspace...'
                bat 'mvn clean'
            }
        }
        
        stage('🔨 Compilación') {
            steps {
                echo '🔨 Compilando el proyecto...'
                bat 'mvn compile'
            }
        }
        
        stage('🧪 Pruebas Unitarias') {
            steps {
                echo '🧪 Ejecutando pruebas unitarias...'
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('📦 Empaquetado') {
            steps {
                echo '📦 Empaquetando aplicación...'
                bat 'mvn package -DskipTests'
            }
        }
        
        stage('📊 Análisis de Calidad') {
            steps {
                echo '📊 Verificando calidad del código...'
                bat 'mvn verify -DskipTests'
            }
        }
    }
    
    post {
        success {
            script {
                def duration = currentBuild.durationString.replace(' and counting', '')
                def endTime = new Date().format('dd/MM/yyyy HH:mm:ss')
                
                slackSend(
                    channel: '#notificaciones-dev',
                    color: 'good',
                    message: """
✅ *BUILD EXITOSO*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 *Proyecto:* ${PROJECT_NAME}
🔢 *Build:* #${BUILD_NUMBER}
⏱️ *Duración:* ${duration}
🕐 *Finalizado:* ${endTime}
🌿 *Branch:* ${GIT_BRANCH ?: 'main'}
🔗 *Detalles:* ${BUILD_URL}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎉 ¡Todos los tests pasaron correctamente!
                    """.stripIndent()
                )
            }
        }
        
        failure {
            script {
                def duration = currentBuild.durationString.replace(' and counting', '')
                def endTime = new Date().format('dd/MM/yyyy HH:mm:ss')
                
                slackSend(
                    channel: '#notificaciones-dev',
                    color: 'danger',
                    message: """
❌ *BUILD FALLIDO*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 *Proyecto:* ${PROJECT_NAME}
🔢 *Build:* #${BUILD_NUMBER}
⏱️ *Duración:* ${duration}
🕐 *Finalizado:* ${endTime}
🌿 *Branch:* ${GIT_BRANCH ?: 'main'}
🔗 *Detalles:* ${BUILD_URL}console
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ Por favor, revisa los logs para más detalles.
                    """.stripIndent()
                )
            }
        }
        
        unstable {
            slackSend(
                channel: '#notificaciones-dev',
                color: 'warning',
                message: """
⚠️ *BUILD INESTABLE*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 *Proyecto:* ${PROJECT_NAME}
🔢 *Build:* #${BUILD_NUMBER}
🔗 *Detalles:* ${BUILD_URL}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ Algunos tests fallaron o hay warnings.
                """.stripIndent()
            )
        }
        
        always {
            echo '🧹 Limpiando recursos temporales...'
            cleanWs()
        }
    }
}
