{application,ff_server,
             [{registered,[]},
              {description,"ff_server"},
              {vsn,"0.0.2"},
              {modules,['Elixir.FfServer','Elixir.FfServer.API',
                        'Elixir.FfServer.Data','Elixir.FfServer.Data.Item']},
              {mod,{'Elixir.FfServer',[]}},
              {applications,[kernel,stdlib,elixir,logger,maru,ex_statsd,
                             poison]}]}.