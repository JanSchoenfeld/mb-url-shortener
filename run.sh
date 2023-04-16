#!/bin/bash
mvn install -DskipTests
docker build -t mb-url-shortener.jar .
docker-compose up -d