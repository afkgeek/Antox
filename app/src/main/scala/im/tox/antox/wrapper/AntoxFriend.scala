package im.tox.antox.wrapper

import java.util

import im.tox.tox4j.core.enums.ToxStatus

import scala.beans.{BeanProperty, BooleanBeanProperty}
//remove if not needed

class AntoxFriend(friendnumber: Int) {

  @transient private val friendNumber: Int = friendnumber

  @BeanProperty
  var name: String = _

  @BeanProperty
  var key: String = _

  @BeanProperty
  var status: ToxStatus = ToxStatus.NONE

  @BeanProperty
  var statusMessage: String = _

  @BooleanBeanProperty
  var online: Boolean = false

  var isTyping: Boolean = _

  @BeanProperty
  var nickname: String = _

  @BeanProperty
  var previousNames: util.ArrayList[String] = _

  def getFriendnumber: Int = this.friendNumber

  def setTyping(isTyping: Boolean) {
    this.isTyping = isTyping
  }
}
