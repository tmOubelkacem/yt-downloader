package otm.mine.youtube.model

import play.api.libs.json.Json

object Model {

  case class PageInfo(
                       totalResults: Double,
                       resultsPerPage: Double
                     )

  case class Default(
                      url: String,
                      width: Double,
                      height: Double
                    )

  case class Thumbnails(
                         default: Default,
                         medium: Default,
                         high: Default
                       )

  case class ResourceId(
                         kind: String,
                         videoId: String
                       )

  case class Snippet(
                      publishedAt: String,
                      channelId: String,
                      title: String,
                      description: String,
                      thumbnails: Thumbnails,
                      channelTitle: String,
                      playlistId: String,
                      position: Double,
                      resourceId: ResourceId
                    )

  case class ContentDetails(
                             videoId: String,
                             videoPublishedAt: String
                           )

  case class Item(
                    kind: String,
                    etag: String,
                    id: String,
                    snippet: Snippet,
                    contentDetails: ContentDetails
                  )

  case class PlaylistItemListResponse(
                                       kind: String,
                                       etag: String,
                                       nextPageToken: Option[String],
                                       pageInfo: PageInfo,
                                       items: List[Item]
                                     )

  implicit val pageInfoReads = Json.reads[PageInfo]
  implicit val defaultReads = Json.reads[Default]
  implicit val resourceIdReads = Json.reads[ResourceId]
  implicit val thumbnailsReads = Json.reads[Thumbnails]
  implicit val snippetReads = Json.reads[Snippet]
  implicit val contentDetailsReads = Json.reads[ContentDetails]
  implicit val itemReads = Json.reads[Item]
  implicit val playlistItemsReads = Json.reads[PlaylistItemListResponse]
}

