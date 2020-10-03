## Nessus H2

Run the image 

```
docker rm -f dbsrv
docker run --detach \
    --name dbsrv \
    -p 8084:8084 \
    -v h2vol:/var/opt/h2 \
    -e JDBC_SERVER_URL=jdbc:h2:tcp://localhost:8084/var/opt/h2 \
    -e JDBC_URL=jdbc:h2:file:/var/opt/h2/nessus \
    -e JDBC_USER=h2 \
    -e JDBC_PASS=h2 \
    nessusio/nessus-h2

docker logs -f dbsrv

docker exec dbsrv tail -n1000 -f nessus-h2/debug.log

docker cp dbsrv:nessus-h2/debug.log .
tail -n 1000 debug.log
```

Enjoy!
