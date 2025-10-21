# Changelog del Proyecto

## [2025-10-21] Cambios Realizados

### Tests y Correcciones

#### AuthControllerTest - Mejoras y Refactorización
- Convertido a `@WebMvcTest` con importación explícita de `SecurityConfig`
  - Mejora: Test slice más ligero y enfocado en la capa web
  - Corrección: Configuración de seguridad consistente con la aplicación
- Implementados 11 tests completos verificando:
  - Registro de usuarios (success)
  - Login (success, credenciales inválidas, usuario no encontrado)
  - Gestión de API keys (creación, listado, revocación)
  - Casos de seguridad (accesos no autorizados)
- Añadida documentación detallada del test suite
- Validado con pruebas de integración completas

#### Estado de Tests
- Suite completa: 20 tests ejecutados
  - AuthControllerTest: 11 tests ✓
  - TeamsPlayersControllerTest: 1 test ✓
  - ApiKeyServiceTest: 7 tests ✓
  - ApiFutbolApplicationTests: 1 test ✓
- Build: SUCCESS
- Cobertura: Mejorada para AuthController y flujos de autenticación

### Próximos Pasos
- Evaluar actualización de `@MockBean` (deprecated desde Spring Boot 3.4.0)
- Considerar migración a nuevos patrones de mocking recomendados

## [2025-10-21] Cambios Realizados

### Seguridad y Autenticación

#### ApiKey - Modelo y Servicio
- Implementado sistema de ApiKey con fingerprint para búsqueda rápida
  - Agregado campo `keyFingerprint` (SHA-256 Base64URL) para lookup optimizado
  - Soporte para generación de raw key, hash bcrypt y fingerprint
  - Búsqueda en dos pasos: primero por fingerprint (índice), luego verificación bcrypt

#### JWT - Implementación
- Añadido soporte JWT para autenticación
  - Configurado secreto JWT en `application.properties`
  - Implementada generación y validación de tokens
  - Agregados filtros de seguridad para JWT y ApiKey

#### Configuración de Seguridad
- Configurado `SecurityConfig` para modo stateless
- Definida cadena de filtros de autenticación (ApiKey → JWT)
- Endpoints públicos configurados (`/auth/register`, `/auth/login`)
- Soporte para documentación Swagger/OpenAPI

### Tests y Calidad

#### Tests Implementados
- Test MockMvc para `TeamsPlayersController`
  - Simula respuesta del servicio y verifica formato JSON
  - Agregado `@WithMockUser` para satisfacer seguridad en tests
  - Verificación de respuesta 200 OK y estructura de datos

- Tests Unitarios para `ApiKeyService` (7 tests implementados)
  - `createForUser`: verifica generación de key, hash bcrypt y fingerprint
  - `findByRawKey` (fast path): valida búsqueda por fingerprint indexado
  - `findByRawKey` (fallback): prueba búsqueda por bcrypt cuando fingerprint falla
  - `findByRawKey` (not found): verifica comportamiento cuando key no existe
  - `listForUser`: comprueba listado de keys por usuario
  - `revokeByIdForUser`: valida revocación exitosa de key existente
  - `revokeByIdForUser` (no existe): verifica manejo de revocación de key inexistente
  - Cobertura: generación de keys, búsqueda optimizada, casos de error

#### Limpieza de Dependencias
- Eliminadas dependencias duplicadas en `pom.xml`:
  - Removida duplicación de `selenium-java` (mantenida v4.21.0)
  - Removida duplicación de `jsoup` (mantenida v1.17.2)
- Build verificado sin warnings de dependencias

### Estructura del Proyecto

#### Endpoints Implementados
- `POST /auth/register` - Registro de usuarios
- `POST /auth/login` - Login y obtención de JWT
- `POST /auth/apikeys` - Creación de API keys
- `GET /auth/apikeys` - Listado de API keys del usuario
- `DELETE /auth/apikeys/{id}` - Revocación de API key
- `GET /api/players/{teamName}` - Consulta de jugadores por equipo

#### Base de Datos
- Esquema actualizado para soportar fingerprints de ApiKey
- Índice creado en columna `key_fingerprint` para búsqueda rápida

### Documentación
- Agregados JavaDoc y comentarios inline en clases clave:
  - Documentación de servicios y controladores
  - Explicación de filtros de seguridad
  - Notas sobre manejo de tokens y claves

## Próximos Pasos Planificados

### Tests Pendientes
1. ✅ Tests unitarios para `ApiKeyService` (Completado)
   - ✅ Creación de ApiKey (generación y almacenamiento)
   - ✅ Búsqueda por raw key (fast path y fallback)
   - ✅ Revocación y listado de keys

2. Tests para `AuthController`
   - Flujo de registro
   - Flujo de login y obtención de JWT
   - CRUD de API keys

### Mejoras de Calidad Pendientes
1. Resolver advertencias del compilador
   - Imports no utilizados en varios archivos
   - Campos no utilizados en `PlayerStats`
   - APIs deprecadas en `ScraperService`

2. Ajustes de Seguridad
   - Anotaciones `@NonNull` en filtros
   - Gestión segura de secretos JWT

3. Migración de Datos
   - Script para backfill de `key_fingerprint`
   - Creación de índices en datos existentes

## Estado Actual de Tests
- Tests ejecutados: 9 (2 previos + 7 nuevos ApiKeyService)
- Fallos: 0
- Errores: 0
- Build: SUCCESS
- Cobertura: ApiKeyService 100% métodos probados

---
*Nota: Este changelog será actualizado conforme se implementen nuevas características o mejoras.*