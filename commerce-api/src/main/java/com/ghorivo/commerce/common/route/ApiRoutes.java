package com.ghorivo.commerce.common.route;

public final class ApiRoutes {

    public static final String API_V1 = "/api/v1";

    private ApiRoutes() {
    }

    public static final class Identity {

        public static final String AUTH = API_V1 + "/auth";
        public static final String USERS = API_V1 + "/users";

        private Identity() {
        }
    }

    public static final class Catalog {

        public static final String PRODUCTS = API_V1 + "/products";
        public static final String CATEGORIES = API_V1 + "/categories";

        private Catalog() {
        }
    }

    public static final class Inventory {

        public static final String STOCK = API_V1 + "/stock";

        private Inventory() {
        }
    }

    public static final class Orders {

        public static final String BASE = API_V1 + "/orders";

        private Orders() {
        }
    }

    public static final class Customers {

        public static final String BASE = API_V1 + "/customers";

        private Customers() {
        }
    }
}
