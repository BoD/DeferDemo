import express from "express";
import {WebSocketServer} from 'ws';
import cors from 'cors';
import {useServer} from "graphql-ws/lib/use/ws";
import {
  getGraphQLParameters,
  processRequest,
  renderGraphiQL,
  sendMultipartResponseResult,
  sendResponseResult,
  shouldRenderGraphiQL,
} from "graphql-helix";
import {
  execute,
  GraphQLBoolean,
  GraphQLDeferDirective,
  GraphQLError,
  GraphQLID,
  GraphQLInt,
  GraphQLList,
  GraphQLNonNull,
  GraphQLObjectType,
  GraphQLSchema,
  GraphQLStreamDirective,
  GraphQLString,
  specifiedDirectives,
  subscribe,
} from "graphql";

const app = express();

app.use(cors());
app.use(express.json());

const schema = new GraphQLSchema({
  query: new GraphQLObjectType({
    name: "Query",
    fields: () => ({
      computers: {
        type: new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLObjectType({
          name: "Computer",

          fields: () => ({
            id: {
              type: new GraphQLNonNull(GraphQLID),
            },
            cpu: {
              type: new GraphQLNonNull(GraphQLString),
            },
            year: {
              type: new GraphQLNonNull(GraphQLInt),
            },
            screen: {
              type: new GraphQLNonNull(new GraphQLObjectType({
                name: "Screen",

                fields: () => ({
                  resolution: {
                    type: new GraphQLNonNull(GraphQLString),
                  },
                  isColor: {
                    type: new GraphQLNonNull(GraphQLBoolean),
                  },
                }),
              }))
            },
          })
        })))),

        resolve: () => (
          [
            {
              "id": "Computer1",
              "cpu": "386",
              "year": 1993,
              "screen": {
                "resolution": "640x480",
                "isColor": false
              }
            },
            {
              "id": "Computer2",
              "cpu": "486",
              "year": 1996,
              "screen": {
                "resolution": "800x600",
                "isColor": true
              }
            }
          ]
        ),
      },
    }),

  }),

  subscription: new GraphQLObjectType({
    name: "Subscription",
    fields: () => ({
      count: {
        type: new GraphQLObjectType({
          name: "Counter",

          fields: () => ({
            value: {
              type: new GraphQLNonNull(GraphQLInt),
            },
            valueTimesTwo: {
              type: new GraphQLNonNull(GraphQLInt),
            },
          })
        }),
        args: {
          to: {
            type: GraphQLInt,
          },
        },
        subscribe: async function* (_root, args) {
          for (let count = 1; count <= args.to; count++) {
            await new Promise((resolve) => setTimeout(resolve, 1000));
            yield {
              "count": {
                "value": count,
                "valueTimesTwo": count * 2,
              }
            };
          }
        },
      },
    }),
  }),

  directives: [
    ...specifiedDirectives,
    GraphQLDeferDirective,
    GraphQLStreamDirective,
  ],
});


app.use("/graphql", async (req, res) => {
  const request = {
    body: req.body,
    headers: req.headers,
    method: req.method,
    query: req.query,
  };

  if (shouldRenderGraphiQL(request)) {
    res.send(
      renderGraphiQL({
        subscriptionsEndpoint: "ws://localhost:4000/graphql",
      })
    );
    return;
  }

  const {operationName, query, variables} = getGraphQLParameters(request);

  const result = await processRequest({
    operationName,
    query,
    variables,
    request,
    schema,
  });

  if (result.type === "RESPONSE") {
    sendResponseResult(result, res);
  } else if (result.type === "MULTIPART_RESPONSE") {
    sendMultipartResponseResult(result, res);
  } else {
    res.status(422);
    res.json({
      errors: [new GraphQLError("Subscriptions should be sent over WebSocket.")],
    });
  }
});

const port = process.env.PORT || 4000;

const server = app.listen(port, () => {
  const wsServer = new WebSocketServer({
    server,
    path: "/graphql",
  });

  useServer({schema, execute, subscribe}, wsServer);

  console.log(`GraphQL server is running on port ${port}.`);
});
