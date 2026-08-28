package com.fleethub.api.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fleethub.api.FleethubApiApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Clase adaptadora (Handler) para ejecutar la aplicación Spring Boot dentro de AWS Lambda.
 *
 * ¿Cómo funciona?
 * 1. Implementa {@link RequestStreamHandler}, que es la interfaz nativa que AWS Lambda
 *    invoca cuando recibe un evento (por ejemplo, desde Amazon API Gateway o un ALB).
 * 2. Utiliza la librería "AWS Serverless Java Container" para interceptar las peticiones HTTP
 *    entrantes y transformarlas en peticiones internas de Spring MVC (HttpServletRequest).
 * 3. De esta forma, todos tus controladores estándar de Spring (@RestController, @GetMapping, etc.)
 *    funcionan en AWS Lambda sin necesidad de reescribir nada.
 */
public class StreamLambdaHandler implements RequestStreamHandler {

    // Instancia estática del manejador proxy de Spring Boot.
    // Al ser estática, se mantiene viva en memoria entre invocaciones de Lambda (Warm Starts),
    // reduciendo drásticamente el tiempo de respuesta tras la primera petición (Cold Start).
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    // Bloque estático de inicialización:
    // Se ejecuta UNA SOLA VEZ cuando AWS Lambda crea la instancia del contenedor de ejecución.
    static {
        try {
            // Inicializa el contexto completo de Spring Boot pasando la clase principal (@SpringBootApplication)
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(FleethubApiApplication.class);
        } catch (ContainerInitializationException e) {
            // Si el arranque de Spring Boot falla (por ejemplo, error en configuración o beans),
            // se imprime el stacktrace y se lanza una excepción para que Lambda lo registre en CloudWatch.
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la aplicación Spring Boot en AWS Lambda", e);
        }
    }

    /**
     * Método principal invocado por AWS Lambda por cada solicitud entrante.
     *
     * @param inputStream Flujo de datos de entrada con el evento JSON de AWS API Gateway / Proxy.
     * @param outputStream Flujo de datos de salida donde se escribe la respuesta HTTP generada por Spring Boot.
     * @param context Objeto de contexto de AWS Lambda con metadatos de la ejecución (ID de petición, tiempo restante, logger).
     * @throws IOException Si ocurre un error de lectura/escritura en los flujos de datos.
     */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        // Enruta la petición de API Gateway hacia el dispatcher servlet de Spring Boot y devuelve la respuesta
        handler.proxyStream(inputStream, outputStream, context);
    }
}

