defmodule FfServer.API do
  use Maru.Router

  namespace :fridge do

    post "/" do
      token = Dict.get(conn.body_params, "token", "")
      data = Dict.get(conn.body_params, "data", "[]")
        |> Poison.decode!
        |> IO.inspect
      ret = %{
        "data" => FfServer.Data.sync(token, data)
      } |> IO.inspect
      conn
        |> put_status(200)
        |> json(ret)
    end
  end
end
