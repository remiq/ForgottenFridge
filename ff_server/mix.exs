defmodule FfServer.Mixfile do
  use Mix.Project

  def project do
    [app: :ff_server,
     version: "0.0.2",
     elixir: "~> 1.2",
     build_embedded: Mix.env == :prod,
     start_permanent: Mix.env == :prod,
     aliases: aliases,
     deps: deps]
  end

  # Configuration for the OTP application
  #
  # Type "mix help compile.app" for more information
  def application, do: [
    mod: {FfServer, []},
    applications: [:logger, :maru, :ex_statsd, :poison]]


  defp deps, do: [
    {:maru, "~> 0.9.2"},
    {:poison, "~> 2.0"},
    {:ex_statsd, "~> 0.5.2"},
    {:exrm, "~> 0.19"}]

  defp aliases, do: [
    rsync: &rsync/1,
    r: ["release", "rsync"]]

  defp rsync(_args) do
    conf = project()
    app_name = Atom.to_string conf[:app]
    version = conf[:version]
    IO.puts "Sending release to remote server"
    {stdout, _status} = System.cmd("rsync", [ "-Pravdtze", "ssh",
      "rel/#{app_name}/releases/#{version}/#{app_name}.tar.gz",
      "mirkoczat@congo:/home/mirkoczat/ff/releases/#{version}/"])
    IO.puts stdout
  end

end
