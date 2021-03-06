package im.tox.antox.utils

object Constants {

  val START_TOX = "im.tox.antox.START_TOX"

  val STOP_TOX = "im.tox.antox.STOP_TOX"

  val BROADCAST_ACTION = "im.tox.antox.BROADCAST"

  val SWITCH_TO_FRIEND = "im.tox.antox.SWITCH_TO_FRIEND"

  val UPDATE = "im.tox.antox.UPDATE"

  val DOWNLOAD_DIRECTORY = "Tox Received Files"

  val PROFILE_EXPORT_DIRECTORY = "Tox Exported Profiles"

  val DATABASE_VERSION = 5

  val TABLE_FRIENDS = "friends"

  val TABLE_GROUPS = "groups"

  val TABLE_CHAT_LOGS = "messages"

  val TABLE_FRIEND_REQUESTS = "friend_requests"

  val TABLE_GROUP_INVITES = "group_invites"

  val COLUMN_NAME_KEY = "tox_key"

  val COLUMN_NAME_GROUP_INVITER = "group_inviter"

  val COLUMN_NAME_GROUP_DATA = "group_data"

  val COLUMN_NAME_SENDER_NAME = "sender_name"

  val COLUMN_NAME_MESSAGE = "message"

  val COLUMN_NAME_NAME = "name"

  val COLUMN_NAME_TOPIC = "topic"

  val COLUMN_NAME_USERNAME = "username"

  val COLUMN_NAME_TIMESTAMP = "timestamp"

  val COLUMN_NAME_NOTE = "note"

  val COLUMN_NAME_STATUS = "status"

  val COLUMN_NAME_MESSAGE_ID = "message_id"

  val COLUMN_NAME_HAS_BEEN_RECEIVED = "has_been_received"

  val COLUMN_NAME_HAS_BEEN_READ = "has_been_read"

  val COLUMN_NAME_SUCCESSFULLY_SENT = "successfully_sent"

  val COLUMN_NAME_ISONLINE = "isonline"

  val COLUMN_NAME_ISCONNECTED = "isconnected"

  val COLUMN_NAME_ALIAS = "alias"

  val COLUMN_NAME_IGNORED = "ignored"

  val COLUMN_NAME_ISBLOCKED = "isblocked"

  val ADD_FRIEND_REQUEST_CODE = 0

  val WELCOME_ACTIVITY_REQUEST_CODE = 3

  val IMAGE_RESULT = 0

  val PHOTO_RESULT = 1

  val FILE_RESULT = 3

  var epoch: Long = _

  val UNREAD_COUNT_LIMIT = 99

  val MAX_NAME_LENGTH = 128
}
