package edu.ub.domain.session;

import edu.ub.domain.model.entities.Client;

public class SessionManager {
    private static Client currentClient = null;

    public static void login(Client client) {
        currentClient = client;
    }

    public static void logout() {
        currentClient = null;
    }

    public static boolean isLoggedIn() {
        return currentClient != null;
    }

    public static Client getCurrentClient() {
        return currentClient;
    }

    public static void setCurrentClient(Client client) {
        currentClient = client;
    }

}
