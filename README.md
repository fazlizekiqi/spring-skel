
## The application uses json web token to secure the Rest APIs. ## 
- Every time you run the program 2 users with different roles get persisted
to the database:
- USER and ADMIN 

> Below you can get the commands on how login, access jwt and make api calls using curl. 
##### Authorities: ##### 
    - User:
            -> GET all books 
            -> GET books by query 
            -> GET book by guid 
    - Admin:   
            -> GET all books 
            -> GET book by query 
            -> GET book by guid 
            -> CREATE new book 
            -> UPDATE  book 
            -> DELETE book by ID  
        
#### First you need to access a token to make the api calls :

- As user\
`
 curl -H "Content-Type: application/json" \
           -X POST \
           -d '{"mUsername":"user","mPassword":"user"}' \
           http://localhost:9443/app/api/reader/login
` 
- As admin\
`
 curl -H "Content-Type: application/json" \
           -X POST \
           -d '{"mUsername":"admin","mPassword":"admin"}' \
           http://localhost:9443/app/api/reader/login
` 
### If you have jq 'Command-line JSON processor' installed you can save the token directly in a variable so that we can use it on our api calls.  
`
TOKEN=$(curl -H "Content-Type: application/json" \
                   -X POST \
                   -d '{"mUsername":"admin","mPassword":"admin"}' \
                   http://localhost:9443/app/api/reader/login |jq -r '.authorization')
`
##### GET:
*   `curl -H 'Accept: application/json' -H "Authorization: ${TOKEN}" http:/localhost:9443/app/api/1.0/book`         
*   `curl -H 'Accept: application/json' -H "Authorization: ${TOKEN}" http:/localhost:9443/app/api/1.0/book/search\?query=Hobbit&page=0&size=10`
*   `curl -H 'Accept: application/json' -H "Authorization: ${TOKEN}" http:/localhost:9443/app/api/1.0/book/{book-uuid}`
##### POST:
 `
 curl -H "Content-Type: application/json" \
      -H "Authorization: ${TOKEN}"  \
           -X POST \har 
           -d '{"mTitle":"Java steg för steg","mAuthor":"Jan Skansholm","mISBN":"978-3-16-148412-4"}' \
           http://localhost:9443/app/api/1.0/book
 `
##### DELETE:
`
 curl -X DELETE \
   -H "Authorization: ${TOKEN}"  \
   http://localhost:9443/app/api/1.0/book/{bookGuid}   
`
##### PATCH:
`
 curl -H "Content-Type: application/json" \
      -H "Authorization: ${TOKEN}"  \
            -X PATCH \
            -d '{"mTitle":"My new Title","mAuthor":"Author name correction!","mISBN":"978-3-16-148412-4"}' \
            http://localhost:9443/app/api/1.0/book/{bookUUID}   
` 


###OBS! If you dont have 'jq' installed on your pc, you can install it with npm :
        npm install -g jq  
###or you can make api call like this  
`
curl -H 'Accept: application/json'\
 -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IlVTRVIifV0sImlhdCI6MTYwMzQxMzY5OSwiZXhwIjoxNjAzODM5NjAwfQ.AHzRaSuLmHb5vywt36bG4W5otFRrAjqyHB_02xTxK968a0Wt84NsaQpmLw2Y2IWlY-9r_9vjipzswcinzks_Sw" \
  http:/localhost:9443/app/api/1.0/book
`

###Couldn't build the application after I cloned the project due to the gradle version.
#####I changed the gradle version from 5.6 to 6.3 on the gradle-wrapper.properties 
   
#####https://github.com/gradle/gradle/issues/12599
