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

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.play.http.HttpVerbs.{PUT => PUT_VERB}
import uk.gov.hmrc.play.http.hooks.HttpHooks
import uk.gov.hmrc.play.http.logging.ConnectionTracing

import scala.concurrent.{ExecutionContext, Future}


trait HttpPut extends CorePut with HttpTransport with HttpVerb with ConnectionTracing with HttpHooks {

  override def put[I](url: String, body: I)(implicit wts: Writes[I], hc: HeaderCarrier, ec:ExecutionContext): Future[HttpResponse] = {
    withTracing(PUT_VERB, url) {
      val httpResponse = doPut(url, body)
      executeHooks(url, PUT_VERB, Option(Json.stringify(wts.writes(body))), httpResponse)
      mapErrors(PUT_VERB, url, httpResponse)
    }
  }
}