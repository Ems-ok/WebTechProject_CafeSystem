FROM ubuntu:latest
LABEL authors="emmao"

ENTRYPOINT ["top", "-b"]