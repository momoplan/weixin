set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean package -Pruyicai_product -Dmaven.test.skip=true && pause