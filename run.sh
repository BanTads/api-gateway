docker-compose -f rabbitmq.yml up -d
sleep 10
docker-compose -f docker-compose.yml up