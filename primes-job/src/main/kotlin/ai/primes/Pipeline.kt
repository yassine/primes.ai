package ai.primes

import org.apache.beam.sdk.transforms.Combine
import java.io.Serializable
import java.util.*


class AccState: Serializable {

  var list: LinkedList<Int> = LinkedList<Int>()
  var state: Array<IntArray> = Array(10) {  IntArray(10) { 0 } }

  fun mergeWith(other: AccState) : AccState {
    if (this != other) {
      for ( i in other.state.indices)
        for ( j in other.state[i].indices)
          state[i][j] += other.state[i][j]
      val combined = (list + other.list).toSet().toList().sorted()
      if (combined.size > 1000) {
        list = LinkedList(combined.subList(combined.size - 1000, combined.size))
        combined.subList(0, combined.size - 1000)
          .forEach { reversePrimeEffect(state, it) }
      }
    }
    return this
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AccState

    if ( list != other.list )
      return false

    return true
  }

  override fun hashCode(): Int
    = state.contentDeepHashCode()


}

class AccStateReducer(private val threshold: Int) : Combine.CombineFn<Int, AccState, Array<IntArray>>() {

  override fun addInput(acc: AccState, input: Int): AccState {
    acc.list.add(input)
    acceptPrime(acc.state, input)
    if ( acc.list.size > threshold )
      reversePrimeEffect(acc.state, acc.list.removeFirst())
    return acc
  }

  override fun createAccumulator(): AccState
    = AccState()

  override fun mergeAccumulators(accumulators: MutableIterable<AccState>): AccState
    = accumulators.fold(AccState()) { acc, cur -> cur.mergeWith(acc) }

  override fun extractOutput(accumulator: AccState): Array<IntArray>
    = accumulator.state

}

fun acceptPrime(matrix: Array<IntArray>, number: Int): Array<IntArray> {
  var modulo: Int
  var n        = number
  var position = 0
  while (n != 0) {
    modulo = n % 10
    matrix[position][modulo]++
    n = (n - modulo) / 10
    position++
  }
  return matrix
}

fun reversePrimeEffect(matrix: Array<IntArray>, number: Int): Array<IntArray> {
  var modulo: Int
  var n        = number
  var position = 0
  while (n != 0) {
    modulo = n % 10
    matrix[position][modulo]--
    n = (n - modulo) / 10
    position++
  }
  return matrix
}
