package ai.primes

import org.apache.beam.runners.flink.FlinkPipelineOptions
import org.apache.beam.sdk.options.Description
import org.apache.beam.sdk.options.Validation

interface PrimesOptions : FlinkPipelineOptions {

  @Validation.Required
  @Description("The window size of prime numbers to use for the digits distribution")
  fun getWindowSize(): Int
  fun setWindowSize(value: Int)

  @Validation.Required
  @Description("The number of prime numbers to generate")
  fun getPrimesCount(): Int
  fun setPrimesCount(value: Int)


  @Validation.Required
  @Description("The port number for Redis")
  fun getRedisPort(): Int
  fun setRedisPort(value: Int)


  @Validation.Required
  @Description("Redis host")
  fun getRedisHost(): String
  fun setRedisHost(value: String)
}
