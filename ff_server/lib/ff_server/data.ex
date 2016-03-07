defmodule FfServer.Data do
  use GenServer

  @pid __MODULE__
  @removed "*removed*"

  # TODO: implement persistence

  defmodule Item do
    defstruct [
      uid: "",
      name: "Product A",
      amount: "",
      expire: "2016-01-01",
      updated: "2016-01-01 01:01:01"
    ]
  end

  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, :ok, opts)
  end

  def list do
    GenServer.call(@pid, :list)
  end

  def sync(token, input_data) do
    GenServer.call(@pid, {:sync, token, input_data})
  end

  def init(:ok) do
    {:ok, %{}}
  end

  def handle_call(:list, _from, state) do
    {:reply, state, state}
  end

  def handle_call({:sync, token, input_data}, _from, state) do
    new_items = Enum.map(input_data, fn row -> as_item(row) end)
    data = Dict.get(state, token, [])
      |> merge_data(new_items)
    new_state = Dict.put(state, token, data)
    ret = data
      |> Enum.filter(fn i -> i.name != @removed end)
    ExStatsD.increment("ff.sync_count")
    new_state
      |> Map.keys
      |> ExStatsD.gauge("ff.fridge_count")
    {:reply, ret, new_state}
  end

  ## PRIVATE
  def as_item(row) do
    %Item{
      uid: Dict.get(row, "uid", ""),
      name: Dict.get(row, "name", @removed),
      amount: Dict.get(row, "amount", ""),
      expire: Dict.get(row, "expire", "2016-01-01"),
      updated: Dict.get(row, "updated", "2015-01-01 01:01:01")
    }
  end

  def merge_data(old, new) do
    %{}
      |> merge(old)
      |> merge(new)
      |> Map.values
  end

  def merge(map, []), do: map
  def merge(map, list) do
    Enum.reduce(list, map, fn item, acc ->
      existing = Dict.get(acc, item.uid, %Item{})
      if (item.updated > existing.updated) do
        Dict.put(acc, item.uid, item)
      else
        acc
      end
    end)
  end

end
