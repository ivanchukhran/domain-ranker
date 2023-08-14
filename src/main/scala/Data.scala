import StringUtils.tabbedString

case object Data {
  case class Category(id: String, displayName: String, isPredicted: Boolean)

  case class Consumer(id: String, displayName: String, isVerified: Boolean) {
    override def toString: String = s"id: $id,  displayName: $displayName,  isVerified: $isVerified"
  }

  case class Date(createdAt: String) {
    override def toString: String = s"createdAt: $createdAt"
  }

  case class ParentCategory(id: String, displayName: String, subcategories: Seq[Subcategory])

  case class Domain(id: String,
                    identifyingName: String,
                    numberOfReviews: Int,
                    stars: Float,
                    trustScore: Float,
                    categories: Seq[Category],
                    latestReview: Option[Review] = None)

  case class DomainStats(domainId: String,
                         identifyingName: String,
                         latestReview: Option[Review],
                         traffic: Option[Int],
                         numberOfReviews: Int,
                         latestReviewCount: Int = 0) {
    override def toString: String = {
      val toBeTabbed = {
        s"""|  latestReview: ${tabbedString(latestReview.getOrElse("").toString, 2)}
            |  traffic: ${traffic.getOrElse(0)},
            |  numberOfReviews: $numberOfReviews,
            |  latestReviewCount: $latestReviewCount""".stripMargin
      }
      s"""|  $identifyingName
          |  ${tabbedString(toBeTabbed, 1)}
          |  """.stripMargin
    }
  }

  case class Review(id: String, text: String, rating: Float, date: Date, consumer: Consumer) {
    override def toString: String =
      s"""
         |  id: $id, rating: $rating, $date, consumer: ${consumer.displayName}
         |  text: ${text.split("\\s+").grouped(20).map(_.mkString(" ")).mkString("", "\n", "")}"""
  }

  case class Subcategory(id: String, displayName: String)
}
