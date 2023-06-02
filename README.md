# codeforces-spring-boot-api

End Points

POST http://localhost:8080/codeforces/users/{userHandle}
fetches data from codeforces api and adds the user to the mongodb database

GET http://localhost:8080/codeforces/users
get all the users from the database

GET http://localhost:8080/codeforces/users/descByRating
fetches users from database and sorts them on the base of rating

GET http://localhost:8080/codeforces/users/byCity
fetches and groups the user by city

GET http://localhost:8080/codeforces/users/highestRankedByCity
fetches the highest ranked programmer in a city

GET http://localhost:8080/codeforces/users/byCityDescRating
groups the users by city and sorts them in decreasing order

POST http://localhost:8080/codeforces/elastic/users/{userHandle}
fetches data from codeforces api and add the user to elastic search

GET http://localhost:8080/codeforces/elastic/users
get all the users from the elasticsearch

GET http://localhost:8080/codeforces/elastic/users/name/{nameSubstring}
get all the users from elastic search that contains the nameSubstring in their firstName

GET http://localhost:8080/codeforces/elastic/users/byRatingAsc
get all the users sorted by their rating in ascending order

