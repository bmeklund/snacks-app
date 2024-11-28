FROM registry.redhat.io/rhel8/mysql-80

ENV APP_DATA=/opt/app-root/src
ENV HOME=/var/lib/mysql

ENV MYSQL_DATABASE=snackdb
ENV MYSQL_USER=myuser
ENV MYSQL_PASSWORD=mypassword
ENV MYSQL_ROOT_PASSWORD=someroot

COPY import.sql /etc/mysql/import.sql

USER 27

EXPOSE 3306

ENTRYPOINT ["container-entrypoint"]
CMD ["run-mysqld", "--init-file=/etc/mysql/import.sql"]