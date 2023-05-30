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
