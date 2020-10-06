FROM nessusio/centosj

COPY bin /h2db/bin
COPY config /h2db/config
COPY lib /h2db/lib

ENTRYPOINT ["/h2db/bin/dbsrv.sh"]
