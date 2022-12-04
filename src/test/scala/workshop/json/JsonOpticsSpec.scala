package workshop.json

import io.circe.optics.JsonPath.root
import io.circe.{Json, parser}
import zio.Scope
import zio.test.TestAspect.ignore
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object JsonOpticsSpec extends ZIOSpecDefault {

  val json: Json = parser.parse("""
    {
      "order": {
        "customer": {
          "name": "Custy McCustomer",
          "contactDetails": {
            "address": "1 Fake Street, London, England",
            "phone": "0123-456-789"
          }
        },
        "items": [{
          "id": 123,
          "description": "banana",
          "quantity": 1
        }, {
          "id": 456,
          "description": "apple",
          "quantity": 2
        }],
        "total": 123.45
      }
    }
    """).getOrElse(Json.Null)

  val expectedJson: Json = parser.parse("""
  {
    "order": {
      "customer": {
        "name": "Custy McCustomer",
        "contactDetails": {
          "address": "1 Fake Street, London, England",
          "phone": "0123-456-789"
        }
      },
      "items": [{
        "id": 123,
        "description": "banana",
        "quantity": 2
      }, {
        "id": 456,
        "description": "apple",
        "quantity": 4
      }],
      "total": 123.45
    }
  }
  """).getOrElse(Json.Null)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("JsonOpticsSpec")(
      test("modify each item to be locked") {
        val lockItems: Json => Json =
          root.order.items.each.quantity.int.modify(_ * 2)
        assertTrue(lockItems(json) == expectedJson)
      } @@ ignore
    )
}
