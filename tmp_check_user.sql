ALTER SESSION SET CONTAINER = orclpdb;
SELECT username FROM dba_users WHERE username IN ('FASTFOODDB','TIMO','SYSTEM') ORDER BY username;
exit
