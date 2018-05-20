# Scala Blockchain
Basic blockchain implementation build in scala with akka http.
## To Start Server
```$xslt
sbt run
```
## Interacting
### Mine a New Block
```$xslt
curl localhost:8080/mine
```
### Get Chain
```$xslt
curl localhost:8080/chain
```
### Create a Transaction
```$xslt
curl -X POST -H 'Content-Type: application/json' \
  -d '{\
    "sender": "a", \
    "receiver": "b", \
    "amount": 1 \
  }' \
  http://localhost:8081/transaction/create
```
