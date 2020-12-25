/*
 * Copyright 2020 Feng Hsien Hsu, Siao Syuan Yang, Wei-Qi Wang, Ya-Han Tsai, Yu Hao Wu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tw.ktrssreader.kotlin.constant

object ParserConst {
    const val ITUNES = "itunes"
    const val GOOGLE_PLAY = "googleplay"

    const val CHANNEL = "channel"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val IMAGE = "image"
    const val LANGUAGE = "language"
    const val CATEGORY = "category"
    const val LINK = "link"
    const val COPYRIGHT = "copyright"
    const val MANAGING_EDITOR = "managingEditor"
    const val WEB_MASTER = "webMaster"
    const val PUB_DATE = "pubDate"
    const val LAST_BUILD_DATE = "lastBuildDate"
    const val GENERATOR = "generator"
    const val DOCS = "docs"
    const val CLOUD = "cloud"
    const val TTL = "ttl"
    const val RATING = "rating"
    const val TEXT_INPUT = "textInput"
    const val SKIP_HOURS = "skipHours"
    const val SKIP_DAYS = "skipDays"
    const val ITEM = "item"
    const val URL = "url"
    const val DOMAIN = "domain"
    const val PORT = "port"
    const val PATH = "path"
    const val REGISTER_PROCEDURE = "registerProcedure"
    const val PROTOCOL = "protocol"
    const val NAME = "name"
    const val ENCLOSURE = "enclosure"
    const val GUID = "guid"
    const val AUTHOR = "author"
    const val COMMENTS = "comments"
    const val SOURCE = "source"
    const val LENGTH = "length"
    const val TYPE = "type"
    const val EXPLICIT = "explicit"
    const val OWNER = "owner"
    const val BLOCK = "block"
    const val PERMALINK = "isPermaLink"
    const val HOUR = "hour"
    const val DAY = "day"
    const val HEIGHT = "height"
    const val WIDTH = "width"
    const val HREF = "href"
    const val TEXT = "text"
    const val EMAIL = "email"

    const val ITUNES_IMAGE = "$ITUNES:$IMAGE"
    const val ITUNES_EXPLICIT = "$ITUNES:$EXPLICIT"
    const val ITUNES_CATEGORY = "$ITUNES:$CATEGORY"
    const val ITUNES_AUTHOR = "$ITUNES:$AUTHOR"
    const val ITUNES_OWNER = "$ITUNES:$OWNER"
    const val ITUNES_NAME = "$ITUNES:$NAME"
    const val ITUNES_EMAIL = "$ITUNES:$EMAIL"
    const val ITUNES_TITLE = "$ITUNES:$TITLE"
    const val ITUNES_TYPE = "$ITUNES:$TYPE"
    const val ITUNES_NEW_FEED_URL = "$ITUNES:new-feed-url"
    const val ITUNES_BLOCK = "$ITUNES:$BLOCK"
    const val ITUNES_COMPLETE = "$ITUNES:complete"
    const val ITUNES_DURATION = "$ITUNES:duration"
    const val ITUNES_EPISODE = "$ITUNES:episode"
    const val ITUNES_SEASON = "$ITUNES:season"
    const val ITUNES_EPISODE_TYPE = "$ITUNES:episodeType"

    const val GOOGLE_DESCRIPTION = "$GOOGLE_PLAY:$DESCRIPTION"
    const val GOOGLE_IMAGE = "$GOOGLE_PLAY:$IMAGE"
    const val GOOGLE_EXPLICIT = "$GOOGLE_PLAY:$EXPLICIT"
    const val GOOGLE_CATEGORY = "$GOOGLE_PLAY:$CATEGORY"
    const val GOOGLE_AUTHOR = "$GOOGLE_PLAY:$AUTHOR"
    const val GOOGLE_OWNER = "$GOOGLE_PLAY:$OWNER"
    const val GOOGLE_BLOCK = "$GOOGLE_PLAY:$BLOCK"
    const val GOOGLE_EMAIL = "$GOOGLE_PLAY:$EMAIL"
}