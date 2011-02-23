#! /bin/bash

mockapiPath="mockapi"
cd $mockapiPath
pwd
grails -DfindMe=jp2pMockapi -Dmockapi=true run-app 1>log/std.out 2>log/std.err &
sleep 5s

#while [ "$pong" != "pong" ]
#do
#    echo testing...
#    pong=`curl "http://localhost:8080/ping" 2>/dev/null`
#    sleep 1s
#done
echo mockapi started
