#!/bin/bash

REPOSITORY=/home/ec2-user/IdleGameServer/IdleGameServer

cd $REPOSITORY/eternalfantasia/

echo "> Git Pull"

git pull

echo "> 프로젝트 Build 시작"

./gradlew build

echo "> Build 파일 복사"

cp ./build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 redis-server pid 확인"

REDIS_SERVER_PID=$(pgrep -f redis-server)

echo "$REDIS_SERVER_PID"

if [ -z $REDIS_SERVER_PID ]; then
    echo "> 현재 구동중인 redis-server가 없으므로 종료하지 않습니다."
else
    echo "> kill -2 $REDIS_SERVER_PID"
    kill -9 $REDIS_SERVER_PID
    sleep 5
fi

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f eternalfantasia)

echo "$CURRENT_PID"

if [ -z $CURRENT_PID ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -2 $CURRENT_PID"
    kill -9 $CURRENT_PID
    sleep 5
fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls $REPOSITORY/ |grep 'eternalfantasia' | tail -n 1)

echo "> JAR Name: $JAR_NAME"

nohup java -jar $REPOSITORY/$JAR_NAME 2>&1 &