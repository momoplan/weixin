set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean package -Pprelaunch -Dmaven.test.skip=true && pause