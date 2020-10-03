FROM nessusio/centosj

COPY bin /nessus-h2/bin
COPY config /nessus-h2/config
COPY lib /nessus-h2/lib

ENTRYPOINT ["/nessus-h2/bin/dbsrv.sh"]
