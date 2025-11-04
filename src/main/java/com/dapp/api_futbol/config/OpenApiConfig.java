package com.dapp.api_futbol.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"}) // Only enable Swagger UI in dev and test profiles
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String apiDescription = """
            API para gestionar datos de fútbol que permite consultar información sobre equipos, jugadores y partidos.
            
            ## Endpoints Principales
            
            ### Autenticación
            - POST /auth/register - Registro de usuario
            - POST /auth/login - Login de usuario (obtiene JWT)
            - POST /auth/apikey - Genera API Key para acceder a endpoints protegidos
            
            ### Partidos
            - GET /matches/upcoming?team={name} - Próximos partidos de un equipo
            - GET /matches/history?team={name} - Historial de partidos de un equipo
            - GET /matches/prediction/{matchId} - Predicción del resultado de un partido
            
            ### Jugadores
            - GET /players/performance/{playerId} - Rendimiento del jugador (últimos 10 partidos)
            - GET /players/team/{teamName} - Lista de jugadores de un equipo
            
            ### Historial de Consultas
            - GET /history - Historial de consultas realizadas por el usuario
            
            ## Autenticación
            La API utiliza dos mecanismos de autenticación:
            1. JWT (JSON Web Token) para el login de usuarios
            2. API Key para acceder a los endpoints protegidos
            
            ## Rate Limiting
            Se aplican límites de tasa para prevenir el abuso de la API:
            - 100 requests/hora por API Key
            - 1000 requests/día por API Key
            """;

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Usar el token JWT obtenido del endpoint /auth/login")
                )
                .addSecuritySchemes("api-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API Key obtenida del endpoint /auth/apikey")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .addSecurityItem(new SecurityRequirement().addList("api-key"))
            .info(new Info()
                .title("API Fútbol")
                .version("v1.0.0")
                .description(apiDescription)
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("equipo@dapp.com")
                )
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")
                )
            )
            .addServersItem(new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server")
            );
    }
}
