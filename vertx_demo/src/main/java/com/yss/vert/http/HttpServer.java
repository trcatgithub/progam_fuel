package com.yss.vert.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashSet;
import java.util.Set;


public class HttpServer extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(HttpServer.class, new DeploymentOptions());
    }

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        // CROS
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);

        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);
        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

        {
            // File upload demo
            router.route("/static/*").handler(StaticHandler.create().setWebRoot("fileupload"));
            router.route("/form").handler(FileUploadHandler::hander);
        }

        {
            //Handling requests and calling the next handler
            Route route = router.route("/handler/chain");
            route.handler(ChainHandler::hander1);
            route.handler(ChainHandler::hander2);
            route.handler(ChainHandler::hander3);
        }

        {
            // Using blocking handlers
            Route route = router.route("/handller/block");
            route.blockingHandler(BlockingHandler::hander, false);
        }

        {
            // Capturing path parameters
            Route route = router.route(HttpMethod.GET, "/handler/param/:param1/:param2");
            route.handler(CaptureParamHandler::handerPathParam);
        }

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}