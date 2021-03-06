package im.tox.antox.wrapper

import java.sql.Timestamp

import im.tox.antox.data.AntoxDB
import im.tox.antox.tox.ToxSingleton

import scala.collection.JavaConversions._

class Group(val id: String,
            val groupNumber: Int,
            private var _name: String,
            var alias: String,
            var topic: String,
            val peers: PeerList) {

  var connected = false

  def addPeer(tox: ToxCore, peerNumber: Int): Unit = {
    var peerName = tox.getGroupPeerName(groupNumber, peerNumber)
    if (peerName == null) peerName = ""
    this.peers.addGroupPeer(new GroupPeer(peerName, ignored = false))
    printPeerList()
  }

  def printPeerList(): Unit = {
    println("peer list: ")
    var number = 0
    for (peer <- peers.all()) {
      println("peer " + number + " with name " + peer.name)
      number += 1
    }
  }

  def getPeerCount: Int = {
    peers.all().size()
  }

  def clearPeerList(): Unit = {
    peers.clear()
  }

  def leave(partMessage: String): Unit = {
    ToxSingleton.tox.deleteGroup(groupNumber, partMessage)
  }

  override def toString: String = name

  //Getters
  def name = _name

  //Setters
  def name_= (name: String): Unit = {
    _name = name
  }
}
