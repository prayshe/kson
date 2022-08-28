package com.morsecode

import kotlin.math.pow

class Json(private var jsonString: String, private var offset: Int = 0) {
  fun parse(): HashMap<String, Any> {
    checkNext('{')
    return parseObject()
  }

  private fun parseObject(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    while (nextNonWSChar() == '"') {
      val key = nextString()
      checkNext(':')
      map[key] = parseValue()
      if (nextNonWSChar() != ',') {
        break
      }
    }
    --offset
    checkNext('}')
    return map
  }

  private fun parseArray(): Array<Any> {
    val list = ArrayList<Any>()
    do {
      list.add(parseValue())
    } while (nextNonWSChar() == ',')
    --offset
    checkNext(']')
    return list.toArray()
  }

  private fun parseValue(): Any {
    return when (nextNonWSChar()) {
      '"' -> {
        nextString()
      }
      in '0'..'9', '-' -> {
        --offset
        nextNumber()
      }
      '{' -> {
        parseObject()
      }
      '[' -> {
        parseArray()
      }
      't' -> {
        if (jsonString[offset++] != 'r' || jsonString[offset++] != 'u' || jsonString[offset++] != 'e') {
          throw IllegalArgumentException("Invalid true")
        }
        true
      }
      'f' -> {
        if (jsonString[offset++] != 'a' || jsonString[offset++] != 'l' || jsonString[offset++] != 's' || jsonString[offset++] != 'e') {
          throw IllegalArgumentException("Invalid false")
        }
        false
      }
      else -> throw IllegalArgumentException("Invalid json value")
    }
  }

  private fun nextNonWSChar(): Char {
    while (offset < jsonString.length && jsonString[offset] in " \r\n\t") {
      ++offset
    }
    if (offset == jsonString.length) {
      throw NoSuchElementException("offset reached end")
    }
    return jsonString[offset++]
  }

  private fun <T> nextValue(
    predicate: (Char) -> Boolean,
    initial: T,
    reduce: (T, Char) -> T,
  ): T {
    var value = initial
    while (offset < jsonString.length && predicate(jsonString[offset])) {
      value = reduce(value, jsonString[offset++])
    }
    if (offset == jsonString.length) {
      throw NoSuchElementException("offset reached end")
    }
    return value
  }

  private fun nextString(): String {
    val stringBuilder = nextValue({ it != '"' }, StringBuilder(""), StringBuilder::append)
    ++offset
    return stringBuilder.toString()
  }

  private fun nextNumber(): Double {
    fun nextSign(): Int {
      return when (jsonString[offset++]) {
        '+' -> 1
        '-' -> -1
        in '0'..'9' -> {
          --offset
          1
        }
        else -> {
          throw IllegalArgumentException("Invalid number")
        }
      }
    }
    val sign = nextSign()
    val integer = nextValue({ it in '0'..'9' }, 0.0) { v, c ->
      v * 10 + c.digitToInt()
    }
    var fraction = 0.0
    var exponent = 0
    if (jsonString[offset] == '.') {
      ++offset
      fraction = nextValue({ it in '0'..'9' }, Pair(0.0, 10.0)) { v, c ->
        Pair(v.first + c.digitToInt() / v.second, v.second * 10.0)
      }.first
    }
    if (jsonString[offset].uppercaseChar() == 'E') {
      ++offset
      exponent = nextSign() * nextValue({ it in '0'..'9' }, 0) { v, c ->
        v * 10 + c.digitToInt()
      }
    }
    return sign * (integer + fraction) * 10.0.pow(exponent)
  }

  private fun checkNext(e: Char) {
    if (nextNonWSChar() != e) {
      throw NoSuchElementException("offset [$offset] should be $e")
    }
  }
}