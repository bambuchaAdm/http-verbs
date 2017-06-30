/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.http

import java.net.URLEncoder

import uk.gov.hmrc.play.http.HttpVerbs.{GET => GET_VERB}
import uk.gov.hmrc.play.http.hooks.HttpHooks
import uk.gov.hmrc.play.http.logging.ConnectionTracing

import scala.concurrent.{ExecutionContext, Future}

trait HttpGet extends CoreGet with HttpTransport with HttpVerb with ConnectionTracing with HttpHooks {

  override def get(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =withTracing(GET_VERB, url) {
    val httpResponse = doGet(url)
    executeHooks(url, GET_VERB, None, httpResponse)
    mapErrors(GET_VERB, url, httpResponse)
  }

  override def get(url: String, queryParams: Seq[(String, String)])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val queryString = makeQueryString(queryParams)
    if (url.contains("?")) {
      throw new UrlValidationException(url, s"${this.getClass}.GET(url, queryParams)", "Query parameters must be provided as a Seq of tuples to this method")
    }
    get(url + queryString)
  }

  private def makeQueryString(queryParams: Seq[(String,String)]) = {
    val paramPairs = queryParams.map(Function.tupled((k, v) => s"$k=${URLEncoder.encode(v, "utf-8")}"))
    val params = paramPairs.mkString("&")

    if (params.isEmpty) "" else s"?$params"
  }
}