## Nessus H2

You can spin up an [H2](http://h2database.com) database like this ...

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    -e JDBC_SERVER_URL="jdbc:h2:tcp://localhost:9092/nessus" \
    -e JDBC_URL="jdbc:h2:/var/h2db/nessus" \
    -e JDBC_USER="h2" \
    -e JDBC_PASSWORD="" \
    nessusio/nessus-h2
    
docker logs -f h2

docker cp h2:h2db/debug.log .
tail -n 1000 debug.log
```

or with volume persistence, like this ... 

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    -v h2vol:/var/h2db \
    -e JDBC_SERVER_URL="jdbc:h2:tcp://localhost:9092/nessus" \
    -e JDBC_URL="jdbc:h2:/var/h2db/nessus" \
    -e JDBC_USER="h2" \
    -e JDBC_PASSWORD="" \
    nessusio/nessus-h2
```

### Enable Mission Control

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    -p 7091:7091 \
    -e JDBC_SERVER_URL="jdbc:h2:tcp://localhost:9092/nessus" \
    -e JDBC_URL="jdbc:h2:/var/h2db/nessus" \
    -e JDBC_USER="h2" \
    -e JDBC_PASSWORD="" \
    -e JMX_REMOTE=true \
    -e JMX_REMOTE_PORT=7091 \
    -e JMX_REMOTE_HOST=yourhost \
    -e JMX_REMOTE_SECURITY=false \
    nessusio/nessus-h2
    
docker logs -f h2
```

Enjoy!
