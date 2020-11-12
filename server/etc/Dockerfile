FROM azul/zulu-openjdk-centos:8

COPY bin /h2db/bin
COPY lib /h2db/lib
COPY config /h2db/config

ENTRYPOINT ["/h2db/bin/run.sh"]
