
Backend Test
Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.
Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like ( except Spring ), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.


Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.
Please put your work on github or bitbucket.

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
