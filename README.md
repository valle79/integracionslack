# 🚀 Integración Jenkins con Slack - Proyecto Demo

## 📋 Descripción
Proyecto de demostración para la integración de Jenkins con Slack, implementando un pipeline de CI/CD con notificaciones automáticas.

## 🎯 Objetivo
Automatizar el envío de notificaciones a Slack según el estado del pipeline de Jenkins (inicio, éxito, error).

## 🛠️ Tecnologías Utilizadas
- **Java 17**
- **Spring Boot 4.0.6**
- **Maven**
- **Jenkins**
- **Slack API**
- **JUnit 5**

## 📁 Estructura del Proyecto
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

## 🔧 Endpoints Disponibles

### Health Check
```
GET /api/health
```
Respuesta:
```json
{
  "status": "UP",
  "timestamp": "2026-04-28T15:30:00",
  "service": "Slack Integration Demo",
  "version": "1.0.0"
}
```

### Información del Sistema
```
GET /api/info
```

### Gestión de Mensajes
```
GET /api/messages          - Obtener todos los mensajes
POST /api/messages         - Crear un nuevo mensaje
GET /api/messages/{id}     - Obtener mensaje por ID
```

## 🚀 Instalación y Configuración

### 1. Prerrequisitos
- Java 17 o superior
- Maven 3.6+
- Jenkins instalado
- Cuenta de Slack con permisos de administrador

### 2. Clonar el Repositorio
```bash
git clone <tu-repositorio>
cd slack
```

### 3. Compilar el Proyecto
```bash
mvn clean install
```

### 4. Ejecutar Tests
```bash
mvn test
```

### 5. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📱 Configuración de Slack

### Paso 1: Crear Aplicación en Slack
1. Ir a https://api.slack.com/apps
2. Clic en "Create New App"
3. Seleccionar "From scratch"
4. Nombre: `Jenkins Notifier`
5. Seleccionar workspace

### Paso 2: Configurar Permisos
En **OAuth & Permissions**, agregar estos scopes:
- `chat:write`
- `chat:write.public`
- `channels:read`
- `files:write`

### Paso 3: Instalar App
1. Clic en "Install to Workspace"
2. Autorizar la aplicación
3. Copiar el **Bot User OAuth Token** (comienza con `xoxb-`)

### Paso 4: Crear Canal
1. En Slack, crear canal: `#notificaciones-dev`
2. Invitar al bot: `/invite @Jenkins Notifier`

## 🔧 Configuración de Jenkins

### Paso 1: Instalar Plugin
1. Jenkins → Manage Jenkins → Manage Plugins
2. Buscar: **Slack Notification Plugin**
3. Instalar y reiniciar

### Paso 2: Configurar Credenciales
1. Manage Jenkins → Configure System
2. Buscar sección **Slack**
3. Configurar:
   - **Workspace**: nombre de tu workspace
   - **Credential**: Agregar "Secret text" con el token OAuth
   - **Default Channel**: `#notificaciones-dev`
4. Clic en **Test Connection**

### Paso 3: Crear Pipeline
1. New Item → Pipeline
2. Nombre: `Slack-Integration-Demo`
3. En Pipeline → Definition: **Pipeline script from SCM**
4. SCM: Git
5. Repository URL: tu repositorio
6. Script Path: `Jenkinsfile`

## 📊 Pipeline Stages

El Jenkinsfile incluye las siguientes etapas:

1. **📢 Inicio**: Notifica el inicio del build
2. **🧹 Limpieza**: Limpia el workspace
3. **🔨 Compilación**: Compila el código fuente
4. **🧪 Pruebas Unitarias**: Ejecuta los tests
5. **📦 Empaquetado**: Genera el JAR
6. **📊 Análisis de Calidad**: Verifica la calidad del código

## 📸 Notificaciones en Slack

### Notificación de Inicio
```
🚀 INICIO DE BUILD
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #1
👤 Iniciado por: admin
🕐 Hora: 28/04/2026 15:30:00
🌿 Branch: main
```

### Notificación de Éxito
```
✅ BUILD EXITOSO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #1
⏱️ Duración: 2 min 30 seg
🕐 Finalizado: 28/04/2026 15:32:30
🎉 ¡Todos los tests pasaron correctamente!
```

### Notificación de Error
```
❌ BUILD FALLIDO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Proyecto: Jenkins-Slack Integration Demo
🔢 Build: #1
⏱️ Duración: 1 min 15 seg
⚠️ Por favor, revisa los logs para más detalles.
```

## 🧪 Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=HealthControllerTest
mvn test -Dtest=MessageServiceTest

# Con reporte de cobertura
mvn clean test jacoco:report
```

## 📦 Generar JAR

```bash
mvn clean package
```

El JAR se generará en: `target/slack-0.0.1-SNAPSHOT.jar`

## 🎯 Beneficios de la Integración

1. **Visibilidad en Tiempo Real**: El equipo recibe notificaciones inmediatas
2. **Detección Temprana de Errores**: Alertas instantáneas cuando algo falla
3. **Mejora en la Colaboración**: Todos están informados del estado del proyecto
4. **Trazabilidad**: Historial completo de builds en Slack
5. **Automatización**: Reduce la necesidad de revisar Jenkins manualmente

## 📝 Notas Importantes

- Asegúrate de que Jenkins tenga acceso a internet
- El bot debe estar invitado al canal de Slack
- Verifica que el token OAuth sea válido
- Los tests deben pasar antes de hacer commit

## 👥 Autor
**Valle Grande - Integración CI/CD**

## 📄 Licencia
Este proyecto es para fines educativos.

---

**Fecha**: Abril 2026  
**Curso**: Integración Continua y DevOps  
**Actividad**: S09 | M09 - Integración Jenkins con Slack
