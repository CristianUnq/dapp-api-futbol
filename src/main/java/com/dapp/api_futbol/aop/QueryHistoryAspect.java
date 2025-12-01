package com.dapp.api_futbol.aop;

import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Arrays;

@Aspect
@Component
public class QueryHistoryAspect {

    private static final Logger logger = LoggerFactory.getLogger(QueryHistoryAspect.class);

    @Autowired
    private QueryHistoryService queryHistoryService;

    @Autowired
    private UserService userService;

    /**
     * Define un pointcut que captura todos los métodos públicos en cualquier clase
     * dentro del paquete com.dapp.api_futbol.controller.
     */
    @Pointcut("execution(public * com.dapp.api_futbol.controller..*(..))")
    public void controllerMethods() {}

    /**
     * Advice que se ejecuta después de que un método del controlador finaliza con éxito.
     * Extrae el usuario, el tipo de consulta (nombre del método) y los parámetros para registrarlos.
     * @param joinPoint Contiene información sobre el método interceptado.
     */
    @After("controllerMethods()")
    public void logQueryHistory(JoinPoint joinPoint) {
        // Buscamos el objeto Principal en los argumentos del método
        Principal principal = findPrincipal(joinPoint.getArgs());

        if (principal != null) {
            String username = principal.getName();
            // Buscamos al usuario en la base de datos
            userService.findByUsername(username).ifPresent(user -> {
                String queryType = joinPoint.getSignature().getName(); // Nombre del método como tipo de consulta
                String parameters = Arrays.toString(joinPoint.getArgs()); // Parámetros del método

                logger.info("Registrando consulta para el usuario: {}, Tipo: {}, Parámetros: {}", username, queryType, parameters);
                queryHistoryService.recordQuery(user, queryType, parameters);
            });
        }
    }

    /**
     * Helper para encontrar el objeto Principal en la lista de argumentos de un método.
     * @param args Array de argumentos del método interceptado.
     * @return El objeto Principal si se encuentra, de lo contrario null.
     */
    private Principal findPrincipal(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof Principal principal) {
                return principal;
            }
        }
        return null;
    }
}
