import Data._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case object JsonSerializers {

  implicit val dateReads: Reads[Date] = (JsPath \ "createdAt").read[String].map(Date.apply)

  implicit val consumerReads: Reads[Consumer] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "displayName").read[String] and
      (JsPath \ "isVerified").read[Boolean])(Consumer.apply _)

  implicit val categoryReads: Reads[Category] =
    ((JsPath \ "categoryId").read[String] and
      (JsPath \ "displayName").read[String] and
      (JsPath \ "isPredicted").read[Boolean])(Category.apply _)

  implicit val subcategoryReads: Reads[Subcategory] =
    ((JsPath \ "categoryId").read[String] and
      (JsPath \ "displayName").read[String])(Subcategory.apply _)

  implicit val parentCategoryReads: Reads[ParentCategory] =
    ((JsPath \ "categoryId").read[String] and
      (JsPath \ "displayName").read[String] and
      (JsPath \ "subCategories").read[Seq[Subcategory]]
      )(ParentCategory.apply _)


  implicit val parsedReviewReads: Reads[Review] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "text").read[String] and
      (JsPath \ "rating").read[Float] and
      (JsPath \ "date").read[Date](dateReads) and
      (JsPath \ "consumer").read[Consumer]
      )(Review.apply _)

  implicit val parsedDomainReads: Reads[Domain] =
    ((JsPath \ "businessUnitId").read[String] and
      (JsPath \ "identifyingName").read[String] and
      (JsPath \ "numberOfReviews").read[Int] and
      (JsPath \ "stars").read[Float] and
      (JsPath \ "trustScore").read[Float] and
      (JsPath \ "categories").read[Seq[Category]] and
      (JsPath \ "review").readNullableWithDefault(None)(parsedReviewReads)
      )(Domain.apply _)

  implicit val domainStatsReads: Reads[DomainStats] =
    ((JsPath \ "domainId").read[String] and
      (JsPath \ "identifyingName").read[String] and
      (JsPath \ "latestReview").readNullableWithDefault(None)(parsedReviewReads) and
      (JsPath \ "traffic").readNullable[Int] and
      (JsPath \ "numberOfReviews").read[Int] and
      (JsPath \ "latestReviewCount").read[Int]
      )(DomainStats.apply _)

  implicit val dateWrites: Writes[Date] = (date: Date) => Json.obj(
    "createdAt" -> date.createdAt
  )

  implicit val consumerWrites: Writes[Consumer] = (consumer: Consumer) => Json.obj(
    "id" -> consumer.id,
    "displayName" -> consumer.displayName,
    "isVerified" -> consumer.isVerified
  )

  implicit val reviewWrites: Writes[Review] = (review: Review) => Json.obj(
    "id" -> review.id,
    "text" -> review.text,
    "rating" -> review.rating,
    "date" -> review.date,
    "consumer" -> review.consumer
  )

  implicit val domainStatsWrites: Writes[DomainStats] = (domainStats: DomainStats) => Json.obj(
    "domainId" -> domainStats.domainId,
    "identifyingName" -> domainStats.identifyingName,
    "latestReview" -> domainStats.latestReview,
    "traffic" -> domainStats.traffic,
    "numberOfReviews" -> domainStats.numberOfReviews,
    "latestReviewCount" -> domainStats.latestReviewCount
  )
}
