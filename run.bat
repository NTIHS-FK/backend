docker run --name pgdb\
-e POSTGRES_USER=pguser\
-e POSTGRES_PASSWORD=4818115\
-e domain=127.0.0.1:8080\
-e ssl=true\
-p 5432:5432 -d postgres --restart always

@REM JAR 參數 -DLOG_DEST=./logs -DLOG_MAX_HISTORY=1