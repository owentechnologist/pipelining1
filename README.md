# Pipelining1
## First off:
### There is a terrific Benchmarking tool available here:

https://redis.io/docs/reference/optimization/benchmarks/

## TRY BENCHMARK ^ FIRST! 
### You can start with the simplest default command:
```> redis-benchmark ```

### If you insist on using this or similar code instructions follow:

## The connection information is set using the jedisconnectionfactory.properties file (so edit that file to match your environment)

#### To run this test application you can execute:
```
mvn compile exec:java
```

#### add args as follows:
```
mvn compile exec:java -Dexec.args="10 10 true"
```
#### first arg is opsPerLoop
#### second arg is numberOfLoops
#### third is isVerbose output

```
mvn compile exec:java -Dexec.args="1000 500 false"
```

####  the fourth argument is a key prefix for if/when you want to differentiate keys in tests

```
mvn compile exec:java -Dexec.args="1000 500 false test3"
```


## AGAIN... TRY the redis official BENCHMARK tool: 
https://redis.io/docs/reference/optimization/benchmarks/
