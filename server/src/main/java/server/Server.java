package server;

import com.google.gson.Gson;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Step 3: Initialize Gson
        Gson gson = new Gson();

        // Step 4: Add Example Endpoint
        Spark.post("/example", (req, res) -> {
            // Deserialize JSON request to Java object
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

            // Perform some operations (for demonstration purposes, we just create a response)
            RegisterResult result = new RegisterResult(request.username(), "example_authToken");

            // Serialize Java object to JSON response
            return gson.toJson(result);
        });

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
