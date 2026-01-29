#!/bin/sh
echo "Waiting 5 seconds for DB..."
sleep 5
exec java -jar app.jar
