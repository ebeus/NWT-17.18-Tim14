#!/bin/sh
while ! nc -z eureka-server 8761 ; do
    echo "Waiting for the Eureka Server"
    sleep 3
done
java -jar /opt/lib/nwt_tim14_korisnici-1.0-SNAPSHOT.jar