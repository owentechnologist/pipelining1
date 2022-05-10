package com.redislabs.sa.ot.fiddlewith.pipelining;

import com.redislabs.sa.ot.util.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * Instead of using this or other code - try this utility:
 * redis-benchmark
 * https://redis.io/docs/reference/optimization/benchmarks/
 */
//run this java code by executing:
// mvn compile exec:java
// mvn compile exec:java -Dexec.args="10 10 true"
// or perhaps you prefer:
// mvn compile exec:java -Dexec.args="1000 500 false"
// first arg is opsPerLoop, second arg is numberOfLoops, third is isVerbose output
//  mvn compile exec:java -Dexec.args="1000 500 false test3"
// fourth argument is a key prefix for if/when you want to differentiate keys in tests
public class Main {
    public static JedisPool jedisPool = JedisConnectionFactory.getInstance().getJedisPool();

    // 1st arg sets opsPerLoop
    // second sets numberOfLoops
    // third sets isVerbose (limits amount of print statements)
    // fourth sets key prefix for easy filtering when looking for keys
    public static void main(String[] args ) {
        long opsPerLoop = 1000;
        long numberOfLoops = 100;
        boolean isVerbose = true;
        String keyPrefix = "key";
        if(args.length>0) {
            opsPerLoop = Long.parseLong(args[0]);
        }
        if(args.length>1){
            numberOfLoops = Long.parseLong(args[1]);
        }
        if(args.length>2){
            isVerbose = Boolean.parseBoolean(args[2]);
        }
        if(args.length>3){
            keyPrefix = args[3];
        }
        System.out.println("THIS TEST WILL BATCH-SET "+opsPerLoop+" Strings "+numberOfLoops+" times\n");
        long testDuration = System.currentTimeMillis(); //Will do math later
        long startTime = 0;
        long endTime = 0;
        try (Jedis jedis = Main.jedisPool.getResource()) { // pool will auto-close connection
            Pipeline batcher = jedis.pipelined();
            for(int l = 0; l < numberOfLoops; l++ ){
                for (int n = 0; n < opsPerLoop; n++) {
                    String key = keyPrefix+ ":"+ n + "__Client-Side-time-is__" + System.currentTimeMillis();
                    String padding = "__asfghdkewhef*ksjdbf%dfkjb)dzfkjb___";
                    String payloadBase = "value:bar" + n + "__Client-Side-nano-time-is__" + System.nanoTime()+padding+" --> "+key;
                    if(System.nanoTime()%3==0) { // make some keys bigger (around 1kb)
                        String valueForKey = payloadBase + padding + payloadBase + padding + payloadBase + padding + payloadBase+ padding + payloadBase+ padding + payloadBase + System.nanoTime();
                        batcher.set(key, valueForKey);
                    }else{
                        batcher.set(key, payloadBase);
                    }
                }
                try (Jedis notPipe = Main.jedisPool.getResource()) {
                    printIfVerbose("Jedis Server says time is now: " + notPipe.time().get(0),isVerbose);
                }
                startTime=System.currentTimeMillis();
                List<Object> s = batcher.syncAndReturnAll();
                endTime=System.currentTimeMillis();
                printIfVerbose("Loop #"+l+" >>> executed "+opsPerLoop+" ops in "+((endTime-startTime))+ " millis",isVerbose);
                printIfVerbose("Loop #"+l+" >>> # Strings set: "+s.size(),isVerbose);
            }

            jedis.disconnect();
            System.out.println("\n\nTOTAL SECONDS FOR TEST: "+((System.currentTimeMillis()-testDuration)/1000));
        }//end of try block
    }

    static void printIfVerbose(String content, boolean isVerbose){
        if(isVerbose){
            System.out.println(content);
        }
    }
}
