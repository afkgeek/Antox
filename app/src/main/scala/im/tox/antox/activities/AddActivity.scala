package im.tox.antox.activities

import android.app.Activity
import android.content.{Context, Intent}
import android.net.Uri
import android.os.{Build, Bundle}
import android.preference.PreferenceManager
import android.support.v4.app.NavUtils
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.ActionBarActivity
import android.util.Log
import android.view.{Menu, MenuItem, View, WindowManager}
import android.widget.{EditText, Toast}
import im.tox.QR.IntentIntegrator
import im.tox.antox.fragments.{InputableID, AddPaneFragment, AddFriendFragment}
import im.tox.antox.R
import im.tox.antox.data.AntoxDB
import im.tox.antox.tox.ToxSingleton
import im.tox.antox.utils.{Constants, Hex}
import im.tox.tox4j.exceptions.ToxException
import org.xbill.DNS.{Lookup, TXTRecord, Type}
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.{AndroidMainThreadScheduler, IOScheduler}
//remove if not needed

class AddActivity extends ActionBarActivity {

  var context: Context = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    overridePendingTransition(R.anim.slide_from_bottom, R.anim.fade_scale_out)

    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN &&
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      getWindow.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    context = getApplicationContext

    setContentView(R.layout.activity_add)

    val intent = getIntent
    if (Intent.ACTION_VIEW == intent.getAction && intent != null) {
      // Handle incoming tox uri links
      var uri: Uri = null
      uri = intent.getData
      if (uri != null) {
        getSupportFragmentManager.findFragmentById(R.id.fragment_add_pane)
          .asInstanceOf[AddPaneFragment].getSelectedFragment
      }
    }
  }

  override def onPause() = {
    super.onPause()
    if (isFinishing) overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_bottom)
  }

  private def scanIntent() {
    val integrator = new IntentIntegrator(this)
    integrator.initiateScan()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
    val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
    if (scanResult != null) {
      if (scanResult.getContents != null) {
        getSupportFragmentManager
          .findFragmentById(R.id.fragment_add_pane)
          .asInstanceOf[AddPaneFragment]
          .getSelectedFragment
          .asInstanceOf[InputableID]
          .inputID(scanResult.getContents)
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.add_friend, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        NavUtils.navigateUpFromSameTask(this)
        true

      case R.id.scanFriend => scanIntent()
    }
    super.onOptionsItemSelected(item)
  }
}
