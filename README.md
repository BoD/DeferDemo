# Apollo Kotlin `@defer` demo

A simple Android demo app that shows how to use the `@defer` directive with Apollo Kotlin.

## Setup

The server used by this app is the Apollo Router from the [Supergraph Demo for Federation 2](https://github.com/apollographql/supergraph-demo-fed2).

To setup the server:
- You'll need docker and docker-compose
- Clone the Supergraph Demo for Federation 2 repository: `git clone git@github.com:apollographql/supergraph-demo-fed2.git`
- In this repo, execute:
  - `make docker-build-router-image`
  - `make docker-up-local-router-custom-image`
- A GraphQL server will now be listening for connections at http://localhost:4000.

Full setup instructions and more information available [here](https://github.com/apollographql/supergraph-demo-fed2).

You can now run the Android app on an emulator.

## What the app demonstrates

You should see a screen showing:
1. Basic product information (Id. SKU, Size)
2. After a few seconds: more information about inventory (estimated delivery, fastest delivery)

<img src="https://raw.githubusercontent.com/BoD/DeferDemo/master/screenrecord.gif" width="320" />

This is because the inventory fields are queried in a fragment with the `@defer` directive:

```graphql
query ProductQuery {
  product(id: "converse-1") {
    ...ProductInfoBasic
    ...ProductInfoInventory @defer # <- here!
  }
}

# Basic info (fast)
fragment ProductInfoBasic on Product {
  id
  sku
  dimensions {
    size
  }
}

# Inventory (slow)
fragment ProductInfoInventory on Product {
  delivery {
    estimatedDelivery
    fastestDelivery
  }
}
```

This way, fast fields can be received as soon as available, while the slower fields are received asynchronously.

## Alternative branch using GraphQL Helix

In the [`helix`](https://github.com/BoD/DeferDemo/tree/helix) branch, an alternative version of this demo is available, which uses an instance of the [GraphQL Helix](https://www.graphql-helix.com/) server instead of the Apollo Supergraph demo. It may be easier to setup.

Checkout the `helix` branch and follow the instructions in the [`helix/README.md`](https://github.com/BoD/DeferDemo/blob/helix/helix/README.md) file to start the server.
