package im.tox.antox.fragments

import im.tox.antox.R
import im.tox.antox.adapters.LeftPaneAdapter
import im.tox.antox.utils.LeftPaneItem
import im.tox.antox.wrapper._

class RecentFragment extends AbstractContactsFragment(showSearch = false, showFab = false) {

  override def updateContacts(contactInfoTuple: (Array[FriendInfo], Array[FriendRequest],
    Array[GroupInvite], Array[GroupInfo])) {
    contactInfoTuple match {
      case (friendsList, friendRequests, groupInvites, groupList) =>
        leftPaneAdapter = new LeftPaneAdapter(getActivity)
        updateContactsLists(leftPaneAdapter, friendsList ++ groupList)

        contactsListView.setAdapter(leftPaneAdapter)
    }
  }

  def updateContactsLists(leftPaneAdapter: LeftPaneAdapter, contactList: Array[ContactInfo]): Unit = {
    val sortedContactList = contactList.sortWith(compareLastMessageTimestamp).sortWith(compareNames)
    if (sortedContactList.length > 0) {
      for (contact <- sortedContactList) {
        val itemType = if (contact.isInstanceOf[GroupInfo]) {
          ContactItemType.GROUP
        } else {
          ContactItemType.FRIEND
        }

        val contactPaneItem = new LeftPaneItem(itemType, contact.key, contact.name, contact.statusMessage,
          contact.online, UserStatus.getToxUserStatusFromString(contact.status), contact.unreadCount,
          contact.lastMessageTimestamp)
        leftPaneAdapter.addItem(contactPaneItem)
      }
    }
  }

  def compareLastMessageTimestamp(a: ContactInfo, b: ContactInfo): Boolean = {
    a.lastMessageTimestamp.after(b.lastMessageTimestamp)
  }
}
