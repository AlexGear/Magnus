#!/bin/bash
# ASSUMING THAT YOU HAVE ALREADY CREATED THE USER

CONTAINER=magnus-db
USER=admin
PASSWORD=ausrotten
sudo docker exec -i $CONTAINER mysql -u $USER --password=$PASSWORD -e "CREATE DATABASE IF NOT EXISTS magnus; CREATE DATABASE IF NOT EXISTS magnus_test;"
cat db.sql | sudo docker exec -i $CONTAINER mysql -u $USER --password=$PASSWORD magnus
cat db.sql | sudo docker exec -i $CONTAINER mysql -u $USER --password=$PASSWORD magnus_test
