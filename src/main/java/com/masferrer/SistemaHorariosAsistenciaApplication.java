package com.masferrer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Collections;


@SpringBootApplication
public class SistemaHorariosAsistenciaApplication {

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SistemaHorariosAsistenciaApplication.class);

        // Obtener el valor del puerto de la variable de entorno PORT
        String port = System.getenv("PORT");
        
        // Establecer el puerto en la aplicación
        if (port != null) {
            app.setDefaultProperties(Collections.singletonMap("server.port", port));
        }

        // Iniciar la aplicación
        app.run(args);
	}

}
