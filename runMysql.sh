# -d runs in background

docker run \
-p 3307:3306 \
-v cms:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD='password' \
-e MYSQL_DATABASE='cms' \
-e MYSQL_USER='cms-user' \
-e MYSQL_PASSWORD='password' \
mysql:8.0.20
