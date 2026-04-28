# 📄 DOCUMENTO TÉCNICO - ENTREGABLE
## Integración de Jenkins con Slack

---

## PORTADA

**Universidad/Institución**: Valle Grande  
**Curso**: Integración Continua y DevOps  
**Actividad**: S09 | M09 - Integración de Jenkins con Slack  
**Estudiante**: [TU NOMBRE COMPLETO]  
**Código**: [TU CÓDIGO]  
**Profesor**: Valery Giselle Chumpitaz Caycho  
**Fecha**: 28 de Abril de 2026  

---

## 1. OBJETIVO

Implementar un pipeline de integración continua utilizando Jenkins e integrarlo con Slack para automatizar el envío de notificaciones según el estado del proceso de build (inicio, éxito o error), con el fin de mejorar la comunicación y colaboración dentro de un equipo de desarrollo.

---

## 2. DESCRIPCIÓN DEL PROCESO REALIZADO

### 2.1 Configuración de Slack

#### Paso 1: Creación de la Aplicación
1. Accedí a https://api.slack.com/apps
2. Creé una nueva aplicación llamada "Jenkins Notifier"
3. Seleccioné mi workspace de desarrollo

**[INSERTAR CAPTURA: Pantalla de creación de app en Slack]**

#### Paso 2: Configuración de Permisos OAuth
Configuré los siguientes scopes para el bot:
- `chat:write` - Para enviar mensajes
- `chat:write.public` - Para publicar en canales públicos
- `channels:read` - Para leer información de canales
- `files:write` - Para subir archivos (opcional)

**[INSERTAR CAPTURA: OAuth & Permissions con scopes configurados]**

#### Paso 3: Instalación y Obtención del Token
1. Instalé la aplicación en mi workspace
2. Obtuve el Bot User OAuth Token (xoxb-...)
3. Guardé el token de forma segura para usarlo en Jenkins

**[INSERTAR CAPTURA: Token generado (ocultar parte del token por seguridad)]**

#### Paso 4: Creación del Canal
1. Creé el canal `#notificaciones-dev` en Slack
2. Invité al bot usando el comando `/invite @Jenkins Notifier`

**[INSERTAR CAPTURA: Canal creado con bot invitado]**

---

### 2.2 Configuración de Jenkins

#### Paso 1: Instalación del Plugin
1. Navegué a Manage Jenkins → Manage Plugins
2. Busqué "Slack Notification Plugin"
3. Instalé el plugin y reinicié Jenkins

**[INSERTAR CAPTURA: Plugin de Slack instalado]**

#### Paso 2: Configuración de Credenciales
1. Fui a Manage Jenkins → Configure System
2. Localicé la sección "Slack"
3. Configuré:
   - Workspace: [nombre de mi workspace]
   - Credential: Agregué el token como "Secret text"
   - Default Channel: #notificaciones-dev
   - Marqué "Custom slack app bot user"

**[INSERTAR CAPTURA: Configuración de Slack en Jenkins]**

#### Paso 3: Prueba de Conexión
Realicé un test de conexión que resultó exitoso, confirmando la comunicación entre Jenkins y Slack.

**[INSERTAR CAPTURA: Test de conexión exitoso]**

#### Paso 4: Configuración de Herramientas
Configuré Maven y JDK 17 en Global Tool Configuration para el pipeline.

**[INSERTAR CAPTURA: Configuración de Maven y JDK]**

---

### 2.3 Desarrollo del Proyecto

#### Estructura del Proyecto
Desarrollé una aplicación Spring Boot con la siguiente estructura:

```
slack/
├── src/
│   ├── main/
│   │   ├── java/pe/edu/vallegrande/slack/
│   │   │   ├── controller/
│   │   │   │   ├── HealthController.java
│   │   │   │   └── MessageController.java
│   │   │   ├── model/
│   │   │   │   └── Message.java
│   │   │   ├── service/
│   │   │   │   └── MessageService.java
│   │   │   └── SlackApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/pe/edu/vallegrande/slack/
│           ├── controller/
│           │   └── HealthControllerTest.java
│           └── service/
│               └── MessageServiceTest.java
├── Jenkinsfile
├── pom.xml
└── README.md
```

