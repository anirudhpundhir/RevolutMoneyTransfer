## How to build
`gradle fatJar`  
In *build/libs* will be jar file to run

## How to run
`java -jar money-transfer-all-1.0-SNAPSHOT.jar`  
url - `localhost:4567`
## How to use
Each user has default account with 10000 RUB  
After running, there will be created three users.
First user will have two additional accounts, one with euro

GET `/users` - get all users  
GET `/users/:id` - get user  
POST `/users` - create new user  
PUT `/users/:id` - update existing user  
DELETE `/users/:id` - delete user  

GET `/accounts` - get all accounts  
GET `/accounts/:accountNumber` - get account by its number  
POST `/accounts` - create new accounts  
PUT `/accounts/:accountNumber` - update existing accounts  
DELETE `/accounts/:accountNumber` - delete accounts  

GET `/accounts/:accountNumber/transfers` - get transfers history for account   
GET `/accounts/:accountNumber/transfers/:id` - get transfer by its number  
POST `/accounts/:accountNumber/transfers` - create new transfer