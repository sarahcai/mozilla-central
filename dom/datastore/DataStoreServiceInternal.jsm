/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

"use strict"

const {classes: Cc, interfaces: Ci, utils: Cu, results: Cr} = Components;

this.EXPORTED_SYMBOLS = ["DataStoreServiceInternal"];

function debug(s) {
  // dump('DEBUG DataStoreServiceInternal: ' + s + '\n');
}

Cu.import("resource://gre/modules/XPCOMUtils.jsm");

XPCOMUtils.defineLazyServiceGetter(this, "ppmm",
                                   "@mozilla.org/parentprocessmessagemanager;1",
                                   "nsIMessageBroadcaster");

XPCOMUtils.defineLazyServiceGetter(this, "dataStoreService",
                                   "@mozilla.org/datastore-service;1",
                                   "nsIDataStoreService");

this.DataStoreServiceInternal = {
  init: function() {
    debug("init");

    let inParent = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime)
                      .processType == Ci.nsIXULRuntime.PROCESS_TYPE_DEFAULT;
    if (inParent) {
      ppmm.addMessageListener("DataStore:Get", this);
    }
  },

  receiveMessage: function(aMessage) {
    debug("receiveMessage");

    if (aMessage.name != 'DataStore:Get') {
      return;
    }

    let msg = aMessage.data;

    // This is a security issue and it will be fixed by Bug 916091
    msg.stores = dataStoreService.getDataStoresInfo(msg.name, msg.appId);
    aMessage.target.sendAsyncMessage("DataStore:Get:Return", msg);
  }
}

DataStoreServiceInternal.init();