**[INSERTAR CAPTURA: Estructura del proyecto en el IDE]**

#### Componentes Principales

**HealthController**: Endpoint para verificar el estado de la aplicación
```java
@GetMapping("/api/health")
public ResponseEntity<Map<String, Object>> health() {
    // Retorna estado UP con timestamp
}
```

**MessageService**: Servicio para gestionar mensajes
```java
public List<Message> getAllMessages() {
    // Retorna lista de mensajes
}
```

**Tests Unitarios**: Cobertura de servicios y controladores
- MessageServiceTest: 4 tests
- HealthControllerTest: 2 tests

**[INSERTAR CAPTURA: Código de los controladores principales]**

---

### 2.4 Implementación del Pipeline

#### Creación del Pipeline en Jenkins
1. Creé un nuevo item tipo "Pipeline" llamado "Slack-Integration-Demo"
2. Configuré el SCM para obtener el código del repositorio Git
3. Especifiqué el Jenkinsfile como script del pipeline

**[INSERTAR CAPTURA: Configuración del pipeline en Jenkins]**

---

## 3. JENKINSFILE IMPLEMENTADO

```groovy
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
                sh 'mvn clean'
            }
        }
        
        stage('🔨 Compilación') {
            steps {
                echo '🔨 Compilando el proyecto...'
                sh 'mvn compile'
            }
        }
        
        stage('🧪 Pruebas Unitarias') {
            steps {
                echo '🧪 Ejecutando pruebas unitarias...'
                sh 'mvn test'
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
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('📊 Análisis de Calidad') {
            steps {
                echo '📊 Verificando calidad del código...'
                sh 'mvn verify -DskipTests'
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
```

### Explicación del Pipeline

**Etapas del Pipeline:**

1. **📢 Inicio**: Envía notificación a Slack indicando que el build ha comenzado
2. **🧹 Limpieza**: Ejecuta `mvn clean` para limpiar compilaciones anteriores
3. **🔨 Compilación**: Compila el código fuente con `mvn compile`
4. **🧪 Pruebas Unitarias**: Ejecuta los tests con `mvn test`
5. **📦 Empaquetado**: Genera el JAR con `mvn package`
6. **📊 Análisis de Calidad**: Verifica la calidad del código con `mvn verify`

**Notificaciones Configuradas:**

- **success**: Se envía cuando todas las etapas se completan exitosamente
- **failure**: Se envía cuando alguna etapa falla
- **unstable**: Se envía cuando hay tests fallidos pero el build continúa
- **always**: Limpia el workspace después de cada ejecución

---

## 4. EVIDENCIAS

### 4.1 Configuración en Slack

**[INSERTAR CAPTURA 1: Dashboard de Slack API con la app creada]**

**[INSERTAR CAPTURA 2: OAuth & Permissions mostrando los scopes configurados]**

