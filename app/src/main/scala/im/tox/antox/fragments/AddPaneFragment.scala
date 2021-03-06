package im.tox.antox.fragments

import java.util

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.{Fragment, FragmentManager, FragmentPagerAdapter}
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{TextView, ImageView}
import com.astuetz.PagerSlidingTabStrip
import com.astuetz.PagerSlidingTabStrip.CustomTabProvider
import com.balysv.materialripple.MaterialRippleLayout
import im.tox.antox.R
import im.tox.antox.pager.BetterFragmentPagerAdapter

class AddPaneFragment extends Fragment {

  var pager: ViewPager = _

  class AddPagerAdapter(fm: FragmentManager) extends BetterFragmentPagerAdapter(fm) with CustomTabProvider {

    val ICONS: Array[Int] = Array(R.drawable.ic_action_contacts_tab, R.drawable.ic_action_add_group)
    val LABELS: Array[String] = Array(getResources.getString(R.string.addpane_friend_label),
                                      getResources.getString(R.string.addpane_group_label))

    override def getCustomTabView(parent: ViewGroup, position: Int): View = {
         val materialRippleLayout: MaterialRippleLayout = LayoutInflater.from(getActivity)
            .inflate(R.layout.custom_tab, parent, false).asInstanceOf[MaterialRippleLayout]
         materialRippleLayout.findViewById(R.id.image).asInstanceOf[ImageView].setImageResource(ICONS(position))
         materialRippleLayout.findViewById(R.id.text).asInstanceOf[TextView].setText(LABELS(position))
         materialRippleLayout
    }

    override def getPageTitle(position: Int): CharSequence = {
      position match {
        case 0 => return LABELS(0)
        case _ => return LABELS(1)
      }

      null
    }

    override def getItem(pos: Int): Fragment = pos match {
      case 0 => new AddFriendFragment()
      case _ => new AddGroupFragment()
    }

    override def getCount: Int = 2
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val rootView = inflater.inflate(R.layout.fragment_pane, container, false)
    pager = rootView.findViewById(R.id.pager).asInstanceOf[ViewPager]
    val tabs = rootView.findViewById(R.id.pager_tabs).asInstanceOf[PagerSlidingTabStrip]

    pager.setAdapter(new AddPagerAdapter(getFragmentManager))
    tabs.setViewPager(pager)

    rootView
  }

  def getSelectedFragment: Fragment = {
    pager.getAdapter.asInstanceOf[AddPagerAdapter].getActiveFragment(pager, pager.getCurrentItem)
  }
}
