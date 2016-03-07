defmodule FfServer do
  use Application

  def start(_type, _args) do
    import Supervisor.Spec, warn: false
    [
      worker(FfServer.Data, [[name: FfServer.Data]])
    ] |> Supervisor.start_link([strategy: :one_for_one])
  end
end
