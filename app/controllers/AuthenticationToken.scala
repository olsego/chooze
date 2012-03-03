package controllers

import play.api.mvc.{RequestHeader, PlainResult, Controller}
import java.util.UUID
import play.api.data.Form

trait AuthenticationToken {
  this: Controller =>

  private val AUTH_TOKEN = "auth_token"

  /**
   * Pre-fill a form with a generated token
   * @param form Form to fill with the authenticity token
   * @param key Key of the field which will contain the token
   * @param result Continuation computing the result using the prefilled form
   */
  def authenticationToken[A](form: Form[A], key: String)(result: Form[A] => PlainResult)(implicit request: RequestHeader): PlainResult = {
    val token = UUID.randomUUID().toString
    result(form.bind(Map(key -> token)).copy(errors = Nil)).withSession(AUTH_TOKEN -> token)
  }

  /**
   * Check the authenticity of a given token against the token found in the session
   * @return Some(token) if there is a token in the session and it has the same value as the token param, otherwise None
   */
  def checkAuthenticity(token: String)(implicit request: RequestHeader): Option[String] = {
    for {
      authToken <- session.get(AUTH_TOKEN)
      if token == authToken
    } yield token
  }
}