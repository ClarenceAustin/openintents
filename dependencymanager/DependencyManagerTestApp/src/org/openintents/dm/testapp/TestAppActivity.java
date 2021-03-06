/**
 * This file is part of the Android DependencyManager project hosted at
 * http://code.google.com/p/android-dependencymanager/
 *
 * Copyright (C) 2009,2010 Jens Finkhaeuser <jens@finkhaeuser.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.openintents.dm.testapp;

import org.openintents.dm.client.DMActivity;
import org.openintents.dm.common.DependencyManagerContract;

import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

import android.util.Log;

/**
 * The app's main activity should be derived from DMActivity in order to benefit
 * from DependencyManager with minimal effort. DMActivity binds to
 * DependencyManager in onCreate(), and unbinds onStop(). It's entirely possible
 * for each Activity to derive from DMActivity, but that may well incur overhead
 * you don't need.
 * 
 * After binding occurred, DMActivity tries to resolve this package's
 * dependencies, which - if they're not all resolved - will pop up a dialog
 * prompting the user to action.
 * 
 * The only thing you may want to do in order to provide best user experience is
 * to prompt for the installation of DependencyManager if binding failed. To do
 * so, just query the bindResult() function.
 **/
public class TestAppActivity extends DMActivity
{
  /***************************************************************************
   * Private constants
   **/
  // Log ID
  private static final String LTAG = "TestAppActivity";

  // Dialog IDs
  private static int DIALOG_INSTALL_DM = 0;



  /***************************************************************************
   * Implementation
   **/
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button b = (Button) findViewById(R.id.show_dialog);
    if (null != b) {
      b.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v)
          {
            resolveDependencies();
          }
      });
    }
  }



  @Override
  public void onStart()
  {
    super.onStart();
    if (!bindResult()) {
      showDialog(DIALOG_INSTALL_DM);
    }
  }



  @Override
  protected Dialog onCreateDialog(int id)
  {
    if (DIALOG_INSTALL_DM != id) {
      return null;
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("DependencyManager is not installed.")
      .setCancelable(false).setPositiveButton("Install now",
          new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // Try only the first Uri.
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(DependencyManagerContract.INSTALL_URIS[0]);
                try {
                  startActivity(i);
                } catch (ActivityNotFoundException e) {
                  // FIXME 
                  // What to do if no market installed?
                }
              }
          }
      );
    return builder.create();
  }
}
