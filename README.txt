
1. Homepage customiztion for Chinese edition
	Default sites are hot sites instead of most visited.

	Major Related files: 
		mobile/android/base/Makefile.in mobile/android/base/cmhomepage-assets/default-pinned-sites.json
		toolkit/mozapps/installer/packager.mk
		mobile/android/base/db/BrowserProvider.java.in
		mobile/android/base/AboutHomeContent.java

2. Search Engine
	Setting Baidu as default search engine.
	Adding Taobao search url for mobile phone/tablet.

	Major Related files: 
		toolkit/components/search/nsSearchServices.js
		mobile/locales/zh-CN/searchplugins/*

3. Default Bookmarks
	Recommending different bookmarks for mobile phone/tablet.

	Major Related files: 
		mobile/android/base/db/BrowserProvider.java.in

4. Proxy
	Adding proxy item on the setting menu.

	Major Related files: 
		mobile/android/base/GeckoPreferences.java
		mobile/android/base/resources/xml/preferences_customize.xml
		mobile/android/base/resources/xml-v11/preferences_proxy.xml

5. Search tab for awesomebar
	Pinning search tab as default tab instead of topsites tab.
	Search hints and history are showed on this page.

	Major Related files: 
		mobile/android/base/awesomebar/AllPagesTab.java

6. Chinese navigation tab
	Pinning navigation tab to awesomebar.
	Major Related files: 	
		mobile/android/base/awesomebar/CnTopsitesTab.java

7. No Image Mode
	Introducing the "No image mode" to enable users to read page without loading images by changing the preference "permissions.default.image"

	Major Related files: 
		mobile/android/base/BrowserApp.java

8. Webkit CSS Support
	Support for CSS styles with -webkit- prefix 

	Major Related files: 
		layout/style/nsCSSKeywordList.h 
		layout/style/nsCSSPropAliasList.h 
		layout/style/nsCSSProps.cpp

9. Quit Button
	The quit button is always showed on the menu

	files: 
		mobile/android/base/BrowserApp.java

10. Reader Mode
	Introducing "reader mode" to modify the page more comfortable when users come across ebook or related pages
	The reader icon is showed on the URL bar.
	
	
	Major Related files: 
		mobile/android/chrome/content/Readability.js
		mobile/android/chrome/content/browser.js

11. Geolocation service
	Baidu geolocation service takes the place of Google service.
	
	Major Related files: 
		mobile/android/base/AndroidManifest.xml.in
		mobile/android/base/GeckoApp.java
		mobile/android/base/GeckoAppShell.java
		mobile/android/base/baidu-geolocation-libs/*

12. Barcode Scanner
	Introducing barcode scanner by using zxing code
	The sacnner icon is adding on the menu.

	Major Related files: 
		mobile/android/base/locales/zh-CN/zxing_strings.dtd
		mobile/android/base/resources/layout/capture.xml
		mobile/android/base/AndroidManifest.xml.in
		mobile/android/base/zxing/*
		mobile/android/base/BrowserApp.java




An explanation of the Mozilla Source Code Directory Structure and links to
project pages with documentation can be found at:

    https://developer.mozilla.org/en/Mozilla_Source_Code_Directory_Structure

For information on how to build Mozilla from the source code, see:

    http://developer.mozilla.org/en/docs/Build_Documentation

To have your bug fix / feature added to Mozilla, you should create a patch and
submit it to Bugzilla (https://bugzilla.mozilla.org). Instructions are at:

    http://developer.mozilla.org/en/docs/Creating_a_patch
    http://developer.mozilla.org/en/docs/Getting_your_patch_in_the_tree

If you have a question about developing Mozilla, and can't find the solution
on http://developer.mozilla.org, you can try asking your question in a
mozilla.* Usenet group, or on IRC at irc.mozilla.org. [The Mozilla news groups
are accessible on Google Groups, or news.mozilla.org with a NNTP reader.]

You can download nightly development builds from the Mozilla FTP server.
Keep in mind that nightly builds, which are used by Mozilla developers for
testing, may be buggy. Firefox nightlies, for example, can be found at:

    ftp://ftp.mozilla.org/pub/firefox/nightly/latest-trunk/
            - or -
    http://nightly.mozilla.org/
