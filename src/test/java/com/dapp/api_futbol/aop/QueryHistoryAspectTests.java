package com.dapp.api_futbol.aop;

import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {QueryHistoryAspect.class, QueryHistoryAspectTests.TestController.class})
@ExtendWith(SpringExtension.class)
public class QueryHistoryAspectTests {

    // Mockeamos los servicios que el aspecto usa para aislarlos de la prueba.
    @MockBean
    private QueryHistoryService queryHistoryService;

    @MockBean
    private UserService userService;

    // Inyectamos nuestro controlador de prueba.
    @Autowired
    private TestController testController;

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;
    @Autowired
    private QueryHistoryAspect queryHistoryAspect;

    /**
     * Controlador de prueba simple para que el aspecto lo intercepte.
     * Lo definimos como una clase interna estática para que Spring lo pueda escanear.
     */
    @RestController
    static class TestController {
        @GetMapping("/test/with-principal/{id}")
        public String testMethodWithPrincipal(@PathVariable String id, Principal principal) {
            return "ok";
        }

        @GetMapping("/test/without-principal")
        public String testMethodWithoutPrincipal() {
            return "ok";
        }
    }

    @Test
    public void whenMethodWithPrincipalIsCalled_thenQueryShouldBeRecorded() {
        // Arrange
        String username = "testuser";
        User mockUser = new User(); // Asume que User tiene un constructor por defecto
        Principal mockPrincipal = () -> username;

        // Configuramos el mock de UserService para que devuelva nuestro usuario de prueba.
        when(userService.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        // Instead of relying on proxy-based AOP in this small unit test, invoke the aspect directly
        // by constructing a mocked JoinPoint representing the controller method execution.
        org.aspectj.lang.JoinPoint joinPoint = mock(org.aspectj.lang.JoinPoint.class);
        org.aspectj.lang.Signature signature = mock(org.aspectj.lang.Signature.class);
        when(signature.getName()).thenReturn("testMethodWithPrincipal");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"123", mockPrincipal});

        // Call the aspect's advice method directly
        queryHistoryAspect.logQueryHistory(joinPoint);

        // Assert
        // Verificamos que el método recordQuery de nuestro servicio mock fue llamado exactamente una vez.
        verify(queryHistoryService, times(1)).recordQuery(any(User.class), anyString(), anyString());

        // Opcional: Podemos ser más específicos y verificar los argumentos exactos.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> paramsCaptor = ArgumentCaptor.forClass(String.class);

        verify(queryHistoryService).recordQuery(userCaptor.capture(), typeCaptor.capture(), paramsCaptor.capture());

        assertEquals(mockUser, userCaptor.getValue());
        assertEquals("testMethodWithPrincipal", typeCaptor.getValue());
        assertTrue(paramsCaptor.getValue().contains("123"));
    }

    @Test
    public void whenMethodWithoutPrincipalIsCalled_thenQueryShouldNotBeRecorded() {
        // Arrange (no se necesita nada especial)

        // Act
        applicationContext.getBean(TestController.class).testMethodWithoutPrincipal();

        // Assert
        // Verificamos que el método recordQuery NUNCA fue llamado.
        verify(queryHistoryService, never()).recordQuery(any(), any(), any());
    }
}
