package com.dapp.api_futbol.aop;

import com.dapp.api_futbol.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
public class WebServiceAuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("WebServiceAudit");

    @Autowired
    private UserService userService;

    /**
     * Define un pointcut que captura todos los métodos públicos en cualquier clase
     * anotada con @RestController.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    /**
     * Advice que se ejecuta alrededor de los métodos de los controladores REST.
     * Registra información de auditoría como timestamp, usuario, operación, parámetros y tiempo de ejecución.
     * @param joinPoint Representa el método que está siendo interceptado.
     * @return El resultado del método interceptado.
     * @throws Throwable Si el método interceptado lanza una excepción.
     */
    @Around("restControllerMethods()")
    public Object auditWebServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Instant timestamp = Instant.now();
        String username = "anonymous";
        String operation = joinPoint.getSignature().getName();
        String parameters = getMethodParameters(joinPoint.getArgs());

        try {
            // Intentamos obtener el usuario del contexto de seguridad si está disponible.
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                username = principal.getName();
            }
        } catch (IllegalStateException e) {
            // No hay request o principal disponible (ej. en tests directos o llamadas internas)
            auditLogger.debug("No se pudo obtener el Principal desde RequestContextHolder: {}", e.getMessage());
        }

        Object result = null;
        try {
            result = joinPoint.proceed(); // Ejecuta el método original del controlador.
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            auditLogger.info("AUDIT | Timestamp: {}, User: {}, Operation: {}, Parameters: [{}], ExecutionTime: {}ms",
                             timestamp, username, operation, parameters, executionTime);
        }
        return result;
    }

    /**
     * Helper para formatear los parámetros del método de forma legible.
     * Excluye objetos Principal para evitar loguear credenciales si se exponen.
     * @param args Array de argumentos del método.
     * @return String formateado de los parámetros.
     */
    private String getMethodParameters(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return Arrays.stream(args)
                     .filter(arg -> !(arg instanceof Principal))
                     .map(Object::toString)
                     .collect(Collectors.joining(", "));
    }
}
