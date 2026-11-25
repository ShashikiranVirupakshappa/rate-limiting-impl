package com.javabro.ratelimiting.tokenbucket;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class TokenBucket {

    private BigInteger availableTokens;
    //monotonic clock reading (not wall-clock) so system clock changes / DST cannot stall or advance refills
    private long refilledNanoTime = System.nanoTime();
    private static final int REFILL_INTERVAL = 10000;
    private static final BigInteger NUM_OF_TOKEN = BigInteger.valueOf(1000L);
    private static final int TOKEN_CAPACITY = 10000;

    public TokenBucket(BigInteger availableTokens) {
        if(availableTokens == null)
            availableTokens = BigInteger.valueOf(1000L);
        if(availableTokens.signum() < 0)
            throw new IllegalArgumentException("initial available tokens cannot be negative");
        this.availableTokens = availableTokens;
    }

    //if elapsed time is greater than the refill interval then try adding 1000 more tokens to the available tokens or else it will come out by doing nothing
    private synchronized void refill() {
        if(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - refilledNanoTime) > REFILL_INTERVAL && availableTokens.intValue() < TOKEN_CAPACITY) {
            if(TOKEN_CAPACITY - availableTokens.intValue() < NUM_OF_TOKEN.intValue())
                availableTokens = availableTokens.add(BigInteger.valueOf(TOKEN_CAPACITY - availableTokens.intValue()));
            else
                availableTokens = availableTokens.add(NUM_OF_TOKEN);
            refilledNanoTime = System.nanoTime();
        }
        if(availableTokens.intValue() > TOKEN_CAPACITY) {
            System.out.println("available tokens exceeded the capacity of the token bucket");
        }
    }

    /*client facing method - if any call to consumeToken method then it will decrement a token from available tokens
    it will throw exception when it runs out of all available tokens
     */
    public synchronized String consumeToken() {
        refill();
        if(availableTokens.signum() > 0)
            availableTokens = availableTokens.subtract(BigInteger.valueOf(1L));
        else
            throw new RuntimeException("No more tokens available - you have consumed all tokens");
        return availableTokens.toString();
    }
}