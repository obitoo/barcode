package org.bullock.barcode3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Info extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.info);
      
     // passed from parent
     Bundle bundle = getIntent().getExtras();
     String string1 =bundle.getString("string1");
      
      
	 TextView infotext = (TextView)findViewById(R.id.info_content);
	 infotext.append("\n"+string1);
      
      
   }
}
