package im.tox.antox.adapters

import java.util.ArrayList

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.{Gravity, LayoutInflater, View, ViewGroup}
import android.widget.Filter.FilterResults
import android.widget.{BaseAdapter, Filter, Filterable, ImageView, TextView}
import im.tox.antox.R
import im.tox.antox.adapters.LeftPaneAdapter._
import im.tox.antox.data.AntoxDB
import im.tox.antox.fragments.ContactItemType
import im.tox.antox.tox.ToxSingleton
import im.tox.antox.utils._

import scala.collection.JavaConversions._

object LeftPaneAdapter {

  private class ViewHolder {

    var firstText: TextView = _

    var secondText: TextView = _

    var icon: TextView = _

    var countText: TextView = _

    var timeText: TextView = _
  }
}

class LeftPaneAdapter(private var context: Context) extends BaseAdapter with Filterable {

  private var mDataOriginal: ArrayList[LeftPaneItem] = new ArrayList[LeftPaneItem]()

  private var mData: ArrayList[LeftPaneItem] = new ArrayList[LeftPaneItem]()

  private var mInflater: LayoutInflater = context.asInstanceOf[Activity].getLayoutInflater

  var mFilter: Filter = _

  def addItem(item: LeftPaneItem) {
    mData.add(item)
    mDataOriginal.add(item)
    notifyDataSetChanged()
  }

  override def getItemViewType(position: Int): Int = {
    val `type` = getItem(position).viewType
    `type`.id
  }

  override def getViewTypeCount: Int = ContactItemType.values.size

  override def getCount: Int = mData.size

  override def getItem(position: Int): LeftPaneItem = mData.get(position)

  def getKey(position: Int): String = getItem(position).key

