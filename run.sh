docker rm -vf $(docker ps -aq)
docker rmi -f $(docker images -aq)
./gradlew clean build && docker-compose up -d
docker logs authorizer-api-1 --follow