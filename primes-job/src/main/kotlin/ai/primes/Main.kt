package ai.primes

import ai.primes.MatrixRenderer.dump
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.redis.RedisIO
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.*
import org.apache.beam.sdk.values.KV
import org.apache.commons.math3.primes.Primes
import redis.clients.jedis.Jedis
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream


fun main(args: Array<String>) {

  val key    = UUID.randomUUID().toString()
  val options = PipelineOptionsFactory.fromArgs(*args).withValidation().`as`(PrimesOptions::class.java)
  val primeNumbersCount = options.getPrimesCount()

  /*
    The input can be simplified further as per 'primeFreqTable()' in the KotlinTest.
    But here we mimic the case of not knowing exactly the size the data we're about to process, still we want to calculate
    the statistics over a 'window' over the last 'options.getWindowSize()' items (that defaults to 1000 as per requirements)
  */
  val primes = mutableListOf<Int>()
  for (number in 1 until Int.MAX_VALUE) {
    if (Primes.isPrime(number)) {
      primes.add(number)
      if (primes.size == primeNumbersCount)
        break
    }
  }

  val pipe    = Pipeline.create(options)
  pipe.apply(Create.of(primes))
    .apply(Combine.globally(AccStateReducer(options.getWindowSize())))
    .apply(ParDo.of(object: DoFn<Array<IntArray>, KV<String, String>>() {
      @ProcessElement @Suppress("unused")
      fun processElement(@Element matrix: Array<IntArray>, out: OutputReceiver<KV<String, String>>) {
        out.output(KV.of(key, dump(matrix)))
      }
    }))
    .apply(RedisIO.write().withEndpoint(options.getRedisHost(), options.getRedisPort()))

  pipe.run().waitUntilFinish()

  println(Jedis("localhost", options.getRedisPort()).get(key))

}

/*
  A function that does the computations in-memory and prints the result to stdout.
*/
fun primeFreqTable() {
  val primes = IntStream.range(0, Int.MAX_VALUE)
    .filter(Primes::isPrime)
    .limit(100003)
    .boxed()
    .collect(Collectors.toList())
    .reversed()
    .take(1000)

  val matrix = Array(10) {  IntArray(10) {0} }
  primes.fold(matrix, ::acceptPrime)
  println(dump(matrix))
}
