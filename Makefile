mvn:
	mvn clean install -DskipTests

product:
	cd product && mvn clean install -DskipTests

product-run:
	cd product && mvn spring-boot:run

product-stop:
	cd product && mvn spring-boot:stop

product-restart:
	cd product && mvn spring-boot:restart

product-logs:
	cd product && mvn spring-boot:logs

product-status:
	cd product && mvn spring-boot:status

cd:
	cd../