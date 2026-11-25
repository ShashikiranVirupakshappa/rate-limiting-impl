package com.javabro.ratelimiting.tokenbucket;

public class TokenBucketClientApp {
    public static void main(String[] args) throws Exception {

        //TokenBucket tokenBucket = new TokenBucket(BigInteger.valueOf(1000L));
        TokenBucket tokenBucket = new TokenBucket(null);

        while(true) {
            try {
                Thread.sleep(10);
            }catch(InterruptedException e) {
                throw e;
            }
            System.out.println(tokenBucket.consumeToken());
        }
    }
}
