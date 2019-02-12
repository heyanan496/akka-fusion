package fusion.http.server

import java.time.{LocalDate, LocalDateTime, LocalTime}

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.{FromStringUnmarshaller, Unmarshaller}
import helloscala.util.TimeUtils

trait HttpDirectives {
  implicit def localDateFromStringUnmarshaller: FromStringUnmarshaller[LocalDate] =
    HttpDirectives._localDateFromStringUnmarshaller
  implicit def localTimeFromStringUnmarshaller: FromStringUnmarshaller[LocalTime] =
    HttpDirectives._localTimeFromStringUnmarshaller
  implicit def localDateTimeFromStringUnmarshaller: FromStringUnmarshaller[LocalDateTime] =
    HttpDirectives._localDateTimeFromStringUnmarshaller

  def curlLogging(logger: com.typesafe.scalalogging.Logger): Directive0 =
    mapRequest { req =>
      def entity = req.entity match {
        case HttpEntity.Empty => ""
        case _                => "\n" + req.entity
      }

      logger.debug(s"""
                      |method: ${req.method.value}
                      |uri: ${req.uri}
                      |search: ${req.uri.rawQueryString}
                      |header: ${req.headers.mkString("\n        ")}$entity""".stripMargin)
      req
    }

}

object HttpDirectives extends HttpDirectives {

  private val _localDateFromStringUnmarshaller =
    Unmarshaller.strict[String, LocalDate](str => LocalDate.parse(str, TimeUtils.formatterDate))

  private val _localTimeFromStringUnmarshaller =
    Unmarshaller.strict[String, LocalTime](str => LocalTime.parse(str, TimeUtils.formatterTime))

  private val _localDateTimeFromStringUnmarshaller =
    Unmarshaller.strict[String, LocalDateTime](str => LocalDateTime.parse(str, TimeUtils.formatterDateTime))

}