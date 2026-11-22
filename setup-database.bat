@echo off
REM ==================================================================
REM  LIBIS - creates the MySQL database by running libis.sql
REM  Double-click this file. It asks for your MySQL ROOT password.
REM ==================================================================
title LIBIS database setup
cd /d "%~dp0"

set "MYSQL="
for /d %%D in ("C:\Program Files\MySQL\MySQL Server *") do (
    if exist "%%D\bin\mysql.exe" set "MYSQL=%%D\bin\mysql.exe"
)
if not defined MYSQL (
    where mysql >nul 2>nul && set "MYSQL=mysql"
)
if not defined MYSQL (
    echo.
    echo  [X] MySQL was not found on this PC.
    echo      Install MySQL Server 8 with MySQL Installer:
    echo      https://dev.mysql.com/downloads/installer/
    echo.
    pause
    exit /b 1
)
echo Using: %MYSQL%

if not exist "libis.sql" (
    echo  [X] libis.sql is not in this folder. Keep this file next to libis.sql.
    pause
    exit /b 1
)

echo.
echo Step 1 of 2: creating the libis database (enter your MySQL ROOT password)
"%MYSQL%" -u root -p --default-character-set=utf8mb4 < libis.sql
if errorlevel 1 (
    echo.
    echo  [X] The script failed. Check the root password and that the MySQL service is running
    echo      ^(Win+R, services.msc, MySQL80 = Running^).
    pause
    exit /b 1
)

echo.
echo Step 2 of 2: checking that LIBIS can log in as libis_app
"%MYSQL%" -u libis_app -pLibisApp#2026 --default-character-set=utf8mb4 -e "SELECT COUNT(*) AS members FROM libis.members; SELECT COUNT(*) AS books FROM libis.books; SELECT COUNT(*) AS loans FROM libis.loans;"
if errorlevel 1 (
    echo.
    echo  [X] The database was created, but libis_app cannot log in.
    pause
    exit /b 1
)

echo.
echo  [OK] Database ready: 6 members, 10 books, 10 loans expected above.
echo       Now copy mysql-connector-j-9.7.0.jar into Tomcat's lib folder (if not done),
echo       restart Tomcat in IntelliJ and log in as librarian / admin123.
echo.
pause
