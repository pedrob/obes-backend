start:
	@docker-compose up -d db && export $(cat .env | xargs) && mvn spring-boot:run