**[INSERTAR CAPTURA 3: Canal #notificaciones-dev con el bot como miembro]**

---

### 4.2 Configuración en Jenkins

**[INSERTAR CAPTURA 4: Lista de plugins mostrando Slack Notification instalado]**

**[INSERTAR CAPTURA 5: Configure System con la configuración de Slack completa]**

**[INSERTAR CAPTURA 6: Credenciales configuradas en Jenkins]**

**[INSERTAR CAPTURA 7: Pipeline "Slack-Integration-Demo" creado]**

---

### 4.3 Ejecución Exitosa

**[INSERTAR CAPTURA 8: Stage View del pipeline mostrando todas las etapas en verde]**

**[INSERTAR CAPTURA 9: Console Output del build exitoso]**

**[INSERTAR CAPTURA 10: Notificación de inicio en Slack]**

**[INSERTAR CAPTURA 11: Notificación de éxito en Slack con detalles del build]**

**[INSERTAR CAPTURA 12: Resultados de tests en Jenkins]**

---

### 4.4 Ejecución con Error

Para probar la notificación de error, modifiqué temporalmente un test:

```java
@Test
void testGetAllMessages() {
    List<Message> messages = messageService.getAllMessages();
    assertEquals(999, messages.size()); // ❌ Forzar fallo
}
```

**[INSERTAR CAPTURA 13: Stage View mostrando etapa fallida en rojo]**

**[INSERTAR CAPTURA 14: Console Output mostrando el error del test]**

**[INSERTAR CAPTURA 15: Notificación de error en Slack con detalles del fallo]**

---

### 4.5 Notificaciones Recibidas en Slack

**Ejemplo de Notificación de Inicio:**
```
🚀 INICIO DE BUILD
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #5
👤 Iniciado por: admin
🕐 Hora: 28/04/2026 15:30:00
🌿 Branch: main
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**[INSERTAR CAPTURA 16: Captura real de la notificación en Slack]**

**Ejemplo de Notificación de Éxito:**
```
✅ BUILD EXITOSO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #5
⏱️ Duración: 2 min 15 seg
🕐 Finalizado: 28/04/2026 15:32:15
🌿 Branch: main
🔗 Detalles: http://localhost:8080/job/Slack-Integration-Demo/5/
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎉 ¡Todos los tests pasaron correctamente!
```

**[INSERTAR CAPTURA 17: Captura real de la notificación en Slack]**

**Ejemplo de Notificación de Error:**
```
❌ BUILD FALLIDO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #6
⏱️ Duración: 1 min 30 seg
🕐 Finalizado: 28/04/2026 15:35:00
🌿 Branch: main
🔗 Detalles: http://localhost:8080/job/Slack-Integration-Demo/6/console
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ Por favor, revisa los logs para más detalles.
```

**[INSERTAR CAPTURA 18: Captura real de la notificación en Slack]**

---

## 5. PERSONALIZACIÓN DE MENSAJES

Los mensajes fueron personalizados para incluir:

✅ **Información del Proyecto:**
- Nombre del proyecto
- Número de build
- Usuario que inició el build

✅ **Información Temporal:**
- Fecha y hora de inicio
- Fecha y hora de finalización
- Duración total del build

✅ **Información Técnica:**
- Branch de Git
- Estado del proceso (éxito/error)
- Enlace directo a los detalles del build

✅ **Formato Visual:**
- Emojis para mejor identificación visual
- Colores según el estado (azul=inicio, verde=éxito, rojo=error)
- Separadores para mejor legibilidad
- Mensajes motivacionales según el resultado

---

## 6. CONCLUSIONES

### 6.1 ¿Qué aprendí?

1. **Integración de Herramientas DevOps:**
   - Aprendí a integrar Jenkins con Slack utilizando la API de Slack y el plugin de Jenkins
   - Comprendí cómo configurar webhooks y tokens OAuth para comunicación entre servicios

2. **Automatización de Notificaciones:**
   - Implementé un sistema de notificaciones automáticas que informa al equipo en tiempo real
   - Configuré diferentes tipos de notificaciones según el estado del pipeline

3. **Pipelines de CI/CD:**
   - Desarrollé un pipeline completo con múltiples etapas (build, test, package)
   - Aprendí a usar Jenkinsfile para definir pipelines como código

4. **Gestión de Credenciales:**
   - Implementé buenas prácticas de seguridad usando Jenkins Credentials
   - Aprendí a manejar tokens y secretos de forma segura

5. **Testing y Calidad:**
   - Integré pruebas unitarias en el pipeline
   - Comprendí la importancia de la verificación automática de calidad

### 6.2 ¿Qué beneficios tiene esta integración?

1. **Comunicación Mejorada:**
   - El equipo recibe notificaciones instantáneas sin necesidad de revisar Jenkins constantemente
   - Todos los miembros están informados del estado del proyecto en tiempo real

2. **Detección Temprana de Errores:**
   - Los fallos se notifican inmediatamente, permitiendo una respuesta rápida
   - Se reduce el tiempo entre la introducción de un error y su detección

3. **Aumento de Productividad:**
   - Los desarrolladores no necesitan monitorear manualmente el estado de los builds
   - Se pueden enfocar en otras tareas mientras el pipeline se ejecuta

4. **Trazabilidad y Transparencia:**
   - Historial completo de builds disponible en Slack
   - Fácil acceso a información de builds anteriores

5. **Colaboración del Equipo:**
   - Todos los miembros del equipo tienen visibilidad del proceso de CI/CD
   - Facilita la coordinación y el trabajo en equipo

6. **Cultura DevOps:**
   - Promueve la automatización y la integración continua
   - Fomenta la responsabilidad compartida sobre la calidad del código

7. **Escalabilidad:**
   - Fácil de extender a múltiples proyectos y equipos
   - Se puede personalizar para diferentes necesidades

### 6.3 Aplicaciones Prácticas

Esta integración es especialmente útil en:
- Equipos distribuidos geográficamente
- Proyectos con múltiples desarrolladores
- Entornos de desarrollo ágil
- Proyectos con despliegues frecuentes
- Equipos que ya usan Slack como herramienta principal de comunicación

### 6.4 Mejoras Futuras

Posibles extensiones de esta integración:
- Agregar notificaciones para despliegues en producción
- Integrar métricas de cobertura de código
- Incluir análisis de seguridad (SAST/DAST)
- Agregar botones interactivos en Slack para aprobar despliegues
- Integrar con otras herramientas (Jira, GitHub, SonarQube)

---

## 7. REFERENCIAS

1. **Slack API Documentation**
   - https://api.slack.com/docs

2. **Jenkins Slack Plugin**
   - https://plugins.jenkins.io/slack/

3. **Jenkins Pipeline Documentation**
   - https://www.jenkins.io/doc/book/pipeline/

4. **Spring Boot Documentation**
   - https://spring.io/projects/spring-boot

5. **Maven Documentation**
   - https://maven.apache.org/guides/

---

## ANEXOS

### Anexo A: Comandos Útiles

```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Generar JAR
mvn package

# Ejecutar aplicación
mvn spring-boot:run

# Ver logs de Jenkins
tail -f /var/log/jenkins/jenkins.log
```

### Anexo B: Estructura de Archivos Clave

- `Jenkinsfile`: Define el pipeline de CI/CD
- `pom.xml`: Configuración de Maven y dependencias
- `application.properties`: Configuración de Spring Boot
- `README.md`: Documentación del proyecto

### Anexo C: Endpoints de la Aplicación

- `GET /api/health` - Estado de la aplicación
- `GET /api/info` - Información del sistema
- `GET /api/messages` - Listar mensajes
- `POST /api/messages` - Crear mensaje
- `GET /api/messages/{id}` - Obtener mensaje por ID

---

**Firma del Estudiante:**

_________________________  
[TU NOMBRE]

**Fecha de Entrega:** 28 de Abril de 2026

---

## CHECKLIST DE ENTREGA

Antes de entregar, verificar que el documento incluya:

- [x] Portada con datos completos
- [x] Objetivo claramente definido
- [x] Descripción detallada del proceso
- [x] Jenkinsfile completo y comentado
- [x] Mínimo 18 capturas de pantalla
- [x] Evidencias de configuración en Slack
- [x] Evidencias de configuración en Jenkins
- [x] Evidencias de ejecución exitosa
- [x] Evidencias de ejecución con error
- [x] Notificaciones recibidas en Slack
- [x] Conclusiones completas
- [x] Referencias bibliográficas
- [x] Formato PDF o Google Docs
- [x] Ortografía y redacción revisadas

---

**FIN DEL DOCUMENTO**
