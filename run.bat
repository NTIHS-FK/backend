docker run -d -p 5432:5432 --name pgdb \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=4818115 \
--restart always postgres

@REM JAR 參數 -DLOG_DEST=./logs -DLOG_MAX_HISTORY=1
@REM -e DOMAIN=127.0.0.1:8080 -e SSL=true -e jwt_secret=