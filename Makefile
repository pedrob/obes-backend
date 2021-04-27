start:
	@docker-compose up -d db && sleep 2 && export $(cat .env | xargs) && mvn spring-boot:run