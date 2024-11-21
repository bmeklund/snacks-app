FROM mysql:8.0

RUN chown -R mysql:root /var/lib/mysql/

ENV MYSQL_DATABASE=snackdb
ENV MYSQL_USER=myuser
ENV MYSQL_PASSWORD=mypassword
ENV MYSQL_ROOT_PASSWORD=someroot

COPY import.sql /etc/mysql/import.sql

RUN sed -i 's/MYSQL_DATABASE/'$MYSQL_DATABASE'/g' /etc/mysql/import.sql
RUN cp /etc/mysql/import.sql /docker-entrypoint-initdb.d

USER 27

EXPOSE 3306