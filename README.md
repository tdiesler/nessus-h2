## Nessus H2

You can spin up an [H2](http://h2database.com) database like this ...

```
docker rm -f dbsrv
docker run --detach \
    --name dbsrv \
    -p 8084:8084 \
    -e JDBC_SERVER_URL="jdbc:h2:tcp://localhost:8084/nessus" \
    -e JDBC_URL="jdbc:h2:/tmp/h2db/nessus" \
    -e JDBC_USER="h2" \
    -e JDBC_PASS="" \
    nessusio/nessus-h2
```

or with volume persistence, like this ... 

```
docker rm -f dbsrv
docker run --detach \
    --name dbsrv \
    -p 8084:8084 \
    -v h2vol:/tmp/h2db \
    -e JDBC_SERVER_URL="jdbc:h2:tcp://localhost:8084/nessus" \
    -e JDBC_URL="jdbc:h2:/tmp/h2db/nessus" \
    -e JDBC_USER="h2" \
    -e JDBC_PASS="" \
    nessusio/nessus-h2
```

Enjoy!
