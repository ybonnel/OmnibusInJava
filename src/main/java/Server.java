import fr.ybonnel.simpleweb4j.SimpleWeb4j;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.ContentType;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.get;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPort;


public class Server {
    private final Elevator elevator;

    public Server() {
        elevator = new Omnibus();
    }

    public void start() {
        setPort(getPort());

        get(new Route<Void, Command>("/nextCommand", Void.class, ContentType.PLAIN_TEXT) {
            @Override
            public Response<Command> handle(Void o, RouteParameters parameters) throws HttpErrorException {
                return new Response<>(elevator.nextCommand());
            }
        });

        get(new Route<Void, Void>("/reset", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                elevator.reset(routeParams.getParam("cause"));
                return new Response<>(null);
            }
        });

        get(new Route<Void, Void>("/call", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                elevator.call(Integer.parseInt(routeParams.getParam("atFloor")),
                        routeParams.getParam("to"));
                return new Response<>(null);
            }
        });

        get(new Route<Void, Void>("/go", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                elevator.go(Integer.parseInt(routeParams.getParam("floorToGo")));
                return new Response<>(null);
            }
        });

        get( new Route<Void, Void>("/userHasEntered", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                elevator.userHasEntered();
                return new Response<>(null);
            }
        });

        get(new Route<Void, Void>("/userHasExited", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                elevator.userHasExited();
                return new Response<>(null);
            }
        });

        SimpleWeb4j.start();
    }



    /**
     * @return port to use
     */
    private static int getPort() {
        // Heroku
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }

        // Cloudbees
        String cloudbeesPort = System.getProperty("app.port");
        if (cloudbeesPort != null) {
            return Integer.parseInt(cloudbeesPort);
        }

        // Default port;
        return 9999;
    }


    public static void main(String[] args) {
        new Server().start();
    }

}