  override def getItemId(position: Int): Long = position

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    var holder: ViewHolder = null
    var newConvertView: View = convertView
    val `type` = ContactItemType(getItemViewType(position))
    if (newConvertView == null) {
      holder = new ViewHolder()
      `type` match {
        case ContactItemType.FRIEND_REQUEST | ContactItemType.GROUP_INVITE =>
          newConvertView = mInflater.inflate(R.layout.friendrequest_list_item, null)
          holder.firstText = newConvertView.findViewById(R.id.request_key).asInstanceOf[TextView]
          holder.secondText = newConvertView.findViewById(R.id.request_message).asInstanceOf[TextView]

        case ContactItemType.FRIEND | ContactItemType.GROUP =>
          newConvertView = mInflater.inflate(R.layout.contact_list_item, null)
          holder.firstText = newConvertView.findViewById(R.id.contact_name).asInstanceOf[TextView]
          holder.secondText = newConvertView.findViewById(R.id.contact_status).asInstanceOf[TextView]
          holder.icon = newConvertView.findViewById(R.id.icon).asInstanceOf[TextView]
          holder.countText = newConvertView.findViewById(R.id.unread_messages_count).asInstanceOf[TextView]
          holder.timeText = newConvertView.findViewById(R.id.last_message_timestamp).asInstanceOf[TextView]

        case ContactItemType.HEADER =>
          newConvertView = mInflater.inflate(R.layout.header_list_item, null)
          holder.firstText = newConvertView.findViewById(R.id.left_pane_header).asInstanceOf[TextView]


      }
      newConvertView.setTag(holder)
    } else {
      holder = newConvertView.getTag.asInstanceOf[ViewHolder]
    }
    val item = getItem(position)
    holder.firstText.setText(item.first)
    if (`type` != ContactItemType.HEADER) {
      if (item.second != "") holder.secondText.setText(item.second) else holder.firstText.setGravity(Gravity.CENTER_VERTICAL)
    }
    if (`type` == ContactItemType.FRIEND || `type` == ContactItemType.GROUP) {
      if (item.count > 0) {
        holder.countText.setVisibility(View.VISIBLE)
        //limit unread counter to 99
        holder.countText.setText(java.lang.Integer.toString(
          if (item.count > Constants.UNREAD_COUNT_LIMIT) Constants.UNREAD_COUNT_LIMIT else item.count))
      } else {
        holder.countText.setVisibility(View.GONE)
      }
      holder.timeText.setText(TimestampUtils.prettyTimestamp(item.timestamp, false))

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        holder.icon.setBackground(context.getResources.getDrawable(IconColor.iconDrawable(item.isOnline, item.status)))
      } else {
        holder.icon.setBackgroundDrawable(context.getResources.getDrawable(IconColor.iconDrawable(item.isOnline, item.status)))
      }
    }
    if (holder.timeText != null) {
      holder.timeText.setTextColor(context.getResources.getColor(R.color.grey_dark))
    }

    val acceptButton = newConvertView.findViewById(R.id.accept).asInstanceOf[ImageView]
    val rejectButton = newConvertView.findViewById(R.id.reject).asInstanceOf[ImageView]
    val key = item.first

    if (`type` == ContactItemType.FRIEND_REQUEST) {
      createFriendRequestClickHandlers(key, acceptButton, rejectButton)
    } else if (`type` == ContactItemType.GROUP_INVITE) {
      createGroupInviteClickHandlers(key, acceptButton, rejectButton)
    }
    newConvertView
  }

  def createFriendRequestClickHandlers(clientId: String, acceptButton: ImageView, rejectButton: ImageView): Unit = {
    acceptButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View) {
        Log.d("OnClick", "Accepting Friend: " + clientId)
        val db = new AntoxDB(context)
        db.addFriend(clientId, "Friend Accepted", "", "")
        db.deleteFriendRequest(clientId)
        db.close()
        try {
          ToxSingleton.tox.addFriendNoRequest(clientId)
          ToxSingleton.tox.save()
        } catch {
          case e: Exception =>
        }
        ToxSingleton.updateFriendRequests(context)
        ToxSingleton.updateFriendsList(context)
      }
    })
    rejectButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View) {
        Log.d("OnClick", "Rejecting Friend: " + clientId)
        val antoxDB = new AntoxDB(context)
        antoxDB.deleteFriendRequest(clientId)
        antoxDB.close()
        ToxSingleton.updateFriendsList(context)
        ToxSingleton.updateFriendRequests(context)
      }
    })
  }

  def createGroupInviteClickHandlers(groupId: String, acceptButton: ImageView, rejectButton: ImageView): Unit = {
    acceptButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View) {
        Log.d("OnClick", "Joining Group: " + groupId)
        val db = new AntoxDB(context)
        try {
          val inviteData = db.getGroupInvitesList.filter(groupInvite => groupInvite.groupId == groupId).head.data
          println("data length " + inviteData.length)
          ToxSingleton.tox.acceptGroupInvite(inviteData)
          ToxSingleton.tox.save()
        } catch {
          case e: Exception => e.printStackTrace()
        }
        db.addGroup(groupId, IDUtils.trimForUI(groupId), "")
        db.deleteGroupInvite(groupId)
        db.close()
        ToxSingleton.updateGroupList(context)
        ToxSingleton.updateGroupInvites(context)
      }
    })
    rejectButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View) {
        Log.d("OnClick", "Joining Group: " + groupId)
        val antoxDB = new AntoxDB(context)
        antoxDB.deleteGroupInvite(groupId)
        antoxDB.close()
        ToxSingleton.updateGroupList(context)
        ToxSingleton.updateGroupInvites(context)
      }
    })
  }

  override def getFilter(): Filter = {
    if (mFilter == null) {
      mFilter = new Filter() {

        protected override def performFiltering(constraint: CharSequence): FilterResults = {
          var filterResults = new FilterResults()
          if (mDataOriginal != null) {
            if (constraint == "" || constraint == null) {
              filterResults.values = mDataOriginal
              filterResults.count = mDataOriginal.size
            } else {
              mData = mDataOriginal
              var tempList1 = new ArrayList[LeftPaneItem]()
              var tempList2 = new ArrayList[LeftPaneItem]()
              var length = mData.size
              var i = 0
              while (i < length) {
                var item = mData.get(i)
                if (item.first.toUpperCase().startsWith(constraint.toString.toUpperCase())) tempList1.add(item) else if (item.first.toLowerCase().contains(constraint.toString.toLowerCase())) tempList2.add(item)
                i += 1
              }
              tempList1.addAll(tempList2)
              filterResults.values = tempList1
              filterResults.count = tempList1.size
            }
          }
          return filterResults
        }

        protected override def publishResults(contraint: CharSequence, results: FilterResults) {
          mData = results.values.asInstanceOf[ArrayList[LeftPaneItem]]
          if (results.count > 0) {
            notifyDataSetChanged()
          } else {
            notifyDataSetInvalidated()
          }
        }
      }
    }
    mFilter
  }
}
