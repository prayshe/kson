package com.morsecode

import com.morsecode.Json
import org.junit.Test

import org.junit.Assert.*

class JsonParserUnitTest {
  @Test
  fun parse_double() {
    assertEquals(hashMapOf("number" to 3.1415926), Json("""
      {
        "number": 3.1415926
      }
    """).parse())
  }

  @Test
  fun parse_integer() {
    assertEquals(hashMapOf("number" to 31415926.0), Json("""
      {
        "number": 31415926
      }
    """).parse())
  }

  @Test
  fun parse_integerZero() {
    assertEquals(hashMapOf("number" to 0.0), Json("""
      {
        "number": 0
      }
    """).parse())
  }

  @Test
  fun parse_doubleZero() {
    assertEquals(hashMapOf("number" to 0.0), Json("""
      {
        "number": 0.0
      }
    """).parse())
  }

  @Test
  fun parse_doubleExponent() {
    assertEquals(hashMapOf("number" to 314.0), Json("""
      {
        "number": 3.14e2
      }
    """).parse())
  }

  @Test
  fun parse_noFractionExponent() {
    assertEquals(hashMapOf("number" to 31400.0), Json("""
      {
        "number": 314E+2
      }
    """).parse())
  }

  @Test
  fun parse_doubleNegativeExponent() {
    assertEquals(0.0314, Json("""
      {
        "number": 3.14e-2
      }
    """).parse()["number"] as Double, 1e-9)
  }

  @Test
  fun parse_string() {
    assertEquals(hashMapOf("this is string key" to " this is value "), Json("""
      {
        "this is string key": " this is value "
      }
    """).parse())
  }

  @Test
  fun parse_childObject() {
    assertEquals(hashMapOf("name" to "node", "child" to hashMapOf("x" to 123.0, "y" to "456", "z" to HashMap<String, Any>(), "w" to HashMap<String, Any>())),
                 Json("""
      {
        "name" : "node" ,
        "child": {
          "x" : 123,
          "y" : "456",
          "z": {},
          "w": {}
        }
      }
    """).parse())
  }

  @Test
  fun parse_emptyObject() {
    assertEquals(hashMapOf("name" to HashMap<String, Any>(), "value" to HashMap<String, Any>()),
                 Json("""
      {
        "name" : {},
        "value": {}
      }
    """).parse())
  }

  @Test
  fun parse_array() {
    assertArrayEquals(arrayOf(hashMapOf("x" to 103.0, "y" to 102.0), "abc", 1.0),
                      Json("""
      {
        "name" : [
          {
            "x": 103,
            "y": 102
          },
          "abc",
          1
        ]
      }
    """).parse()["name"] as Array<Any>)
  }

  @Test
  fun parse_boolean() {
    assertEquals(hashMapOf("x" to true, "y" to false, "z" to true),
                 Json("""
      {
        "x" : true,
        "y": false,
        "z":true}
    """).parse())
  }

  @Test
  fun parse_gltf() {
    val gltf = Json("""
    {
      "scene": 0,
      "scenes" : [
        {
          "nodes" : [ 0 ]
        }
      ],
      
      "nodes" : [
        {
          "mesh" : 0
        }
      ],
      
      "meshes" : [
        {
          "primitives" : [ {
            "attributes" : {
              "POSITION" : 1
            },
            "indices" : 0
          } ]
        }
      ],
    
      "buffers" : [
        {
          "uri" : "data:application/octet-stream;base64",
          "byteLength" : 44
        }
      ],
      "bufferViews" : [
        {
          "buffer" : 0,
          "byteOffset" : 0,
          "byteLength" : 6,
          "target" : 34963
        },
        {
          "buffer" : 0,
          "byteOffset" : 8,
          "byteLength" : 36,
          "target" : 34962
        }
      ],
      "accessors" : [
        {
          "bufferView" : 0,
          "byteOffset" : 0,
          "componentType" : 5123,
          "count" : 3,
          "type" : "SCALAR",
          "max" : [ 2 ],
          "min" : [ 0 ]
        },
        {
          "bufferView" : 1,
          "byteOffset" : 0,
          "componentType" : 5126,
          "count" : 3,
          "type" : "VEC3",
          "max" : [ 1.0, 1.0, 0.0 ],
          "min" : [ 0.0, 0.0, 0.0 ]
        }
      ],
      
      "asset" : {
        "version" : "2.0"
      }
    }    """).parse()
    assertEquals(44.0, ((gltf["buffers"] as Array<Any>)[0] as HashMap<String, Any>)["byteLength"])
  }
}