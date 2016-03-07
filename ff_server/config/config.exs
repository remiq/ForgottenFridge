# This file is responsible for configuring your application
# and its dependencies with the aid of the Mix.Config module.
use Mix.Config

config :maru, FfServer.API,
  http: [port: 8101]

  config :ex_statsd,
    host: "localhost",
    port: 8125,
    namespace: "forgotten_fridge"
