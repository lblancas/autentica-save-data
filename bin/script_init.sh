#!/bin/sh
echo "Config server is up and running"
java -Xms256m -Xmx512m -XX:-TieredCompilation -Xss256k -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom -jar /home/service.jar  --spring.profiles.active=$PROFILES_ACTIVE
